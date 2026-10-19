package logicsim.gates;

public class Wire {
    private String name;
    private boolean value;

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
