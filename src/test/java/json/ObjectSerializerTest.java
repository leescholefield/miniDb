package json;

import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;

/**
 * Tests the {@link ObjectSerializer} class.
 */
public class ObjectSerializerTest {

    private TestClass testClass = new TestClass("hello", 20, "super", true);

    /**
     * Tests the {@link ObjectSerializer#toMap()} method.
     */
    @Test
    public void testToMap() throws Exception {
        ObjectSerializer serializer = new ObjectSerializer(testClass);

        Map<String, Object> actual = serializer.toMap();

        Map<String, Object> expected = new HashMap<>();
        expected.put("stringField", "hello");
        expected.put("intField", 20);
        expected.put("superStringField", "super");
        expected.put("superBooleanField", true);

        assertEquals(expected, actual);
    }

}