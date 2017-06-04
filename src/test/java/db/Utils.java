package db;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;

/**
 * Contains utility methods for creating and deleting files..
 */
class Utils {

    /**
     * Utility method for deleting a file created for testing purposes.
     *
     * @throws IllegalArgumentException if the path was not a recognized file
     * @throws Exception if the file could not be deleted.
     */
    static void deleteFile(File file, String testName) throws Exception {
        if (!file.exists())
            throw new IllegalArgumentException("No such file");

        boolean deleted = file.delete();
        if (!deleted) {
            throw new Exception("Warning: file created for " + testName + " at location " + file.getAbsolutePath() +
                    " could not be deleted");
        }
    }

    /**
     * Deletes the file at the given path.
     *
     * @throws IllegalArgumentException if the path was not a recognized file.
     * @throws Exception if the file could not be deleted.
     */
    static void deleteFile(String path, String testName) throws Exception {

        File file = new File(path);
        if (!file.exists())
            throw new IllegalArgumentException("No file found at " + path);

        deleteFile(file, testName);
    }

    /**
     * Creates a new file at the given path.
     *
     * @param path location to save the file. Note, this only creates the file, the directories must already be created.
     * @param contents contents of file.
     *
     * @throws IllegalArgumentException if a file at the given path already exists.
     */
    static File createFile(String path, String contents) throws Exception {
        File file = new File(path);

        if (file.exists()) {
            throw new IllegalArgumentException("File at " + path + " already exists");
        }

        //noinspection ResultOfMethodCallIgnored
        file.createNewFile();

        FileOutputStream writer = new FileOutputStream(file);
        writer.write(contents.getBytes());

        return file;
    }
}
