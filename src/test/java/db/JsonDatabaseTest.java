package db;

import org.junit.After;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.io.File;
import java.io.IOException;
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

    private static final String TEST_FILE_PATH = "src/test/data/temp_test_file.json";

    private JsonDatabase db = new JsonDatabase("src/test/data/test_data.json");

    @Rule
    public ExpectedException expected = ExpectedException.none();

    @After
    public void deleteFile() throws Exception {
        File file = new File(TEST_FILE_PATH);
        if (file.exists()) {
            Utils.deleteFile(file, "JsonDatabaseTest");
        }
    }

    @Test
    public void tableNames_returns_all_tables() throws Exception {
        String[] tables = db.tableNames();
        String[] expected = new String[] {"default", "events"};

        assertArrayEquals(expected, tables);
    }

    @Test
    public void tableExists_returns_true_when_table_exists() throws Exception {
        assertTrue(db.tableExists("default"));
    }

    @Test
    public void tableExists_returns_false_when_tables_does_not_exist() throws Exception {
        assertFalse(db.tableExists("not_exist"));
    }

    @Test
    public void create_successfully_creates_file() throws Exception {
        JsonDatabase.create(TEST_FILE_PATH);

        File file = new File(TEST_FILE_PATH);

        assertTrue(file.exists());
    }

    @Test
    public void append_successfully_appends_data_to_file() throws Exception {
        JsonDatabase db = createNewDbFile(TEST_FILE_PATH);

        Map<String, Object> content = createValuesMap();
        db.append("default", content);

        String expected = "{\"default\":{\"1\":{\"first key\":\"first value\",\"second key\":\"second value\"}}}";

        assertEquals(expected, db.toString());
    }

    @Test
    public void dropTable_successfully_deletes_table() throws Exception {
        String contents = "{\"default\": {}, \"delete\": {} }";
        Utils.createFile(TEST_FILE_PATH, contents);
        JsonDatabase db = new JsonDatabase(TEST_FILE_PATH);

        db.dropTable("delete");

        String expected = "{\"default\":{}}";

        assertEquals(expected, db.toString());
    }

    @Test
    public void dropTable_throws_exception_if_table_does_not_exist() throws Exception {
        JsonDatabase db = createNewDbFile(TEST_FILE_PATH);

        expected.expect(IllegalArgumentException.class);

        db.dropTable("not exist");
    }

    @Test
    public void getTable_successfully_returns_table() throws Exception {
        File file = Utils.createFile(TEST_FILE_PATH, "{\"default\": {\"name\":\"hello\"}}");
        JsonDatabase db = new JsonDatabase(file);

        Table table = db.getTable("default");

        String expected = "{\"name\":\"hello\"}";

        assertEquals(expected, table.toString());
    }

    @Test
    public void getTable_throws_exception_when_table_does_not_exist() throws Exception {
        expected.expect(IllegalArgumentException.class);

        db.getTable("not exist");
    }

    @Test
    public void delete_successfully_deletes_item() throws Exception {
        File file = Utils.createFile(TEST_FILE_PATH, "{\"default\": {\"2\": {\"name\":\"test\"} } }");
        JsonDatabase db = new JsonDatabase(file);

        db.delete("2", "default");

        assertFalse(db.tableExists("2"));
    }

    @Test
    public void newTable_creates_new_table_with_given_values() throws Exception {
        File file = Utils.createFile(TEST_FILE_PATH, "{\"default\": {}}");
        JsonDatabase db = new JsonDatabase(file);

        db.newTable("test", createValuesMap());

        String expected = "{\"default\":{},\"test\":{\"first key\":\"first value\",\"second key\":\"second value\"}}";

        assertEquals(expected, db.toString());

    }

    @Test
    public void newTable_creates_new_table_when_given_null_values() throws Exception {
        File file = Utils.createFile(TEST_FILE_PATH, "{\"default\": {}}");
        JsonDatabase db = new JsonDatabase(file);

        db.newTable("test", null);

        assertTrue(db.tableExists("test"));
    }

    @Test
    public void newTable_throws_exception_if_table_already_exists() throws Exception {
        File file = Utils.createFile(TEST_FILE_PATH, "{\"default\":{}}");
        JsonDatabase db = new JsonDatabase(file);

        expected.expect(IllegalArgumentException.class);

        db.newTable("default", null);
    }

    @Test
    public void appendValueToTable_successfully_appends_string() throws Exception {
        File file = Utils.createFile(TEST_FILE_PATH, "{\"default\":{\"1\": {\"hello\":\"world\"}}}");
        JsonDatabase db = new JsonDatabase(file);

        String expected = "{\"default\":{\"1\":{\"test\":\"new value\",\"hello\":\"world\"}}}";
        db.appendValueToTable("default", 1, "test", "new value");

        assertEquals(expected, db.toString());
    }

    @Test
    public void appendValueToTable_throws_exception_when_table_does_not_exist() throws Exception {
        File file = Utils.createFile(TEST_FILE_PATH, "{\"default\":{}}");
        JsonDatabase db = new JsonDatabase(file);

        expected.expect(IllegalArgumentException.class);

        db.appendValueToTable("invalid", 1, "test", "testing");
    }

    private JsonDatabase createNewDbFile(String path) throws IOException {
        return JsonDatabase.create(path);
    }

    private Map<String, Object> createValuesMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("first key", "first value");
        map.put("second key", "second value");
        return map;
    }



}