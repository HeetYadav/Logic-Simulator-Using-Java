package logicsim.circuit;

import java.util.ArrayList;
import logicsim.gates.AndGate;
import logicsim.gates.Gate;
import logicsim.gates.NandGate;
import logicsim.gates.NorGate;
import logicsim.gates.NotGate;
import logicsim.gates.OrGate;
import logicsim.gates.Wire;
import logicsim.gates.XorGate;

// COMPOSITION (Practical 5): a Circuit "has" wires and gates.
public class Circuit {
    private ArrayList<Wire> wires = new ArrayList<Wire>();
    private ArrayList<Gate> gates = new ArrayList<Gate>();
    private ArrayList<Wire> inputs = new ArrayList<Wire>();
    private ArrayList<Wire> outputs = new ArrayList<Wire>();

    // Returns the wire with this name. If it does not exist yet, creates it.
    public Wire getWire(String name) {
        for (int i = 0; i < wires.size(); i++) {
            if (wires.get(i).getName().equals(name)) {
                return wires.get(i);
            }
        }
        Wire w = new Wire(name);
        wires.add(w);
        return w;
    }

    public void addInput(String name) {
        inputs.add(getWire(name));
    }

    public void addOutput(String name) {
        outputs.add(getWire(name));
    }

    // Creates the correct gate object from its type name.
    // The reference type is Gate, the object is AndGate/OrGate/... (POLYMORPHISM).
    public void addGate(String type, String outName, String[] inNames) throws InvalidCircuitException {
        String t = type.toUpperCase();
        if (t.equals("NOT")) {
            if (inNames.length != 1) {
                throw new InvalidCircuitException("NOT gate needs exactly 1 input");
            }
        } else if (inNames.length < 2) {
            throw new InvalidCircuitException(t + " gate needs at least 2 inputs");
        }

        Wire out = getWire(outName);
        for (int i = 0; i < gates.size(); i++) {
            if (gates.get(i).getOutput() == out) {
                throw new InvalidCircuitException("Wire " + outName + " is driven by two gates");
            }
        }

        Wire[] in = new Wire[inNames.length];
        for (int i = 0; i < inNames.length; i++) {
            in[i] = getWire(inNames[i]);
        }

        Gate g;
        if (t.equals("AND")) {
            g = new AndGate(out, in);
        } else if (t.equals("OR")) {
            g = new OrGate(out, in);
        } else if (t.equals("XOR")) {
            g = new XorGate(out, in);
        } else if (t.equals("NAND")) {
            g = new NandGate(out, in);
        } else if (t.equals("NOR")) {
            g = new NorGate(out, in);
        } else if (t.equals("NOT")) {
            g = new NotGate(out, in);
        } else {
            throw new InvalidCircuitException("Unknown gate type: " + type);
        }
        gates.add(g);
    }

    public int getInputCount() {
        return inputs.size();
    }

    public int getOutputCount() {
        return outputs.size();
    }

    public String getInputName(int index) {
        return inputs.get(index).getName();
    }

    public void setInput(int index, boolean value) {
        inputs.get(index).setValue(value);
    }

    public boolean getOutput(int index) {
        return outputs.get(index).getValue();
    }

    // Simulation: keep recalculating all gates until no output changes any more.
    public void simulate() throws InvalidCircuitException {
        for (int round = 0; round < 100; round++) {
            boolean changed = false;
            for (int i = 0; i < gates.size(); i++) {
                if (gates.get(i).update()) {
                    changed = true;
                }
            }
            if (!changed) {
                return;   // circuit is stable
            }
        }
        throw new InvalidCircuitException("Circuit does not settle (loop in the circuit?)");
    }

    // Prints every gate (uses the overridden display() of each gate).
    public void displayGates() {
        for (int i = 0; i < gates.size(); i++) {
            gates.get(i).display();
        }
    }

    // Tries every input combination and builds the truth table as text.
    public String buildTruthTable() throws InvalidCircuitException {
        int n = inputs.size();
        if (n == 0 || outputs.size() == 0) {
            throw new InvalidCircuitException("Circuit needs INPUT and OUTPUT lines");
        }
        if (n > 10) {
            throw new InvalidCircuitException("Too many inputs for a truth table (max 10)");
        }

        StringBuilder sb = new StringBuilder();
        for (int j = 0; j < n; j++) {
            sb.append(inputs.get(j).getName()).append(" ");
        }
        sb.append("| ");
        for (int j = 0; j < outputs.size(); j++) {
            sb.append(outputs.get(j).getName()).append(" ");
        }
        sb.append("\n");

        int rows = 1 << n;                         // BITWISE shift: 2 to the power n
        for (int row = 0; row < rows; row++) {
            for (int j = 0; j < n; j++) {
                int bit = (row >> (n - 1 - j)) & 1;   // BITWISE shift and AND
                inputs.get(j).setValue(bit == 1);
                sb.append(bit).append(" ");
            }
            simulate();
            sb.append("| ");
            for (int j = 0; j < outputs.size(); j++) {
                sb.append(outputs.get(j).getValue() ? "1" : "0").append(" ");
            }
            sb.append("\n");
        }
        return sb.toString();
    }
}
