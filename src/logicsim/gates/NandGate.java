package logicsim.gates;

public class NandGate extends Gate {
    public NandGate(Wire output, Wire[] inputs) {
        super("NAND", output, inputs);
    }

    @Override
    public boolean evaluate() {
        boolean result = true;
        for (int i = 0; i < getInputCount(); i++) {
            result = result && getInput(i);
        }
        return !result;
    }
}
