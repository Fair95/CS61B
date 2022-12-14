package gitlet;


import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;


/**
 * Assorted utilities.
 * <p>
 * Give this file a good read as it provides several useful utility functions
 * to save you some time.
 *
 * @author P. N. Hilfinger
 */
class Utils {

    /**
     * The length of a complete SHA-1 UID as a hexadecimal numeral.
     */
    static final int UID_LENGTH = 40;

    /* SHA-1 HASH VALUES. */

    /**
     * Returns the SHA-1 hash of the concatenation of VALS, which may
     * be any mixture of byte arrays and Strings.
     */
    static String sha1(Object... vals) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-1");
            for (Object val : vals) {
                if (val instanceof byte[]) {
                    md.update((byte[]) val);
                } else if (val instanceof String) {
                    md.update(((String) val).getBytes(StandardCharsets.UTF_8));
                } else {
                    throw new IllegalArgumentException("improper type to sha1");
                }
            }
            Formatter result = new Formatter();
            for (byte b : md.digest()) {
                result.format("%02x", b);
            }
            return result.toString();
        } catch (NoSuchAlgorithmException excp) {
            throw new IllegalArgumentException("System does not support SHA-1");
        }
    }

    /**
     * Returns the SHA-1 hash of the concatenation of the strings in
     * VALS.
     */
    static String sha1(List<Object> vals) {
        return sha1(vals.toArray(new Object[vals.size()]));
    }

    /* FILE DELETION */

    /**
     * Deletes FILE if it exists and is not a directory.  Returns true
     * if FILE was deleted, and false otherwise.  Refuses to delete FILE
     * and throws IllegalArgumentException unless the directory designated by
     * FILE also contains a directory named .gitlet.
     */
    static boolean restrictedDelete(File file) {
        if (!(new File(file.getParentFile(), ".gitlet")).isDirectory()) {
            throw new IllegalArgumentException("not .gitlet working directory");
        }
        if (!file.isDirectory()) {
            return file.delete();
        } else {
            return false;
        }
    }

    /**
     * Deletes the file named FILE if it exists and is not a directory.
     * Returns true if FILE was deleted, and false otherwise.  Refuses
     * to delete FILE and throws IllegalArgumentException unless the
     * directory designated by FILE also contains a directory named .gitlet.
     */
    static boolean restrictedDelete(String file) {
        return restrictedDelete(new File(file));
    }

    /* READING AND WRITING FILE CONTENTS */

    /**
     * Return the entire contents of FILE as a byte array.  FILE must
     * be a normal file.  Throws IllegalArgumentException
     * in case of problems.
     */
    static byte[] readContents(File file) {
        if (!file.isFile()) {
            throw new IllegalArgumentException("must be a normal file");
        }
        try {
            return Files.readAllBytes(file.toPath());
        } catch (IOException excp) {
            throw new IllegalArgumentException(excp.getMessage());
        }
    }

    /**
     * Return the entire contents of FILE as a String.  FILE must
     * be a normal file.  Throws IllegalArgumentException
     * in case of problems.
     */
    static String readContentsAsString(File file) {
        return new String(readContents(file), StandardCharsets.UTF_8);
    }

    /**
     * Write the result of concatenating the bytes in CONTENTS to FILE,
     * creating or overwriting it as needed.  Each object in CONTENTS may be
     * either a String or a byte array.  Throws IllegalArgumentException
     * in case of problems.
     */
    static void writeContents(File file, Object... contents) {
        try {
            if (file.isDirectory()) {
                throw
                    new IllegalArgumentException("cannot overwrite directory");
            }
            BufferedOutputStream str =
                new BufferedOutputStream(Files.newOutputStream(file.toPath()));
            for (Object obj : contents) {
                if (obj instanceof byte[]) {
                    str.write((byte[]) obj);
                } else {
                    str.write(((String) obj).getBytes(StandardCharsets.UTF_8));
                }
            }
            str.close();
        } catch (IOException | ClassCastException excp) {
            throw new IllegalArgumentException(excp.getMessage());
        }
    }

    /**
     * Return an object of type T read from FILE, casting it to EXPECTEDCLASS.
     * Throws IllegalArgumentException in case of problems.
     */
    static <T extends Serializable> T readObject(File file,
                                                 Class<T> expectedClass) {
        try {
            ObjectInputStream in =
                new ObjectInputStream(new FileInputStream(file));
            T result = expectedClass.cast(in.readObject());
            in.close();
            return result;
        } catch (IOException | ClassCastException
                 | ClassNotFoundException excp) {
            throw new IllegalArgumentException(excp.getMessage());
        }
    }

    /**
     * Write OBJ to FILE.
     */
    static void writeObject(File file, Serializable obj) {
        writeContents(file, serialize(obj));
    }

    /* DIRECTORIES */

    /**
     * Filter out all but plain files.
     */
    private static final FilenameFilter PLAIN_FILES =
        new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                return new File(dir, name).isFile();
            }
        };

    /**
     * Returns a list of the names of all plain files in the directory DIR, in
     * lexicographic order as Java Strings.  Returns null if DIR does
     * not denote a directory.
     */
    static List<String> plainFilenamesIn(File dir) {
        String[] files = dir.list(PLAIN_FILES);
        if (files == null) {
            return null;
        } else {
            Arrays.sort(files);
            return Arrays.asList(files);
        }
    }

    /**
     * Returns a list of the names of all plain files in the directory DIR, in
     * lexicographic order as Java Strings.  Returns null if DIR does
     * not denote a directory.
     */
    static List<String> plainFilenamesIn(String dir) {
        return plainFilenamesIn(new File(dir));
    }

    /* OTHER FILE UTILITIES */

    /**
     * Return the concatentation of FIRST and OTHERS into a File designator,
     * analogous to the {@link java.nio.file.Paths.#get(String, String[])}
     * method.
     */
    static File join(String first, String... others) {
        return Paths.get(first, others).toFile();
    }

    /**
     * Return the concatentation of FIRST and OTHERS into a File designator,
     * analogous to the {@link java.nio.file.Paths.#get(String, String[])}
     * method.
     */
    static File join(File first, String... others) {
        return Paths.get(first.getPath(), others).toFile();
    }


    /* SERIALIZATION UTILITIES */

    /**
     * Returns a byte array containing the serialized contents of OBJ.
     */
    static byte[] serialize(Serializable obj) {
        try {
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            ObjectOutputStream objectStream = new ObjectOutputStream(stream);
            objectStream.writeObject(obj);
            objectStream.close();
            return stream.toByteArray();
        } catch (IOException excp) {
            throw error("Internal error serializing commit.");
        }
    }



    /* MESSAGES AND ERROR REPORTING */

    /**
     * Return a GitletException whose message is composed from MSG and ARGS as
     * for the String.format method.
     */
    static GitletException error(String msg, Object... args) {
        return new GitletException(String.format(msg, args));
    }

    /**
     * Print a message composed from MSG and ARGS as for the String.format
     * method, followed by a newline.
     */
    static void message(String msg, Object... args) {
        System.out.printf(msg, args);
        System.out.println();
    }

    /**
     * Added from lab6 to support exit
     *
     * @param message error message
     */
    public static void exitWithError(String message) {
        if (message != null && !message.equals("")) {
            System.out.println(message);
        }
        System.exit(0);
    }

    /**
     * Check if the number of args provided is valid, if not print error meg and exit
     *
     * @param args the user supplied args
     * @param n    the desired number of args
     */
    public static void validateNumArgs(String[] args, int n) {
        if (args.length != n) {
            Utils.exitWithError("Incorrect operands.");
        }
    }

    public static String encodeFile(String path) {
        File f = new File(path);
        byte[] content = readContents(f);
        return sha1(path, content);
    }

    public static void writeFile(File src, File dest) {
        String content = readContentsAsString(src);
        writeContents(dest, content);
    }

    private static final FilenameFilter SUBDIR =
        new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                return new File(dir, name).isDirectory();
            }
        };

    static List<String> subDirnamesIn(File dir) {
        String[] files = dir.list(SUBDIR);
        if (files == null) {
            return null;
        } else {
            Arrays.sort(files);
            return Arrays.asList(files);
        }
    }

    public static void writeObjToCWD(String src, String dest) {
        writeFile(join(Info.OBJ_DIR, src), join(Info.CWD, dest));
    }

    public static void deleteFileFromCWD(String filename) {
        File f = join(Info.CWD, filename);
        if (f.isFile()) {
            f.delete();
        }
    }

    public static boolean isInCWD(String filename) {
        return join(Info.CWD, filename).isFile();
    }

    public static boolean isInStagingArea(String filename) {
        return join(Info.STAGING_DIR, filename).isFile();
    }

    public static Commit findCommit(String commitID) {
        Commit c;
        if (commitID.length() < 3) {
            exit("Too short commit ID");
        }
        File commitDir = Utils.join(Info.COMMIT_DIR, commitID.substring(0, 2));
        if (!commitDir.isDirectory()) {
            exit("No commit with that id exists.");
        }
        for (String s : Objects.requireNonNull(plainFilenamesIn(commitDir))) {
            if (s.startsWith(commitID.substring(2))) {
                c = Commit.readCommit(commitID.substring(0, 2) + s);
                return c;
            }
        }
        exit("No commit with that id exists.");
        return null;
    }

    public static void addFileFromNextState(TreeMap<String, String> nextFiles, TreeMap<String, String> curFiles) {
        for (String filename : nextFiles.keySet()) {
            if (!curFiles.containsKey(filename) && Utils.join(Info.CWD, filename).isFile()) {
                exit("There is an untracked file in the way; delete it, or add and commit it first.");
            }
            writeObjToCWD(nextFiles.get(filename), filename);
        }
    }

    public static void deleteFileFromCurState(TreeMap<String, String> nextFiles, TreeMap<String, String> curFiles) {
        for (String filename : curFiles.keySet()) {
            if (!nextFiles.containsKey(filename)) {
                deleteFileFromCWD(filename);
            }
        }
    }

    public static String formatTime(ZonedDateTime t) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEE MMM d HH:mm:ss yyyy Z");
        return t.format(formatter);
    }

//    public static Commit getLatestCommonAncestor(Commit commit1, Commit commit2){
//        // We assume the time always flows forwards (The law of physics!)
//        Comparator<Commit> cmp = Comparator.comparing(Commit::getTime);
//        PriorityQueue<Commit> pq = new PriorityQueue<>();
//        HashSet<String> commitHashSet;
//        while (!commit1.equals(commit2)) {
//            while (cmp.compare(commit1, commit2) != 0) {
//                // commit1 is generated earlier
//                if (cmp.compare(commit1, commit2) < 0) {
//                    commit2 = commit2.getParentCommit();
//                } else {
//                    // commit2 is generated earlier
//                    commit1 = commit1.getParentCommit();
//                }
//            }
//            // Boarder case:
//            // commit has the same generation time, but not the same commit
//            // Try to find the common latest ancestor until we found or the ancestors are of different time
//            if (!commit1.equals(commit2)){
//                commitHashSet = new HashSet<>();
//                commitHashSet.add(commit1.generateSha());
//                commitHashSet.add(commit2.generateSha());
//                String commit1AncestorSha = commit1.getParentSha();
//                Commit commit1Ancestor = Commit.readCommit(commit1AncestorSha);
//                // Loop through ancestors of commit1
//                while (cmp.compare(commit1, commit1Ancestor) == 0){
//                    // Still same generation time, check if common, otherwise go one level up further
//                    if (commitHashSet.contains(commit1AncestorSha)) {
//                        return commit1Ancestor;
//                    } else {
//                        commitHashSet.add(commit1AncestorSha);
//                        commit1AncestorSha = commit1Ancestor.getParentSha();
//                        commit1Ancestor = Commit.readCommit(commit1AncestorSha);
//                    }
//                }
//                String commit2AncestorSha = commit2.getParentSha();
//                Commit commit2Ancestor = Commit.readCommit(commit2AncestorSha);
//                // Loop through ancestors of commit1
//                while (cmp.compare(commit2, commit2Ancestor) == 0){
//                    if (commitHashSet.contains(commit2AncestorSha)) {
//                        return commit2Ancestor;
//                    } else {
//                        commitHashSet.add(commit2AncestorSha);
//                        commit2AncestorSha = commit2Ancestor.getParentSha();
//                        commit2Ancestor = Commit.readCommit(commit2AncestorSha);
//                    }
//                }
//                // Not found, start from their respective ancestors
//                commit1 = commit1Ancestor;
//                commit2 = commit2Ancestor;
//            }
//        }
//        return commit1;
//    }
    public static Commit getLatestCommonAncestor(Commit commit1, Commit commit2){
        // We assume the time always flows forwards (The law of physics!)
        Comparator<Commit> cmp = Comparator.comparing(Commit::getTime).reversed();
        PriorityQueue<Commit> pq = new PriorityQueue<>(cmp);
        HashSet<String> commitHashSet = new HashSet<>();
        pq.add(commit1);
        pq.add(commit2);
        String lastCommitSha;
        Commit lastCommit;
        String lastCommitParentSha;
        Commit lastCommitParent;
        String lastCommitSecondParentSha;
        Commit lastCommitSecondParent;

        while (!pq.isEmpty()){
            lastCommit = pq.poll();
            lastCommitSha = lastCommit.generateSha();
            if (commitHashSet.contains(lastCommitSha)){
                return lastCommit;
            } else {
                commitHashSet.add(lastCommitSha);
                lastCommitParentSha = lastCommit.getParentSha();
                lastCommitSecondParentSha = lastCommit.getSecondParentSha();
                if (!lastCommitParentSha.isEmpty()) {
                    lastCommitParent = Commit.readCommit(lastCommitParentSha);
                    pq.add(lastCommitParent);
                    if (!lastCommitSecondParentSha.isEmpty()) {
                        lastCommitSecondParent = Commit.readCommit(lastCommitSecondParentSha);
                        pq.add(lastCommitSecondParent);
                    }
                }
            }
        }
        System.out.println("Common Ancestor not Found, something wrong.");
        return commit1;
    }

    public static void writeConflictMsg(String cwdFile, String curFile, String targetFile){
        StringBuilder builder = new StringBuilder();
        builder.append("<<<<<<< HEAD\n");
        if (curFile!=null) {
            builder.append(readContentsAsString(join(Info.OBJ_DIR, curFile)));
        }
        builder.append("=======\n");
        if (targetFile!=null) {
            builder.append(readContentsAsString(join(Info.OBJ_DIR, targetFile)));
        }
        builder.append(">>>>>>>\n");
        writeContents(join(Info.CWD, cwdFile), builder.toString());
    }

    public static void exit(String message, Object... args) {
        message(message, args);
        System.exit(0);
    }

}
