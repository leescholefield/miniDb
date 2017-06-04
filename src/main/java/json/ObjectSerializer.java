package json;

import exceptions.SerializerException;
import org.json.JSONObject;

import java.lang.reflect.Field;
import java.util.*;

/**
 * Serializes an Object into a JSON string or {@code JSONObject} so it can be stored in the database. Note, the default
 * behavior will include ALL the objects fields, and all its superclass fields, including private ones.
 * <p>
 * Note, I've written this class rather than used the {@link JSONObject#JSONObject(Object)} constructor since that only
 * gets the fields exposed by getters.
 */
class ObjectSerializer {

    /**
     * Object we are operating on.
     */
    private Object obj;

    /**
     * @param obj object that needs to be serialized.
     */
    public ObjectSerializer(Object obj) {
        this.obj = obj;
    }

    /**
     * Converts the object passed to the constructor to a JSON string.
     * <p>
     * It will be in the format "{"[field_name]" : [field_value], "[field_name]": [field_value]}".
     */
    public String toJson() {
        throw new UnsupportedOperationException("toJsonString has not yet been implemented");
    }

    /**
     * Converts the object passed to the constructor to a {@code JSONObject}.
     */
    public JSONObject toJsonObject() {
        throw new UnsupportedOperationException("toJsonObject has not yet been implemented");
    }

    /**
     * Gets an array of {@code Fields} from {@code obj} and all its superclasses (except Object).
     */
    private Field[] getFields() {
        Class<?> current = obj.getClass();
        List<Field> fieldsList = new ArrayList<>();

        // get current classes fields
        Field[] fields = current.getDeclaredFields();
        fieldsList.addAll(Arrays.asList(fields));
        // get superclasses fields
        while ((current = current.getSuperclass()) != null) {
            Field[] f = current.getDeclaredFields();
            fieldsList.addAll(Arrays.asList(f));
        }

        return fieldsList.toArray(new Field[fieldsList.size()]);
    }

    /**
     * Converts the objects {@code Fields} to a Map.
     * <p>
     * Note, this changes the accessibility level of the {@code obj} fields so their value can be read.
     *
     * @throws SerializerException if the field values could not be accessed.
     */
    Map<String, Object> toMap() throws SerializerException {
        Map<String, Object> objectMap = new HashMap<>();

        Field[] fields = getFields();
        for (Field field : fields) {

            try {
                field.setAccessible(true);
                Object val = field.get(obj);

                objectMap.put(field.getName(), val);

            } catch (SecurityException e) {
                throw new SerializerException("Could not access the field " + field.toString(), e);
            } catch (IllegalAccessException e) {
                throw new SerializerException("Could not access the field " + field.toString(), e);
            }
        }

        return objectMap;
    }
}
