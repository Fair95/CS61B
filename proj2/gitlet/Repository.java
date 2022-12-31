package gitlet;

import java.io.IOException;
import java.io.Serializable;
import java.util.*;


/** Represents a gitlet repository.
 *  TODO: It's a good idea to give a description here of what else this Class
 *  does at a high level.
 *
 *  @author Qianyi Li
 */
public class Repository implements Serializable {
    /**
     * TODO: add instance variables here.
     *
     * List all instance variables of the Repository class here with a useful
     * comment above them describing what that variable represents and how that
     * variable is used. We've provided two examples for you.
     */
    private boolean isInit = false;
    private Map<String, Branch> branches = new HashMap<>();
    private Branch cur;
    private StagingArea SA;
    public void setUp() {
        if (Info.GITLET_DIR.isDirectory()) {
            throw new GitletException("A Gitlet version-control system already exists in the current directory.");
        }

        if (!Info.GITLET_DIR.mkdir()) {
            throw new GitletException("Error creating gitlet dir.");
        }

        if(!Info.OBJ_DIR.mkdir()) {
            throw new GitletException("Error creating objects dir.");
        }

        if(!Info.STAGING_DIR.mkdir()) {
            throw new GitletException("Error creating staging dir.");
        }

        if(!Info.COMMIT_DIR.mkdir()) {
            throw new GitletException("Error creating commits dir.");
        }

        Blob blob = new Blob();
        if (!blob.writeBlob()){
            throw new GitletException("Error writing initial blob file.");
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
            throw new GitletException("Not in an initialized Gitlet directory.");
        }
        return Utils.readObject(Info.REPO, Repository.class);
    }
    public void add(String file_path) {
        SA.add(file_path, cur.HEAD);
        saveState();
    }
    public void commit(String msg) {
        cur.HEAD = cur.commit(msg, SA);
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
        while (!parentSha.isEmpty()){
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
                c = Commit.readCommit(dir+s);
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
                c = Commit.readCommit(dir+s);
                if (c.getMsg().equals(msg)) {
                    found.append(c.generateSha()).append("\n");
                }
            }
        }
        if (found.length()==0){
            throw new GitletException("Found no commit with that message.");
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
        for (String filename : stagedFiles.keySet()){
            if (Utils.isInStagingArea(filename)) {
                statusBuilder.append(filename).append("\n");
                statusBuilder.append("\n");
            }
        }

        // removed files
        statusBuilder.append("=== Removed Files ===").append("\n");
        HashSet<String> removedFiles = SA.getRemovedFiles();
        for (String s : removedFiles){
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
            throw new GitletException("File does not exist in that commit.");
        }
        Utils.writeObjToCWD(files.get(filename), filename);
        saveState();
    }

    public void checkoutFileInCommit(String commitID, String filename) {
        Commit c = Utils.findCommit(commitID);
        TreeMap<String, String> files = c.getFiles();
        if (!files.containsKey(filename)) {
            throw new GitletException("File does not exist in that commit.");
        }
        Utils.writeObjToCWD(files.get(filename), filename);
        saveState();
    }

    public void checkoutBranch(String branchName) {
        if (!branches.containsKey(branchName)){
            System.out.println("No such branch exists.");
        }
        if (branchName.equals(cur.name)){
            System.out.println("No need to checkout the current branch.");
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

    public void makeBranch(String name){
        if (branches.containsKey(name)){
            throw new GitletException("A branch with that name already exists.");
        }
        Branch b = new Branch(name, cur.HEAD, cur.history);
        branches.put(b.name, b);
        saveState();
    }
    public void deleteBranch(String name) {
        if (!branches.containsKey(name)){
            throw new GitletException("A branch with that name does not exist.");
        }
        if (cur.name.equals(name)){
            throw new GitletException("Cannot remove the current branch.");
        }
        branches.remove(name);
        saveState();
    }

    public void reset(String commitID) {
        Commit next = Utils.findCommit(commitID);
        TreeMap<String, String> nextFiles = next.getFiles();
        TreeMap<String, String> curFiles = cur.HEAD.getFiles();

        Utils.addFileFromNextState(nextFiles, curFiles);
        Utils.deleteFileFromCurState(nextFiles, curFiles);

        cur.HEAD = next;
        SA.clean();
        SA = new StagingArea(cur.HEAD);
        saveState();
    }

    public void merge(String arg) {
    }
}
