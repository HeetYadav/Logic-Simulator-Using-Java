package logicsim.gates;

public class OrGate extends Gate {
    public OrGate(Wire output, Wire[] inputs) {
        super("OR", output, inputs);
    }

    @Override
    public boolean evaluate() {
        boolean result = false;
        for (int i = 0; i < getInputCount(); i++) {
            result = result || getInput(i);
        }
        return result;
    }
}
