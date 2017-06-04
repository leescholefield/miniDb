package json;

import org.json.JSONObject;

import java.util.Map;

/**
 * Serializes an Object or {@code Map} into JSON.
 */
public class JsonSerializer {

    /**
     * Converts a {@code Map<String,?>} into a JSON string in the format:
     * <pre>
     *     {
     *         "[key_name]": [key_value],
     *         "[key_name]": [key_name]
     *     }
     * </pre>
     */
    public static String toJsonString(Map<String, ?> map) {
        JSONObject obj = new JSONObject(map);
        return obj.toString();
    }

    /**
     * Converts a {@code Map<String,?>} into a {@code JSONObject}.
     */
    public static JSONObject toJsonObject(Map<String, ?> map) {
        JSONObject obj = new JSONObject(map);
        return obj;
    }
}
