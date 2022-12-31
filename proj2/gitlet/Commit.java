package gitlet;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.*;


/**
 * Represents a gitlet commit object.
 *  TODO: It's a good idea to give a description here of what else this Class
 *  does at a high level.
 *
 * @author Qianyi Li
 */
public class Commit implements Serializable {
    /**
     * TODO: add instance variables here.
     *
     * List all instance variables of the Commit class here with a useful
     * comment above them describing what that variable represents and how that
     * variable is used. We've provided one example for `message`.
     */

    /**
     * The message of this Commit.
     */
    private String msg;
    private ZonedDateTime time;
    private String parent_sha;
    private String second_parent_sha;
    // {file_name: sha}
    private TreeMap<String, String> files;

    Commit() {
        makeInitialCommit();
    }

    Commit(String commit_parent_sha, String commit_msg, ZonedDateTime commit_time, TreeMap<String, String> commit_files) {
        parent_sha = commit_parent_sha;
        second_parent_sha = "";
        msg = commit_msg;
        time = commit_time;
        files = commit_files;
    }
    Commit(String commit_parent_sha, String commit_second_parent_sha, String commit_msg, ZonedDateTime commit_time, TreeMap<String, String> commit_files) {
        parent_sha = commit_parent_sha;
        second_parent_sha = commit_second_parent_sha;
        msg = commit_msg;
        time = commit_time;
        files = commit_files;
    }

    public static Commit readCommit(String sha) {
        File dir = Utils.join(Info.COMMIT_DIR, sha.substring(0, 2));
//        System.out.println();
        if (!dir.isDirectory()) {
//            System.out.println(dir);
            Utils.exit("Commit not exists.");
        }
        File f = Utils.join(dir, sha.substring(2));
        return Utils.readObject(f, Commit.class);
    }

    public void saveCommit() {
        String commitSha = generateSha();
        File dir = Utils.join(Info.COMMIT_DIR, commitSha.substring(0, 2));
        dir.mkdir();
        File f = Utils.join(dir, commitSha.substring(2));
        Utils.writeObject(f, this);
    }

    public String generateSha() {
        return Utils.sha1(parent_sha, time.toString(), msg, files.toString());
    }

    public String generateFileSha() {
        return Utils.sha1(files.toString());
    }

    public void makeInitialCommit() {
        parent_sha = "";
        second_parent_sha = "";
        Instant i = Instant.ofEpochSecond(0);
        ZoneId zoneid = ZonedDateTime.now().getZone();
        time = ZonedDateTime.ofInstant(i, zoneid);
        msg = "initial commit";
        files = new TreeMap<>();
    }

    public Commit makeCommit(StagingArea SA, String msg) {
        if (msg.isEmpty()) {
            Utils.exit("Please enter a commit message.");
        }
        if (SA.generateSha().equals(this.generateFileSha())) {
            // if the stagedFile is the same as HEAD tracked files (i.e. same sha) => nothing has changed
            Utils.exit("No changes added to the commit.");
        }
        // Create a new commit node with stagedFiles as tracked files
        ZonedDateTime time = ZonedDateTime.now();
        Commit c = new Commit(generateSha(), msg, time, SA.getStagedFiles());
        // Calculate the sha of new node, and store all the new files in that directory
        TreeMap<String, String> trackedFiles = c.getFiles();
        Blob blob = Blob.readBlob();
        for (String path : trackedFiles.keySet()) {
            File f = Utils.join(Info.OBJ_DIR, trackedFiles.get(path));
            // Only create new obj files if it is not ever stored
            if (!f.isFile()) {
                Utils.writeFile(Utils.join(Info.STAGING_DIR, path), f);
                // Also update the blob mapping {file_sha: file_path}
                blob.addFile(trackedFiles.get(path), path);
                blob.writeBlob();
            }
        }
        // Save new commit
        c.saveCommit();
        return c;
    }
    public Commit makeMergeCommit(StagingArea SA, String msg, String second_parent_sha){
        if (msg.isEmpty()) {
            Utils.exit("Please enter a commit message.");
        }
        if (SA.generateSha().equals(this.generateFileSha())) {
            // if the stagedFile is the same as HEAD tracked files (i.e. same sha) => nothing has changed
            Utils.exit("No changes added to the commit.");
        }
        // Create a new commit node with stagedFiles as tracked files
        ZonedDateTime time = ZonedDateTime.now();
        Commit c = new Commit(generateSha(), second_parent_sha, msg, time, SA.getStagedFiles());
        // Calculate the sha of new node, and store all the new files in that directory
        TreeMap<String, String> trackedFiles = c.getFiles();
        Blob blob = Blob.readBlob();
        for (String path : trackedFiles.keySet()) {
            File f = Utils.join(Info.OBJ_DIR, trackedFiles.get(path));
            // Only create new obj files if it is not ever stored
            if (!f.isFile()) {
                Utils.writeFile(Utils.join(Info.STAGING_DIR, path), f);
                // Also update the blob mapping {file_sha: file_path}
                blob.addFile(trackedFiles.get(path), path);
                blob.writeBlob();
            }
        }
        // Save new commit
        c.saveCommit();
        return c;
    }

    public void setParent_sha(String sha) {
        parent_sha = sha;
    }

    public void saveChanges(String dir_path) {
        for (String path : files.keySet()) {
        }
    }

    public TreeMap<String, String> getFiles() {
        return files;
    }

    public void updateFiles(TreeMap<String, String> stagedFiles) {
        files = stagedFiles;
    }

    public String getParentSha() {
        return parent_sha;
    }

    public ZonedDateTime getTime() {
        return time;
    }

    public String getMsg() {
        return msg;
    }

    public void printCommit() {
        Utils.message("===");
        Utils.message("commit %s", generateSha());
        if (!second_parent_sha.equals("")){
            Utils.message("Merge: "+ parent_sha.substring(0,7) + " "+ second_parent_sha.substring(0,7));
        }
        Utils.message("Date: %s", Utils.formatTime(getTime()));
        Utils.message("%s", msg);
        System.out.println();
    }

    public Commit getParentCommit(){
        String parentSha = getParentSha();
        if (parentSha.equals("")) {
            Utils.exit("No parent found! It is the root commit");
        }
        return Commit.readCommit(parentSha);
    }
    @Override
    public boolean equals(Object obj){
        if (this == obj) {
            return true;
        }
        if (obj instanceof Commit) {
            Commit c = (Commit) obj;
            return c.generateSha().equals(this.generateSha());
        }
        return false;
    }

}
