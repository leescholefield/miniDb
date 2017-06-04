package json;

import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;

/**
 * Tests the {@link JsonSerializer} class.
 */
public class JsonSerializerTest {

    /**
     * Tests the {@link JsonSerializer#toJsonString(Map)} method.
     */
    @Test
    public void testToJsonString() throws Exception {
        Map<String, Object> map = new HashMap<>();
        map.put("hello", "world");
        map.put("id", 332);

        String expected = "{\"hello\":\"world\",\"id\":332}";

        assertEquals(expected, JsonSerializer.toJsonString(map));
    }
}