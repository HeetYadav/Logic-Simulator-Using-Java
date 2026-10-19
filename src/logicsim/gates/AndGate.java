package logicsim.gates;

public class AndGate extends Gate {
    public AndGate(Wire output, Wire[] inputs) {
        super("AND", output, inputs);
    }

    @Override
    public boolean evaluate() {
        boolean result = true;
        for (int i = 0; i < getInputCount(); i++) {
            result = result && getInput(i);
        }
        return result;
    }
}
