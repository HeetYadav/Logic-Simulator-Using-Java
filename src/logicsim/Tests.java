package logicsim;

import logicsim.circuit.Circuit;
import logicsim.circuit.InvalidCircuitException;

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

    private static boolean throwsError(Circuit c, String type, String out, String[] in) {
        try {
            c.addGate(type, out, in);
            return false;
        } catch (InvalidCircuitException e) {
            return true;
        }
    }

    public static void main(String[] args) throws Exception {
        String[] types = {"AND", "OR", "XOR", "NAND", "NOR"};
        int[][] expected = {
            {0, 0, 0, 1},
            {0, 1, 1, 1},
            {0, 1, 1, 0},
            {1, 1, 1, 0},
            {1, 0, 0, 0}
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

        Circuit threeInput = new Circuit();
        threeInput.addInput("A");
        threeInput.addInput("B");
        threeInput.addInput("C");
        threeInput.addOutput("Y");
        threeInput.addGate("AND", "Y", new String[] {"A", "B", "C"});
        check(run(threeInput, 7) == 1 && run(threeInput, 6) == 0, "3-input AND gate");

        Circuit fa = Main.buildFullAdder();
        boolean faOk = true;
        for (int in = 0; in < 8; in++) {
            int ones = Integer.bitCount(in);
            int expectedOut = ((ones % 2) << 1) | (ones / 2);
            if (run(fa, in) != expectedOut) {
                faOk = false;
            }
        }
        check(faOk, "full adder: all 8 rows");

        Circuit ha = Main.buildHalfAdder();
        boolean haOk = true;
        for (int in = 0; in < 4; in++) {
            int ones = Integer.bitCount(in);
            int expectedOut = ((ones % 2) << 1) | (ones / 2);
            if (run(ha, in) != expectedOut) {
                haOk = false;
            }
        }
        check(haOk, "half adder: all 4 rows");

        String table = Main.buildFullAdder().buildTruthTable();
        check(table.split("\n").length == 9 && table.startsWith("A B CIN | SUM COUT"), "full adder truth table has header and 8 rows");

        check(throwsError(new Circuit(), "FOO", "Y", new String[] {"A", "B"}), "unknown gate type throws exception");
        check(throwsError(new Circuit(), "NOT", "Y", new String[] {"A", "B"}), "NOT with 2 inputs throws exception");
        check(throwsError(new Circuit(), "AND", "Y", new String[] {"A"}), "AND with 1 input throws exception");

        Circuit twoDrivers = new Circuit();
        twoDrivers.addGate("AND", "Y", new String[] {"A", "B"});
        check(throwsError(twoDrivers, "OR", "Y", new String[] {"A", "B"}), "two gates driving one wire throws exception");

        Circuit loop = new Circuit();
        loop.addInput("A");
        loop.addOutput("Y");
        loop.addGate("NOT", "Y", new String[] {"Y"});
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
}
