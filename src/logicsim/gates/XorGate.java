package logicsim.gates;

public class XorGate extends Gate {
    public XorGate(Wire output, Wire[] inputs) {
        super("XOR", output, inputs);
    }

    @Override
    public boolean evaluate() {
        boolean result = false;
        for (int i = 0; i < getInputCount(); i++) {
            result = result ^ getInput(i);
        }
        return result;
    }
}
