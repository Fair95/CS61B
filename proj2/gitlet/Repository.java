package gitlet;

import java.io.IOException;
import java.io.Serializable;
import java.util.*;


/**
 * Represents a gitlet repository.
 *  TODO: It's a good idea to give a description here of what else this Class
 *  does at a high level.
 *
 * @author Qianyi Li
 */
public class Repository implements Serializable {
    /**
     * TODO: add instance variables here.
     * <p>
     * List all instance variables of the Repository class here with a useful
     * comment above them describing what that variable represents and how that
     * variable is used. We've provided two examples for you.
     */
    private boolean isInit = false;
    private Map<String, Branch> branches = new TreeMap<>();
    private Branch cur;
    private StagingArea SA;

    public void setUp() {
        if (Info.GITLET_DIR.isDirectory()) {
            Utils.exit("A Gitlet version-control system already exists in the current directory.");
        }

        if (!Info.GITLET_DIR.mkdir()) {
            Utils.exit("Error creating gitlet dir.");
        }

        if (!Info.OBJ_DIR.mkdir()) {
            Utils.exit("Error creating objects dir.");
        }

        if (!Info.STAGING_DIR.mkdir()) {
            Utils.exit("Error creating staging dir.");
        }

        if (!Info.COMMIT_DIR.mkdir()) {
            Utils.exit("Error creating commits dir.");
        }

        Blob blob = new Blob();
        if (!blob.writeBlob()) {
            Utils.exit("Error writing initial blob file.");
        }

        cur = new Branch();
        // Initially the new commit would have the same file tree as its parent
        // Staging area will be the initial state of the new commit to be committed
        SA = new StagingArea(cur.HEAD);
        branches.put(cur.name, cur);
        isInit = true;
        saveState();
    }

    public boolean isInit() {
        return isInit;
    }

    public Map<String, Branch> getBranches() {
        return branches;
    }

    public void saveState() {
        Utils.writeObject(Info.REPO, this);
    }

    public static Repository getState() {
        if (!Info.REPO.isFile()) {
            Utils.exit("Not in an initialized Gitlet directory.");
        }
        return Utils.readObject(Info.REPO, Repository.class);
    }

    public void add(String file_path) {
        SA.add(file_path);
        saveState();
    }

    public void commit(String msg) {
        cur.HEAD = cur.commit(msg, SA);
        SA.clean();
        SA = new StagingArea(cur.HEAD);
        saveState();
    }

    public void mergeCommit(String msg, String secondParentSha) {
        cur.HEAD = cur.mergeCommit(msg, SA, secondParentSha);
        SA.clean();
        SA = new StagingArea(cur.HEAD);
        saveState();
    }

    public void rm(String file_path) {
        SA.remove(file_path, cur.HEAD);
        saveState();
    }

    public void log() {
        Commit c = cur.HEAD;
        String parentSha = c.getParentSha();
        while (!parentSha.isEmpty()) {
            c.printCommit();
            c = Commit.readCommit(parentSha);
            parentSha = c.getParentSha();
        }
        c.printCommit();
    }

    public void global_log() {
        List<String> allCommitsDir = Utils.subDirnamesIn(Info.COMMIT_DIR);
        List<String> allCommits;
        Commit c;
        assert allCommitsDir != null;
        for (String dir : allCommitsDir) {
            allCommits = Utils.plainFilenamesIn(Utils.join(Info.COMMIT_DIR, dir));
            assert allCommits != null;
            for (String s : allCommits) {
                c = Commit.readCommit(dir + s);
                c.printCommit();
            }
        }
//        List<String> allCommits = Utils.plainFilenamesIn(Info.COMMIT_DIR);
//        Commit c;
//        assert allCommits != null;
//        for (String s : allCommits) {
//            c = Commit.readCommit(s);
//            c.printCommit();
//        }
    }

    public void find(String msg) {
        List<String> allCommitsDir = Utils.subDirnamesIn(Info.COMMIT_DIR);
        StringBuilder found = new StringBuilder();
        List<String> allCommits;
        Commit c;
        assert allCommitsDir != null;
        for (String dir : allCommitsDir) {
            allCommits = Utils.plainFilenamesIn(Utils.join(Info.COMMIT_DIR, dir));
            assert allCommits != null;
            for (String s : allCommits) {
                c = Commit.readCommit(dir + s);
                if (c.getMsg().equals(msg)) {
                    found.append(c.generateSha()).append("\n");
                }
            }
        }
        if (found.length() == 0) {
            Utils.exit("Found no commit with that message.");
        } else {
            System.out.print(found);
        }
    }

    public void status() {
        StringBuilder statusBuilder = new StringBuilder();
        // branches
        statusBuilder.append("=== Branches ===").append("\n");
        for (String name : branches.keySet()) {
            if (cur.name.equals(name)) {
                statusBuilder.append("*");
            }
            statusBuilder.append(name).append("\n");
        }
        statusBuilder.append("\n");

        // staged files
        statusBuilder.append("=== Staged Files ===").append("\n");
        TreeMap<String, String> stagedFiles = SA.getStagedFiles();
        for (String filename : stagedFiles.keySet()) {
            if (Utils.isInStagingArea(filename)) {
                statusBuilder.append(filename).append("\n");
            }
        }
        statusBuilder.append("\n");

        // removed files
        statusBuilder.append("=== Removed Files ===").append("\n");
        TreeSet<String> removedFiles = SA.getRemovedFiles();
        for (String s : removedFiles) {
            statusBuilder.append(s).append("\n");
        }
        statusBuilder.append("\n");

        // modifications not staged for commit
        statusBuilder.append("=== Modifications Not Staged For Commit ===").append("\n");
        // Tracked in the current commit, changed in the working directory, but not staged

        statusBuilder.append("\n");

        // untracked files
        statusBuilder.append("=== Untracked Files ===").append("\n");

        statusBuilder.append("\n");

        // print
        System.out.print(statusBuilder);
    }


    public void checkoutFile(String filename) {
        TreeMap<String, String> files = cur.HEAD.getFiles();
        if (!files.containsKey(filename)) {
            Utils.exit("File does not exist in that commit.");
        }
        Utils.writeObjToCWD(files.get(filename), filename);
        saveState();
    }

    public void checkoutFileInCommit(String commitSha, String filename) {
        Commit c = Utils.findCommit(commitSha);
        TreeMap<String, String> files = c.getFiles();
        if (!files.containsKey(filename)) {
            Utils.exit("File does not exist in that commit.");
        }
        Utils.writeObjToCWD(files.get(filename), filename);
        saveState();
    }

    public void checkoutBranch(String branchName) {
        if (!branches.containsKey(branchName)) {
            System.out.println("No such branch exists.");
            return;
        }
        if (branchName.equals(cur.name)) {
            System.out.println("No need to checkout the current branch.");
            return;
        }
        Branch next = branches.get(branchName);
        TreeMap<String, String> curFiles = cur.HEAD.getFiles();
        TreeMap<String, String> nextFiles = next.HEAD.getFiles();
        Utils.addFileFromNextState(nextFiles, curFiles);
        Utils.deleteFileFromCurState(nextFiles, curFiles);
        cur = next;
        SA.clean();
        SA = new StagingArea(cur.HEAD);
        saveState();
    }

    public void makeBranch(String name) {
        if (branches.containsKey(name)) {
            Utils.exit("A branch with that name already exists.");
        }
        Branch b = new Branch(name, cur.HEAD, cur.history);
        branches.put(b.name, b);
        saveState();
    }

    public void deleteBranch(String name) {
        if (!branches.containsKey(name)) {
            Utils.exit("A branch with that name does not exist.");
        }
        if (cur.name.equals(name)) {
            Utils.exit("Cannot remove the current branch.");
        }
        branches.remove(name);
        saveState();
    }

    public void reset(String commitSha) {
        Commit next = Utils.findCommit(commitSha);
        TreeMap<String, String> nextFiles = next.getFiles();
        TreeMap<String, String> curFiles = cur.HEAD.getFiles();

        Utils.addFileFromNextState(nextFiles, curFiles);
        Utils.deleteFileFromCurState(nextFiles, curFiles);

        cur.HEAD = next;
        SA.clean();
        SA = new StagingArea(cur.HEAD);
        saveState();
    }

    public void merge(String branchName) {
        if (!Utils.plainFilenamesIn(Info.STAGING_DIR).isEmpty() || !SA.getRemovedFiles().isEmpty()) {
            Utils.exit("You have uncommitted changes.");
        }
        if (!branches.containsKey(branchName)) {
            Utils.exit("A branch with that name does not exist.");
        }
        if (branchName.equals(cur.name)) {
            Utils.exit("Cannot merge a branch with itself.");
        }
        Commit target = branches.get(branchName).HEAD;
        Commit ancestor = Utils.getLatestCommonAncestor(cur.HEAD, target);
        boolean conflict = false;
        if (ancestor.equals(target)) {
            System.out.println("Given branch is an ancestor of the current branch.");
        } else if (ancestor.equals(cur.HEAD)) {
            checkoutBranch(branchName);
            System.out.println("Current branch fast-forwarded.");
        } else {
            TreeMap<String, String> targetFiles = target.getFiles();
            TreeMap<String, String> curFiles = cur.HEAD.getFiles();
            TreeMap<String, String> ancestorFiles = ancestor.getFiles();
            HashSet<String> allFiles = new HashSet<>();
            allFiles.addAll(targetFiles.keySet());
            allFiles.addAll(curFiles.keySet());
            allFiles.addAll(ancestorFiles.keySet());
            for (String file : allFiles) {
                String targetFileSha;
                String curFileSha;
                String ancestorFileSha;
                // file exists in ancestor
                if (ancestorFiles.containsKey(file)) {
                    ancestorFileSha = ancestorFiles.get(file);
                    if (curFiles.containsKey(file) && !targetFiles.containsKey(file)) {
                        curFileSha = curFiles.get(file);
                        if (curFileSha.equals(ancestorFileSha)) {
                            // 6. Unmodified in current branch but deleted in target branch
                            SA.remove(file, cur.HEAD);
                        } else {
                            // 8. modified in current branch and deleted in target branchy
                            Utils.writeConflictMsg(file, curFileSha, null);
                            SA.add(file);
                            conflict = true;
                        }
                    } else if (!curFiles.containsKey(file) && targetFiles.containsKey(file)) {
                        targetFileSha = targetFiles.get(file);
                        if (targetFileSha.equals(ancestorFileSha)) {
                            // 7. Unmodified in target branch but deleted in current branch
                            // Do nothing
                        } else {
                            // 8. modified in target branch and deleted in current branch
                            Utils.writeConflictMsg(file, null, targetFileSha);
                            SA.add(file);
                            conflict = true;
                        }

                    } else if (targetFiles.containsKey(file) && curFiles.containsKey(file)) {
                        // file exists in all 3 commits
                        targetFileSha = targetFiles.get(file);
                        curFileSha = curFiles.get(file);
                        ancestorFileSha = ancestorFiles.get(file);
                        if (!curFileSha.equals(targetFileSha) && curFileSha.equals(ancestorFileSha)) {
                            // 1. only modified in the target branch
                            if (Utils.isInCWD(file)) {
                                Utils.exit("There is an untracked file in the way; delete it, or add and commit it first.");
                            }
                            Utils.writeObjToCWD(targetFileSha, file);
                            SA.add(file);
                        } else if (!curFileSha.equals(targetFileSha) && targetFileSha.equals(ancestorFileSha)) {
                            // 2. only modified in the current branch
                            // Do nothing
                        } else if (curFileSha.equals(targetFileSha) && !curFileSha.equals(ancestorFileSha)) {
                            // 3. modified in both target and current branch but in the same way
                            // Do nothing
                        } else if (!curFileSha.equals(targetFileSha)) {
                            // 8. modified in both target and current branch but in different way
                            Utils.writeConflictMsg(file, curFileSha, targetFileSha);
                            SA.add(file);
                            conflict = true;
                        } else {
                            // all files the same
                        }
                    } else {
                        // 3. deleted in both target and current branch
                        // Do nothing
                    }
                } else {
                    // Not in ancestor
                    if (!targetFiles.containsKey(file)) {
                        // 4. Only present in current branch
                        // Do nothing
                    } else if (!curFiles.containsKey(file)) {
                        // 5. Only present in target branch
                        targetFileSha = targetFiles.get(file);
                        if (Utils.isInCWD(file)) {
                            Utils.exit("There is an untracked file in the way; delete it, or add and commit it first.");
                        }
                        Utils.writeObjToCWD(targetFileSha, file);
                        SA.add(file);
                    } else {
                        // Both has the file
                        targetFileSha = targetFiles.get(file);
                        curFileSha = curFiles.get(file);
                        if (!targetFileSha.equals(curFileSha)) {
                            // 8. modified in both target and current branch but in different way
                            Utils.writeConflictMsg(file, curFileSha, targetFileSha);
                            SA.add(file);
                            conflict = true;
                        }
                    }
                }
            }
            String msg = String.format("Merged %s into %s.", branchName, cur.name);
            mergeCommit(msg, target.generateSha());
            if (conflict) {
                System.out.println("Encountered a merge conflict.");
            }
        }
        saveState();
    }
}
