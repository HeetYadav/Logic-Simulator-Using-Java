package logicsim.gates;

public abstract class Gate implements Displayable {
    private final String type;
    private final Wire[] inputs;
    private final Wire output;

    public Gate(String type, Wire output, Wire[] inputs) {
        this.type = type;
        this.output = output;
        this.inputs = inputs.clone();
    }

    public abstract boolean evaluate();

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

    public boolean update() {
        boolean newValue = evaluate();
        boolean changed = (newValue != output.getValue());
        output.setValue(newValue);
        return changed;
    }

    private int toBit(boolean value) {
        return value ? 1 : 0;
    }

    @Override
    public void display() {
        System.out.print(type + " gate: ");
        for (int i = 0; i < inputs.length; i++) {
            System.out.print(inputs[i].getName() + "=" + toBit(inputs[i].getValue()) + " ");
        }
        System.out.println("-> " + output.getName() + "=" + toBit(output.getValue()));
    }
}
