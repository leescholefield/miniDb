package db;

import java.io.*;
import java.nio.file.FileAlreadyExistsException;

/**
 * Utility class for performing synchronized read/writes to a {@code File}.
 * <p>
 * Both reading and writing to the file are synchronized, however, synchronized reading is only a precaution. Since the
 * entire contents are read into memory and stored as a {@code JSONObject} by the {@link JsonDatabase} class, {@code read()}
 * should only be called when the program first runs. For normal database queries {@code JsonDatabase} should use its JSONObject
 * rather than re-read the file.
 * @see JsonDatabase#rootObject for further details.
 * <p>
 * Please note, when calling {@link #write(String)} this will override all existing content with the given string.
 */
class FileHelper {

    /**
     * The file we are operating on.
     */
    private final File file;

    /**
     * Creates a new {@code FileHelper} for the given {@code File}.
     * <p>
     * If the file does not already exist you should call {@link FileHelper#createFile(String)} instead.
     *
     * @throws IllegalArgumentException if the file does not exist.
     */
    FileHelper(File file) {
        if (!file.exists())
            throw new IllegalArgumentException("File does not exist");
        this.file = file;
    }

    /**
     * Creates the file at the specified path and all its parent directories if they don't already exist.
     * <p>
     * Please note, this uses the {@link File#mkdirs()} method, which could create some of
     * the directories specified in the path even if the file creation fails.
     *
     * @param path path of file to create.
     * @return the new {@code File} if it was successfully created. If it was not created it an {@code IOException} will be
     *          thrown.
     */
    static FileHelper createFile(String path) throws IOException {
        File file = new File(path);

        if (file.exists()) {
            throw new FileAlreadyExistsException("File at path " + path + " already exists");
        }

        File parent = file.getParentFile();

        if (parent.exists()) {
            // noinspection ResultOfMethodCallIgnored false if exists. Already checked it exists, and we throw IOException on error
            file.createNewFile();
        }
        else {
            boolean created = parent.mkdirs();

            if(!created) {
                throw new IOException("Could not create parent directories");
            }
        }

        return new FileHelper(file);
    }

    /**
     * Writes the {@code contents} to {@code file}.
     *
     * @param contents String contents to write.
     * @throws IOException if an i/o error occurred.
     */
    synchronized void write(String contents) throws IOException {
        FileOutputStream writer = new FileOutputStream(this.file);
        writer.write(contents.getBytes());
        writer.close();
    }

    /**
     * Reads the {@code file} and returns the entire contents as a String.
     *
     * @throws IOException if readLine() fails.
     */
    synchronized String read() throws IOException {
        StringBuilder builder = new StringBuilder();

        BufferedReader reader = new BufferedReader(new FileReader(file));

        String line;
        while ( (line = reader.readLine()) != null) {
            builder.append(line);
        }

        String contents = builder.toString();
        // return null rather than default empty string
        if (contents.equals(""))
            return null;

        return contents;
    }

}
