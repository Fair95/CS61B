package gitlet;

import java.io.File;

import static gitlet.Utils.join;

public class Info {
    /**
     * The current working directory.
     */
    public static final File CWD = new File(System.getProperty("user.dir"));
    /**
     * The .gitlet directory.
     */
    public static final File GITLET_DIR = join(CWD, ".gitlet");
    /**
     * The blob file.
     */
    public static final File BLOB_FILE = join(GITLET_DIR, "blob");
    /**
     * The staging directory.
     */
    public static final File STAGING_DIR = join(GITLET_DIR, "staging");
    public static final File OBJ_DIR = join(GITLET_DIR, "objects");
    public static final File COMMIT_DIR = join(GITLET_DIR, "commits");
    public static final File REPO = join(GITLET_DIR, "repo");
}
