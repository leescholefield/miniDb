package db;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.io.File;

import static org.junit.Assert.*;

/**
 * Tests the {@link Table} class.
 */
public class TableTest {

    private Table table = new JsonDatabase("src/test/data/test_data.json").getTable("default");
    @Rule
    public ExpectedException expected = ExpectedException.none();

    /**
     * Tests the {@link Table#getValue(String)} method.
     * <p>
     * Warning, running this method will create a new .json file in the test/data directory, which will then be deleted.
     */
    @Test
    public void testGetValue() throws Exception {
        File file = Utils.createFile("src/test/data/get_value.json", "{\"default\":{\"test\":\"testing\"}}");
        JsonDatabase db = new JsonDatabase(file);

        String actual = db.getTable("default").getValue("test");

        try {
            assertEquals("testing", actual);
        }
        // delete table even if test fails
        finally {
            Utils.deleteFile(file, "TableTest.testGetValue()");
        }
    }

    /**
     * Tests that an invalid key passed to {@link Table#getValue(String)} will throw an {@code IllegalArgumentException}.
     */
    @Test
    public void testGetValueInvalidKey() throws Exception {
        expected.expect(IllegalArgumentException.class);

        table.getValue("invalid_key");
    }

    /**
     * Tests that {@link Table#getValue(String)} will return {@code null} when the associated value is null.
     */
    @Test
    public void testGetValueNull() throws Exception {
        File file = Utils.createFile("src/test/data/table_test.json", "{\"default\": {\"name\":null}}");
        JsonDatabase db = new JsonDatabase(file);

        Table table = db.getTable("default");

        try {
            assertNull(table.getValue("name"));
        }
        finally {
            Utils.deleteFile(file, "TableTest.testGetValueNull()");
        }
    }

    /**
     * Tests the {@link Table#getNextId()} method.
     */
    @Test
    public void nextIdTest() throws Exception {
        int expected = 3;
        assertEquals(expected, table.getNextId());
    }

    /*
    @Test
    public void appendTest() throws Exception {
        JSONObject jObj = new JSONObject("{\"name\": \"append\"}");
        table.append(jObj);

        System.out.println(table.toString());
    }
    */

}