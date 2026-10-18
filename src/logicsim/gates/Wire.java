package logicsim.gates;

// A wire carries one value: false = 0, true = 1.
// ENCAPSULATION (Practical 5/6): fields are private, access only through methods.
public class Wire {
    private String name;
    private boolean value;

    // CONSTRUCTOR: a new wire starts at 0.
    public Wire(String name) {
        this.name = name;
        this.value = false;
    }

    public String getName() {
        return name;
    }

    public boolean getValue() {
        return value;
    }

    public void setValue(boolean value) {
        this.value = value;
    }
}
