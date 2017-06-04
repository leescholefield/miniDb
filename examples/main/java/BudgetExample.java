import db.JsonDatabase;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 *
 */
public class BudgetExample {

    private JsonDatabase db;

    private BudgetExample(JsonDatabase db) {
        assert db != null;

        this.db = db;
    }

    /**
     * Gets the .json file at the given path, or creates one if it doesn't exist.
     */
    private static BudgetExample getOrCreate(String path) throws IOException {
        JsonDatabase db;
        try {
            db = new JsonDatabase(path);
        } catch (IllegalArgumentException e) {
            db = JsonDatabase.create(path);
        }

        return new BudgetExample(db);
    }

    /**
     * Creates a new table with the given name if it doesn't already exist.
     */
    private void createTableIfNotExist(String name) throws IOException {
        if (!db.tableExists(name)) {
            db.newTable(name, null);
        }
    }

    /**
     * Adds a new Item to the database.
     */
    private void addItem(String table, Map<String, Object> values) throws IOException {
        if (!db.tableExists(table))
            throw new IllegalArgumentException("Table does not exist");

        db.append(table, values);
    }

    public static void main(String[] args) {
        BudgetExample budget = null;

        try {

            budget = BudgetExample.getOrCreate("examples/main/data/budget.json");
            budget.createTableIfNotExist("expenses");

            Map<String, Object> values = new HashMap<>();
            values.put("name", "rent");
            values.put("cost", 100); // I wish!

            budget.addItem("expenses", values);

            Map<String, Object> values2 = new HashMap<>();
            values2.put("name", "car insurance");
            values2.put("cost", 221);

            budget.addItem("expenses", values2);

        } catch (IOException e){
            e.printStackTrace();
            System.exit(1);
        }

        System.out.println(budget.db.toString());
    }
}
