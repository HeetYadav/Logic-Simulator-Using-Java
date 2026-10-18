package logicsim.gates;

// INHERITANCE: AndGate "is a" Gate.
public class AndGate extends Gate {
    public AndGate(Wire output, Wire[] inputs) {
        super("AND", output, inputs);
    }

    // METHOD OVERRIDING: output is 1 only if all inputs are 1.
    @Override
    public boolean evaluate() {
        boolean result = true;
        for (int i = 0; i < getInputCount(); i++) {
            result = result && getInput(i);
        }
        return result;
    }
}
