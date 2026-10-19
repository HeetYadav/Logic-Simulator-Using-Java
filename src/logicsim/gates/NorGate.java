package logicsim.gates;

public class NorGate extends Gate {
    public NorGate(Wire output, Wire[] inputs) {
        super("NOR", output, inputs);
    }

    @Override
    public boolean evaluate() {
        boolean result = false;
        for (int i = 0; i < getInputCount(); i++) {
            result = result || getInput(i);
        }
        return !result;
    }
}
