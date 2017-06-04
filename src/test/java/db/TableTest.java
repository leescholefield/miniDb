package db;

import org.json.JSONObject;
import org.junit.Test;

import java.io.File;

import static org.junit.Assert.*;

/**
 * Tests the {@link Table} class.
 */
public class TableTest {

    private Table table = new JsonDatabase("src/test/data/test_data.json").getTable("default");

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
     * Tests that an invalid key passed to {@link Table#getValue(String)} will return null.
     */
    @Test
    public void testGetValueInvalidKey() throws Exception {
        assertNull(table.getValue("invalid"));
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