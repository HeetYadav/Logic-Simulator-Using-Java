package logicsim.gates;

// ABSTRACT CLASS (Practical 7): common base of all gates.
// It implements the interface Displayable (Practical 8).
// evaluate() is abstract: each gate decides how it calculates its output.
public abstract class Gate implements Displayable {
    private String type;      // e.g. "AND"
    private Wire[] inputs;    // array of objects
    private Wire output;

    public Gate(String type, Wire output, Wire[] inputs) {
        this.type = type;
        this.output = output;
        this.inputs = inputs;
    }

    // Every child class must write its own version of this method.
    public abstract boolean evaluate();

    // Helper methods so that child classes can read the inputs safely.
    protected int getInputCount() {
        return inputs.length;
    }

    protected boolean getInput(int index) {
        return inputs[index].getValue();
    }

    public String getType() {
        return type;
    }

    public Wire getOutput() {
        return output;
    }

    // Calculates the new output and returns true if the output changed.
    public boolean update() {
        boolean newValue = evaluate();   // DYNAMIC BINDING: the child's evaluate() runs
        boolean changed = (newValue != output.getValue());
        output.setValue(newValue);
        return changed;
    }

    private int toBit(boolean value) {
        return value ? 1 : 0;            // TERNARY operator
    }

    // Prints e.g.:  AND gate: A=1 B=0 -> Y=0
    @Override
    public void display() {
        System.out.print(type + " gate: ");
        for (int i = 0; i < inputs.length; i++) {
            System.out.print(inputs[i].getName() + "=" + toBit(inputs[i].getValue()) + " ");
        }
        System.out.println("-> " + output.getName() + "=" + toBit(output.getValue()));
    }
}
