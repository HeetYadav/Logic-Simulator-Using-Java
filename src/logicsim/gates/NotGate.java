package logicsim.gates;

public class NotGate extends Gate {
    public NotGate(Wire output, Wire[] inputs) {
        super("NOT", output, inputs);
    }

    @Override
    public boolean evaluate() {
        return !getInput(0);
    }
}
