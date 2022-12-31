package gitlet;

import java.io.File;
import java.io.Serializable;
import java.util.TreeMap;

public class Blob implements Serializable {
    // HashMap is non-deterministic in order, which may cause trouble when doing checksum
    // {sha1: commit_sha/file_path}
    private TreeMap<String, String> allFiles = new TreeMap<>();

    public void addFile(String sha, String path) {
        allFiles.put(sha, path);
    }

    public boolean checkSum(Blob blob) {
        if (Utils.sha1(blob).equals(Utils.sha1(this))) {
            return true;
        }
        return false;
    }
    public static Blob readBlob() {
        return Utils.readObject(Info.BLOB_FILE, Blob.class);
    }
    public boolean writeBlob() {
        Utils.writeObject(Info.BLOB_FILE, this);
        return true;
    }
    public TreeMap<String, String> getAllFiles(){
        return allFiles;
    }
}
