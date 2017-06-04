package db;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

import static db.Utils.deleteFile;
import static org.junit.Assert.*;

/**
 * Tests the {@link FileHelper} class.
 */
public class FileHelperTest {

    @Rule
    public final ExpectedException expected = ExpectedException.none();

    /**
     * Tests the {@link FileHelper#createFile(String)} factory method.
     * <p>
     * Warning, this will create a new file at the test path which will then be deleted.
     */
    @Test
    public void createFileTest() throws Exception {
        String path = "src/test/data/temp.json";

        FileHelper.createFile(path);

        File file = new File(path);

        assertTrue(file.exists());

        deleteFile(file, "createFileTest");
    }

    /**
     * Tests that an invalid File passed to {@link FileHelper#FileHelper(File)} throws an IllegalArgumentException.
     */
    @Test
    public void invalidConstructorCall() throws Exception {
        String notExist = "path/that/doesnt/exist.json";

        expected.expect(IllegalArgumentException.class);

        new FileHelper(new File(notExist));
    }

    /**
     * Tests the {@link FileHelper#write(String)} method.
     * <p>
     * Warning, this will create a new file at the test path which will then be deleted.
     */
    @Test
    public void writeTest() throws Exception {
        String contents = "contents of the file";
        File file = new File("src/test/data/temp.json");
        FileHelper helper = FileHelper.createFile("src/test/data/temp.json");

        helper.write(contents);

        assertEquals(contents, new BufferedReader(new FileReader(file)).readLine());

        // delete file
        Utils.deleteFile(file, "writeTest");
    }

    /**
     * Tests the {@link FileHelper#read()} method.
     */
    @Test
    public void readTest() throws Exception {
        String expected = "text of the read test file";
        FileHelper helper = new FileHelper(new File("src/test/data/read_test.txt"));

        String actual = helper.read();

        assertEquals(expected, actual);
    }


}