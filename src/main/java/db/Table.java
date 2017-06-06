package db;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Iterator;

/**
 * Represents a single table in a {@link JsonDatabase}. In practice this class acts as a simple wrapper around a
 * {@code JSONObject}, providing methods for inserting items and querying contents.
 * <p>
 * This class is just to reduce the size of the {@code JsonDatabase} class and is designed for internal use only.
 * It is not a part of the libraries public API.
 * <p>
 * A table will be in the format:
 * <pre>
 *     {
 *         "1" : {
 *              "key_name" : [value],
 *              "key_name" : [value]
 *         },
 *         "2" : {
 *             ...
 *         },
 *         ...
 *     }
 * </pre>
 * Each table will have a sequential list of numbers (in string format) as keys which are mapped to {@code JSONObjects}
 * which contain the actual item data.
 */
class Table {

    /**
     * Root node of the specified {@code Table}.
     */
    private JSONObject jsonRoot;

    /**
     * Package-private constructor used by {@link JsonDatabase#getTable}.
     *
     * @param jsonObject the root object of the table.
     */
    Table(JSONObject jsonObject) {
        this.jsonRoot = jsonObject;
    }

    /**
     * Returns the value associated with the specified key as a {@code String}, or {@code null} if the associated value is
     * null.
     *
     * @throws IllegalArgumentException if there is no such key.
     */
    String getValue(String key) throws IllegalArgumentException {
        try {
            String value = jsonRoot.getString(key);

            if (value.equals("null"))
                return null;

            return value;
        } catch (JSONException e) {
            throw new IllegalArgumentException("Key: " + key + " not found.");
        }
    }

    /**
     * Appends the given {@code JSONObject} to the {@code jsonRoot}.
     */
    void append(JSONObject jObj) {
        try {
            jsonRoot.put(String.valueOf(getNextId()), jObj);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * Gets the next id for this table.
     */
    int getNextId() {
        int highest = 0;

        Iterator<?> iterator = jsonRoot.keys();

        while( iterator.hasNext() ) {
            int id = Integer.valueOf((String)iterator.next());
            if (id > highest)
                highest = id;
        }

        return ++highest;

    }

    /**
     * Calls the {@code jsonRoot} toString() method.
     */
    @Override
    public String toString() {
        return jsonRoot.toString();
    }
}
