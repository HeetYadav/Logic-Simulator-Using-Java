package logicsim;

import java.util.Scanner;
import logicsim.circuit.Circuit;
import logicsim.circuit.InvalidCircuitException;
import logicsim.gates.AndGate;
import logicsim.gates.Gate;
import logicsim.gates.NandGate;
import logicsim.gates.NorGate;
import logicsim.gates.NotGate;
import logicsim.gates.OrGate;
import logicsim.gates.Wire;
import logicsim.gates.XorGate;

public class Main {

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        int choice;

        do {
            System.out.println();
            System.out.println("===== Digital Logic Circuit Simulator =====");
            System.out.println("1. Single gate (you choose the gate and the inputs)");
            System.out.println("2. All gates together (you choose the inputs)");
            System.out.println("3. Half adder");
            System.out.println("4. Full adder");
            System.out.println("0. Exit");
            choice = readChoice(sc, "Enter choice: ");

            try {
                switch (choice) {
                    case 1:
                        singleGate(sc);
                        break;
                    case 2:
                        allGates(sc);
                        break;
                    case 3:
                        circuitMenu(sc, buildHalfAdder(), "Half adder");
                        break;
                    case 4:
                        circuitMenu(sc, buildFullAdder(), "Full adder");
                        break;
                    case 0:
                        System.out.println("Bye!");
                        break;
                    default:
                        System.out.println("Invalid choice, try again.");
                }
            } catch (InvalidCircuitException e) {
                System.out.println("Circuit error: " + e.getMessage());
            }
        } while (choice != 0);

        sc.close();
    }

    private static void singleGate(Scanner sc) throws InvalidCircuitException {
        String[] types = {"AND", "OR", "XOR", "NAND", "NOR", "NOT"};
        int g;
        do {
            System.out.println();
            System.out.println("--- Choose a gate ---");
            for (int i = 0; i < types.length; i++) {
                System.out.println((i + 1) + ". " + types[i]);
            }
            System.out.println("0. Back to main menu");
            g = readChoice(sc, "Enter choice: ");

            if (g >= 1 && g <= types.length) {
                String type = types[g - 1];
                int n = 1;
                if (!type.equals("NOT")) {
                    n = readRange(sc, "Number of inputs (2 to 4): ", 2, 4);
                }

                Circuit c = new Circuit();
                String[] inputNames = new String[n];
                for (int i = 0; i < n; i++) {
                    inputNames[i] = String.valueOf((char) ('A' + i));
                    c.addInput(inputNames[i]);
                }
                c.addOutput("Y");
                c.addGate(type, "Y", inputNames);
                circuitMenu(sc, c, type + " gate");
            } else if (g != 0) {
                System.out.println("Invalid choice, try again.");
            }
        } while (g != 0);
    }

    private static void allGates(Scanner sc) {
        do {
            System.out.println();
            Wire a = new Wire("A");
            Wire b = new Wire("B");
            a.setValue(readBit(sc, "A") == 1);
            b.setValue(readBit(sc, "B") == 1);
            Wire[] two = {a, b};
            Wire[] one = {a};

            Gate[] gates = new Gate[6];
            gates[0] = new AndGate(new Wire("Y"), two);
            gates[1] = new OrGate(new Wire("Y"), two);
            gates[2] = new XorGate(new Wire("Y"), two);
            gates[3] = new NandGate(new Wire("Y"), two);
            gates[4] = new NorGate(new Wire("Y"), two);
            gates[5] = new NotGate(new Wire("Y"), one);

            System.out.println();
            for (int i = 0; i < gates.length; i++) {
                gates[i].update();
                gates[i].display();
            }
        } while (askYesNo(sc, "Try other inputs? (y/n): "));
    }

    static Circuit buildHalfAdder() throws InvalidCircuitException {
        Circuit c = new Circuit();
        c.addInput("A");
        c.addInput("B");
        c.addOutput("SUM");
        c.addOutput("CARRY");
        c.addGate("XOR", "SUM", new String[] {"A", "B"});
        c.addGate("AND", "CARRY", new String[] {"A", "B"});
        return c;
    }

    static Circuit buildFullAdder() throws InvalidCircuitException {
        Circuit c = new Circuit();
        c.addInput("A");
        c.addInput("B");
        c.addInput("CIN");
        c.addOutput("SUM");
        c.addOutput("COUT");
        c.addGate("XOR", "P", new String[] {"A", "B"});
        c.addGate("XOR", "SUM", new String[] {"P", "CIN"});
        c.addGate("AND", "G", new String[] {"A", "B"});
        c.addGate("AND", "T", new String[] {"P", "CIN"});
        c.addGate("OR", "COUT", new String[] {"G", "T"});
        return c;
    }

    private static void circuitMenu(Scanner sc, Circuit c, String title) throws InvalidCircuitException {
        int choice;
        do {
            System.out.println();
            System.out.println("--- " + title + " ---");
            System.out.println("1. Enter my own inputs");
            System.out.println("2. Show truth table");
            System.out.println("0. Back");
            choice = readChoice(sc, "Enter choice: ");

            switch (choice) {
                case 1:
                    enterInputs(sc, c);
                    break;
                case 2:
                    System.out.print(c.buildTruthTable());
                    break;
                case 0:
                    break;
                default:
                    System.out.println("Invalid choice, try again.");
            }
        } while (choice != 0);
    }

    private static void enterInputs(Scanner sc, Circuit c) throws InvalidCircuitException {
        do {
            System.out.println();
            for (int i = 0; i < c.getInputCount(); i++) {
                c.setInput(i, readBit(sc, c.getInputName(i)) == 1);
            }
            c.simulate();

            System.out.println("Internal gates:");
            c.displayGates();
            System.out.println("Outputs:");
            for (int i = 0; i < c.getOutputCount(); i++) {
                System.out.println("  " + c.getOutputName(i) + " = " + (c.getOutput(i) ? 1 : 0));
            }
        } while (askYesNo(sc, "Try other inputs? (y/n): "));
    }

    private static String readLine(Scanner sc) {
        if (!sc.hasNextLine()) {
            System.out.println();
            System.out.println("Input ended. Exiting.");
            System.exit(0);
        }
        return sc.nextLine().trim();
    }

    private static int readChoice(Scanner sc, String prompt) {
        System.out.print(prompt);
        try {
            return Integer.parseInt(readLine(sc));
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    private static int readBit(Scanner sc, String name) {
        int value = -1;
        while (value != 0 && value != 1) {
            value = readChoice(sc, "Value of " + name + " (0 or 1): ");
            if (value != 0 && value != 1) {
                System.out.println("Please type 0 or 1.");
            }
        }
        return value;
    }

    private static int readRange(Scanner sc, String prompt, int min, int max) {
        int value = min - 1;
        while (value < min || value > max) {
            value = readChoice(sc, prompt);
            if (value < min || value > max) {
                System.out.println("Please type a number from " + min + " to " + max + ".");
            }
        }
        return value;
    }

    private static boolean askYesNo(Scanner sc, String prompt) {
        while (true) {
            System.out.print(prompt);
            String answer = readLine(sc).toLowerCase();
            if (answer.equals("y") || answer.equals("yes")) {
                return true;
            }
            if (answer.equals("n") || answer.equals("no")) {
                return false;
            }
            System.out.println("Please type y or n.");
        }
    }
}
