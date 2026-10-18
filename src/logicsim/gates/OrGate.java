package logicsim.gates;

public class OrGate extends Gate {
    public OrGate(Wire output, Wire[] inputs) {
        super("OR", output, inputs);
    }

    // Output is 1 if at least one input is 1.
    @Override
    public boolean evaluate() {
        boolean result = false;
        for (int i = 0; i < getInputCount(); i++) {
            result = result || getInput(i);
        }
        return result;
    }
}
