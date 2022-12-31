package gitlet;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.*;

public class StagingArea implements Serializable {
    // {file_path: sha}
    private TreeMap<String, String> stagedFiles;
    private TreeSet<String> removedFiles = new TreeSet<>();

    @SuppressWarnings("unchecked")
    StagingArea(Commit HEAD) {
        stagedFiles = (TreeMap<String, String>) HEAD.getFiles().clone();
    }

    public String generateSha() {
        return Utils.sha1(stagedFiles.toString());
    }
    public void add(String path) {
        File f = new File(path);
        if (!f.isFile()) {
            Utils.exit("File does not exist.");
        }
        String targetFileSha = Utils.encodeFile(path);
        Blob blob = Blob.readBlob();
        // 1. Overwrite the existing file with its new sha.
        // 2. Or simply rewrite it.
        // 3. Or we have new file, add it to the tracked map.
        stagedFiles.put(path, targetFileSha);
        // For 3. we need to do extra work by writing the file to the staging directory.
        boolean hasStoredBefore = blob.getAllFiles().containsKey(targetFileSha);
        if (!hasStoredBefore) {
            Utils.writeFile(new File(path), Utils.join(Info.STAGING_DIR, path));
        }
//        if (HEAD.getFiles().containsKey(path)) {
//            if (HEAD.getFiles().get(path).equals(targetFileSha)){
//                // Do nothing since it's in the current HEAD commit
//                return;
//            } else if (hasStoredBefore) {
//                // Stored before and same sha, so we can reuse the address (commit_sha/file_path)
//                stagedFiles.put(blob.getAllFiles().get(targetFileSha), targetFileSha);
//            } else {
//                // Stored before but different sha, so stage it for update
//                stagedFiles.put(path, targetFileSha);
//                Utils.writeFile(new File(path), Utils.join(Info.STAGING_DIR, path));
//            }
//        } else {
//            // Never stored before, stage it for addition
//            stagedFiles.put(path, targetFileSha);
//            Utils.writeFile(new File(path), Utils.join(Info.STAGING_DIR, path));
//        }
        // remove from the removal set if we add it back
        removedFiles.remove(path);
    }

    public void clean() {
        // Find all the files in the staging directory and remove them
        List<String> allFiles = Utils.plainFilenamesIn(Info.STAGING_DIR);
        for (String s : allFiles) {
            File f = Utils.join(Info.STAGING_DIR, s);
            f.delete();
        }
        // Reset the stagedFile to point to a new tree

        // Important
        // call stagedFiles.remove() during deletion of file will cause the HEAD commit contains no trackedFile
        // This is because we used stagedFiles as trackedFiles for the new commit
        // and it still points to that heap of memory
        //        stagedFiles = new TreeMap<>();
        // Well, no longer needed, as SA is reset in Repository.commit anyway
    }

    public void remove(String path, Commit HEAD) {
        // Neither staged for removal nor tracked in current HEAD commit
        if (!stagedFiles.containsKey(path) && !HEAD.getFiles().containsKey(path)) {
            Utils.exit("No reason to remove the file.");
        }
        // if staged for track for the next commit node, remove it (i.e. staged for removal)
        if (stagedFiles.containsKey(path)) {
            File f = Utils.join(Info.STAGING_DIR, path);
            f.delete();
            stagedFiles.remove(path);
        }
        // if currently tracked, try remove it from cwd if it is not removed
        if (HEAD.getFiles().containsKey(path)) {
            Utils.deleteFileFromCWD(path);
            removedFiles.add(path);
        }
    }

    public TreeMap<String, String> getStagedFiles() {
        return stagedFiles;
    }

    public TreeSet<String> getRemovedFiles() {
        return removedFiles;
    }
}
