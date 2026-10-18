package logicsim;

import logicsim.circuit.Circuit;
import logicsim.circuit.CircuitParser;
import logicsim.circuit.InvalidCircuitException;

// Very small test program (no JUnit). Run it: it prints PASS / FAIL for each check.
public class Tests {
    private static int passed = 0;
    private static int failed = 0;

    private static void check(boolean condition, String name) {
        if (condition) {
            passed++;
            System.out.println("PASS  " + name);
        } else {
            failed++;
            System.out.println("FAIL  " + name);
        }
    }

    // Applies inputs (bit pattern) to a circuit and returns outputs as a bit pattern.
    private static int run(Circuit c, int inputBits) throws InvalidCircuitException {
        int n = c.getInputCount();
        for (int j = 0; j < n; j++) {
            c.setInput(j, ((inputBits >> (n - 1 - j)) & 1) == 1);
        }
        c.simulate();
        int result = 0;
        for (int j = 0; j < c.getOutputCount(); j++) {
            result = (result << 1) | (c.getOutput(j) ? 1 : 0);
        }
        return result;
    }

    public static void main(String[] args) throws Exception {
        // 2-input gates, written as a netlist and checked for all 4 inputs
        String[] types = {"AND", "OR", "XOR", "NAND", "NOR"};
        int[][] expected = {
            {0, 0, 0, 1},   // AND  for 00 01 10 11
            {0, 1, 1, 1},   // OR
            {0, 1, 1, 0},   // XOR
            {1, 1, 1, 0},   // NAND
            {1, 0, 0, 0}    // NOR
        };
        for (int t = 0; t < types.length; t++) {
            Circuit c = new Circuit();
            c.addInput("A");
            c.addInput("B");
            c.addOutput("Y");
            c.addGate(types[t], "Y", new String[] {"A", "B"});
            boolean ok = true;
            for (int in = 0; in < 4; in++) {
                if (run(c, in) != expected[t][in]) {
                    ok = false;
                }
            }
            check(ok, types[t] + " gate truth table");
        }

        Circuit notCircuit = new Circuit();
        notCircuit.addInput("A");
        notCircuit.addOutput("Y");
        notCircuit.addGate("NOT", "Y", new String[] {"A"});
        check(run(notCircuit, 0) == 1 && run(notCircuit, 1) == 0, "NOT gate truth table");

        // Full adder from file: outputs are SUM, COUT -> compare with A+B+CIN
        Circuit fa = CircuitParser.parseFile("examples/full_adder.net");
        boolean faOk = true;
        for (int in = 0; in < 8; in++) {
            int ones = Integer.bitCount(in);
            int expectedOut = ((ones % 2) << 1) | (ones / 2);   // SUM bit then COUT bit
            if (run(fa, in) != expectedOut) {
                faOk = false;
            }
        }
        check(faOk, "full adder file: all 8 rows");

        // Half adder from file
        Circuit ha = CircuitParser.parseFile("examples/half_adder.net");
        check(run(ha, 3) == 1 && run(ha, 1) == 2, "half adder file");

        // Custom exception cases
        check(throwsError("examples/bad_gate.net"), "unknown gate type -> exception");
        check(throwsError("examples/bad_not.net"), "NOT with 2 inputs -> exception");

        Circuit loop = new Circuit();
        loop.addInput("A");
        loop.addOutput("Y");
        loop.addGate("NOT", "Y", new String[] {"Y"});    // NOT feeding itself never settles
        boolean loopDetected = false;
        try {
            loop.simulate();
        } catch (InvalidCircuitException e) {
            loopDetected = true;
        }
        check(loopDetected, "oscillating circuit is detected");

        System.out.println();
        System.out.println(passed + " passed, " + failed + " failed");
        if (failed > 0) {
            System.exit(1);
        }
    }

    private static boolean throwsError(String file) throws Exception {
        try {
            CircuitParser.parseFile(file);
            return false;
        } catch (InvalidCircuitException e) {
            return e.getMessage().startsWith("line ");
        }
    }
}
