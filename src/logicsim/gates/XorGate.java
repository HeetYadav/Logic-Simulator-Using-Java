package logicsim.gates;

public class XorGate extends Gate {
    public XorGate(Wire output, Wire[] inputs) {
        super("XOR", output, inputs);
    }

    // Output is 1 if an odd number of inputs are 1.
    @Override
    public boolean evaluate() {
        boolean result = false;
        for (int i = 0; i < getInputCount(); i++) {
            result = result ^ getInput(i);
        }
        return result;
    }
}
