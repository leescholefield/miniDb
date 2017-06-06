package db;

import com.sun.istack.internal.Nullable;
import exceptions.ParsingException;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * This class acts as a wrapper around a .json file and provides methods typical of a relational database, such as appending
 * to a table, querying the table, adding entries, etc.
 * <p>
 * Please note, this will load the entire file into memory as a {@code JSONObject}.
 */
public class JsonDatabase {


    /**
     * The entire contents of the JSON file. We should update this whenever we want to append data to the file, and query
     * this rather than {@link FileHelper#read()}
     * <p>
     * Note, during instantiation if the contents of the file are empty this will be null.
     */
    private JSONObject rootObject = null;

    /**
     * Wrapper around a {@code File} instance. Synchronizes writing.
     */
    private FileHelper fileHelper;

    /**
     * Creates a new JsonDatabase connection to the JSON file at the given path.
     *
     * @param path path to the .json file.
     * @throws IllegalArgumentException if the file does not exist
     * @throws ParsingException if the file could be opened but not parsed.
     */
    public JsonDatabase(String path) throws ParsingException, IllegalArgumentException {
        this(new File(path));
    }

    /**
     * Creates a new {@code JsonDatabase} instance for the given {@code file}.
     *
     * @param file .json file
     * @throws ParsingException if the file could not be opened, or if a JSONObject could not be constructed from the
     *                          contents.
     */
    public JsonDatabase(File file) throws ParsingException {
        FileHelper helper = new FileHelper(file);

        JSONObject jsonObject = readContentsAsJson(helper);

        if (jsonObject != null)
            this.rootObject = jsonObject;

        this.fileHelper = helper;
    }

    /**
     * Private constructor used by the {@link #create(String)} factory method.
     */
    private JsonDatabase(FileHelper helper) {
       JSONObject jsonObject = readContentsAsJson(helper);

       if (jsonObject != null)
           this.rootObject = jsonObject;

       this.fileHelper = helper;
    }


    /**
     * Creates a new .json file at the given path. This will also create all of the directories given in the path if they
     * don't already exist.
     * <p>
     * Please note, {@link FileHelper#createFile} uses the {@link File#mkdirs()} method, which could create some of
     * the directories specified in the path even if the file creation fails.
     *
     * @param path path to save the new file.
     * @return a new JsonDatabase instance.
     *
     * @throws IOException if the file already exists, could not be created, or if the default contents could not be written
     *                      to the newly created file.
     */
    public static JsonDatabase create(String path) throws IOException {

        FileHelper helper;
        // create the file
        try {
            helper = FileHelper.createFile(path);
        } catch (IOException e) {
            throw new IOException("Could not create new file.", e);
        }

        // write the default contents to the file.
        try {
            helper.write("{ \"default\": {} }");
        } catch (IOException e) {
            throw new IOException("Could not write initial contents to file.", e);
        }

        return new JsonDatabase(helper);
    }

    /**
     * Reads the contents of the File held by the {@code fileHelper} and converts it into a {@code JSONObject}.
     *
     * @param fileHelper FileHelper containing the file
     * @return the contents as a JSONObject or {@code null} if the contents are empty.
     *
     * @throws ParsingException if the file could not be read, or the JSONObject could not be created.
     */
    private static JSONObject readContentsAsJson(FileHelper fileHelper) throws ParsingException {

        try {
            String contents = fileHelper.read();

            if (contents != null) {
                return new JSONObject(contents);
            } else {
                System.out.println("Contents: " + contents);
                return null;
            }

        } catch (IOException e) {
            throw new ParsingException("Could not read file: ", e);
        } catch (JSONException e) {
            throw new ParsingException("Could not create JSONObject: ", e);
        }
    }

    /**
     * Appends the given {@code values} to the {@code table}.
     *
     * @param tableName table to append to.
     * @param values content to append.
     *
     * @throws ParsingException if the {@code values} could not converted to a {@code JSONObject}
     * @throws IOException if the {@code JSONObject} could not be written to the file.
     * @throws IllegalArgumentException if there is no table with that name.
     */
    public void append(String tableName, Map<String, Object> values) throws ParsingException, IOException {
        Table table = getTable(tableName); // throws IllegalArgumentException on fail

        try {
            // create a new JSONObject from values
            JSONObject content = new JSONObject();
            for (String key : values.keySet()) {
                content.put(key, values.get(key));
            }

            table.append(content);
        } catch (JSONException e) {
            throw new ParsingException("Could not add the values to the table.", e);
        }

        try {
            fileHelper.write(rootObject.toString());
        } catch (IOException e) {
            throw new IOException("Could not write to the file:", e);
        }
    }

    /**
     * Appends the {@code value} to the table matching the given tableName.
     *
     * @param tableName name of table to append to.
     * @param valueKey key to save value under
     * @param value value to save
     *
     * @throws IllegalArgumentException if the table doesn't exist, or the value is already saved.
     */
    public void appendValueToTable(String tableName, int id, String valueKey, Object value) throws IOException,
            IllegalArgumentException {

        JSONObject table = rootObject.optJSONObject(tableName);
        if (table == null) {
            throw new IllegalArgumentException("Could not find table matching the name " + tableName);
        }

        JSONObject entry = table.optJSONObject(String.valueOf(id));
        if (entry == null) {
            throw new IllegalArgumentException("No entry found matching the id " + String.valueOf(id));
        }

        if (entry.has(valueKey)) {
            throw new IllegalArgumentException("A key with the name " + valueKey + " already exists");
        } else {
            try {
                entry.put(valueKey, value);
            } catch (JSONException e) {
                throw new ParsingException("Could not append the value to the jsonobject");
            }
        }

        // save to file
        try {
            fileHelper.write(rootObject.toString());
        } catch (IOException e) {
            throw new IOException("Could not write contents to file", e);
        }

    }

    /**
     * Creates a new table with the given {@code name} and the {@code initialValues}, if any were given.
     *
     * @throws IOException if it could not write to the file.
     * @throws IllegalArgumentException if table already exists.
     */
    public void newTable(String name, @Nullable Map<String, ?> initialValues) throws IOException, IllegalArgumentException {
        if (tableExists(name))
            throw new IllegalArgumentException("Table named " + name + " already exists.");

        JSONObject table = new JSONObject(initialValues);
        try {
            rootObject.put(name, table);
        } catch (JSONException e) {
            throw new ParsingException("Could not append table to rootObject");
        }

        fileHelper.write(rootObject.toString());
    }

    /**
     * Returns the {@link Table} matching the given name.
     *
     * @throws IllegalArgumentException if there is no such table.
     * @throws NullPointerException if rootObject is null. This can happen if the .json file passed to the constructor
     *      is empty.
     */
    Table getTable(String name) {
        if (rootObject == null)
            throw new NullPointerException("rootObject is null");

        JSONObject jObj;
        try {
            jObj = rootObject.getJSONObject(name);
        } catch (JSONException e) {
            throw new IllegalArgumentException("Could not get table " + name, e);
        }

        return new Table(jObj);
    }

    /**
     * Deletes a table from the database.
     *
     * @param tableName name of table to delete.
     *
     * @throws IllegalArgumentException if there is no table with that name.
     * @throws IOException if the table could not be deleted.
     */
    public void dropTable(String tableName) throws IOException {
        assert rootObject != null;

        if (!tableExists(tableName)) {
            throw new IllegalArgumentException("No table found with name " + tableName);
        }

        rootObject.remove(tableName);
        fileHelper.write(rootObject.toString());
    }

    /**
     * Deletes an item with the given {@code id} from the {@code table}.
     *
     * @param id id of item to delete.
     * @param table table to delete from.
     *
     * @throws IOException if the item cannot be deleted.
     */
    public void delete(int id, String table) throws IOException {
        assert rootObject != null;

        delete(String.valueOf(id), table);
    }

    /**
     * Deletes an item with the given {@code key} from the {@code table}.
     *
     * @param key key of item to delete
     * @param table table to delete from.
     *
     * @throws IOException if the item cannot be deleted.
     * @throws IllegalArgumentException if there's no item with the given key
     */
    public void delete(String key, String table) throws IOException {
        assert rootObject != null;

        // get the table
        JSONObject tableObj;
        try {
            tableObj = rootObject.getJSONObject(table);
        } catch (JSONException e) {
            throw new ParsingException("Could not parse JSONObject", e);
        }

        // remove from jsonRoot
        if (tableObj.has(key)) {
            rootObject.remove(key);
        } else {
            throw new IllegalArgumentException("No item found with the gen key");
        }

        // delete from database
        fileHelper.write(rootObject.toString());
    }

    /**
     * Returns {@code true} if a table with the given name exists.
     *
     * @throws NullPointerException if rootObject is null. This can happen if the .json file passed to the constructor
     *      is empty.
     */
    public boolean tableExists(String name) {
        if (rootObject == null)
            throw new NullPointerException("rootObject is null");

        JSONObject array = rootObject.optJSONObject(name);

        return array != null;
    }

    /**
     * Returns an array of table names.
     *
     * @throws NullPointerException if rootObject is null. This can happen if the .json file passed to the constructor
     *      is empty.
     */
    public String[] tableNames() {
        if (rootObject == null)
            throw new NullPointerException("rootObject is null");

        List<String> names = new ArrayList<>();
        Iterator iter = rootObject.keys();

        while(iter.hasNext()) {
            String s = (String)iter.next();
            names.add(s);

        }

        return names.toArray(new String[names.size()]);
    }

    /**
     * Returns the {@code JSONObject} toString method.
     */
    @Override
    public String toString() {
        return rootObject.toString();
    }

}
