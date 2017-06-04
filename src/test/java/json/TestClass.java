package json;

/**
 * Created by lee on 31/05/17.
 */
class TestClass extends SuperTestClass {
    private String stringField;
    private int intField;

    TestClass(String s, int i, String ss, boolean b) {
        super(ss, b);
        this.stringField = s;
        this.intField = i;
    }
}
