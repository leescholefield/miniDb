package db;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;

/**
 * Tests the {@link JsonDatabase} class.
 * <p>
 * Warning, running most of these tests will create a temporary file in the 'test/data' directory. Even if the test fails
 * these files should automatically be deleted, if the delete fails an warning will show.
 */
public class JsonDatabaseTest {

    private JsonDatabase db = new JsonDatabase("src/test/data/test_data.json");

    @Rule
    public ExpectedException expected = ExpectedException.none();

    /**
     * Tests {@link JsonDatabase#tableNames()}
     */
    @Test
    public void testTableNames() throws Exception {
        String[] tables = db.tableNames();
        String[] expected = new String[] {"default", "events"};

        assertArrayEquals(expected, tables);
    }

    /**
     * Tests {@link JsonDatabase#tableExists(String)}.
     */
    @Test
    public void testTableExists() throws Exception {
        assertEquals(true, db.tableExists("default"));
        assertEquals(false, db.tableExists("not_exists"));
    }

    /**
     * Tests whether the {@link JsonDatabase#create(String)} constructor method will successfully create a new file.
     * <p>
     */
    @Test
    public void createFileTest() throws Exception {
        String path = "src/test/data/temp.json";

        JsonDatabase.create(path);

        File file = new File(path);

        try {
            assertTrue(file.exists());
        }
        // delete file even if test fails
        finally {
            Utils.deleteFile(file, "JsonDatabaseTest.createFileTest()");
        }

    }

    /**
     * Tests the {@link JsonDatabase#append(String, Map)} method.
     */
    @Test
    public void appendTest() throws Exception {
        String tempFilePath = "src/test/data/append_data.json";

        Map<String, Object> content = new HashMap<>();
        content.put("super_name", "wolverine");
        content.put("name", "logan");

        JsonDatabase db = JsonDatabase.create(tempFilePath);
        db.append("default", content);

        String expected = "{\"default\":{\"1\":{\"name\":\"logan\",\"super_name\":\"wolverine\"}}}";

        try {
            assertEquals(expected, db.toString());
        }
        // deleted file even if test fails
        finally {
            Utils.deleteFile(tempFilePath, "JsonDatabaseTest.appendTest()");
        }
    }

    /**
     * Tests the {@link JsonDatabase#dropTable(String)} method..
     */
    @Test
    public void dropTableTest() throws Exception {
        String contents = "{\"default\": {}, \"delete\": {} }";
        File file = Utils.createFile("src/test/data/drop_table.json", contents);

        JsonDatabase db = new JsonDatabase(file);

        db.dropTable("delete");

        String expected = "{\"default\":{}}";

        try {
            assertEquals(expected, db.toString());
        }
        // delete file even if test fails.
        finally {
            Utils.deleteFile(file, "JsonDatabaseTest.dropTableTest()");
        }

    }

    /**
     * Tests the {@link JsonDatabase#getTable(String)} method.
     */
    @Test
    public void getTableTest() throws Exception {
        File file = Utils.createFile("src/test/data/get_table.json", "{\"default\": {\"name\":\"hello\"}}");
        JsonDatabase db = new JsonDatabase(file);

        Table table = db.getTable("default");

        String expected = "{\"name\":\"hello\"}";

        try {
            assertEquals(expected, table.toString());
        }
        // delete file even if test fails
        finally {
            Utils.deleteFile(file, "JsonDatabaseTest.getTableTest()");
        }
    }

    /**
     * Tests the {@link JsonDatabase#delete(String, String)} method.
     */
    @Test
    public void deleteTest() throws Exception {
        File file = Utils.createFile("src/test/data/delete_test.json",
                "{\"default\": {\"2\": {\"name\":\"test\"} } }");
        JsonDatabase db = new JsonDatabase(file);

        db.delete("2", "default");


        try {
            assertFalse(db.tableExists("2"));
        }
        // delete file even if test fails
        finally {
            Utils.deleteFile(file, "JsonDatabaseTest.deleteTest()");
        }

    }

    /**
     * Tests the {@link JsonDatabase#newTable(String, Map)} method.
     */
    @Test
    public void newTableTest() throws Exception {
        File file = Utils.createFile("src/test/data/table_test.json",
                "{\"default\": {}}");
        JsonDatabase db = new JsonDatabase(file);

        Map<String, Object> values = new HashMap<>();
        values.put("hello", "world");
        values.put("number", 42);

        db.newTable("test", values);

        String expected = "{\"default\":{},\"test\":{\"number\":42,\"hello\":\"world\"}}";

        try {
            assertEquals(expected, db.toString());
        }
        // delete file even if test fails
        finally {
            Utils.deleteFile(file, "JsonDatabaseTest.newTableTest()");
        }
    }

    /**
     * Tests the {@link JsonDatabase#newTable(String, Map)} method will null values.
     */
    @Test
    public void newTableTestNullValues() throws Exception {
        File file = Utils.createFile("src/test/data/table_test.json",
                "{\"default\": {}}");
        JsonDatabase db = new JsonDatabase(file);

        db.newTable("test", null);

        try {
            assertTrue(db.tableExists("test"));
        }
        // delete file even if test fails
        finally {
            Utils.deleteFile(file, "JsonDatabaseTest.newTableTestNullValues()");
        }
    }

    /**
     * Tests {@link JsonDatabase#newTable(String, Map)} with an already existing table name.
     */
    @Test
    public void newTableTestThrows() throws Exception {
        File file = Utils.createFile("src/test/data/table_throws_test.json",
                "{\"default\":{}}");
        JsonDatabase db = new JsonDatabase(file);

        expected.expect(IllegalArgumentException.class);

        try {
            db.newTable("default", null);
        }
        // delete file even if test fails
        finally {
            Utils.deleteFile(file, "JsonDatabaseTest.newTableTestThrows()");
        }

    }

    /**
     * Tests the {@link JsonDatabase#appendValueToTable(String, int, String, Object)} method with a string object.
     */
    @Test
    public void appendValueToTableTest() throws Exception {
        File file = Utils.createFile("src/test/data/append_value.json",
                "{\"default\":{\"1\": {\"hello\":\"world\"}}}");

        JsonDatabase db = new JsonDatabase(file);

        String expected = "{\"default\":{\"1\":{\"test\":\"new value\",\"hello\":\"world\"}}}";

        try {
            db.appendValueToTable("default", 1, "test", "new value");
            assertEquals(expected, db.toString());
        }
        finally {
            Utils.deleteFile(file, "JsonDatabaseTest.appendValueToTableTest");
        }
    }

    /**
     * Tests the {@link JsonDatabase#appendValueToTable(String, int, String, Object)} with an invalid {@code fileName}.
     */
    @Test
    public void appendValueToTableInvalid() throws Exception {
        File file = Utils.createFile("src/test/data/append_value_invalid.json",
                "{\"default\":{}}");
        JsonDatabase db = new JsonDatabase(file);

        try {
        expected.expect(IllegalArgumentException.class);

        db.appendValueToTable("invalid", 1, "test", "testing");
        } finally {
            Utils.deleteFile(file, "JsonDatabaseTest.appendValueToTableInvalid()");
        }

    }



}