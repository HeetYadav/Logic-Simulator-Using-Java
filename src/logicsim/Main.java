package logicsim;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Scanner;
import logicsim.circuit.Circuit;
import logicsim.circuit.CircuitParser;
import logicsim.circuit.InvalidCircuitException;
import logicsim.gates.AndGate;
import logicsim.gates.Gate;
import logicsim.gates.NandGate;
import logicsim.gates.NorGate;
import logicsim.gates.NotGate;
import logicsim.gates.OrGate;
import logicsim.gates.Wire;
import logicsim.gates.XorGate;

// Driver class with main() (Practical 1, 2, 6, 8).
// It imports the packages logicsim.gates and logicsim.circuit.
// Everything is interactive: the user chooses what to test and types the inputs.
public class Main {

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        int choice;

        // do-while loop + switch: main menu (Practical 2)
        do {
            System.out.println();
            System.out.println("===== Digital Logic Circuit Simulator =====");
            System.out.println("1. Single gate (you choose the gate and the inputs)");
            System.out.println("2. All gates together (you choose the inputs)");
            System.out.println("3. Half adder");
            System.out.println("4. Full adder");
            System.out.println("5. Circuit from a file");
            System.out.println("0. Exit");
            choice = readChoice(sc, "Enter choice: ");

            // MULTIPLE CATCH blocks for one try block (Practical 9)
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
                    case 5:
                        fileCircuit(sc);
                        break;
                    case 0:
                        System.out.println("Bye!");
                        break;
                    default:
                        System.out.println("Invalid choice, try again.");
                }
            } catch (InvalidCircuitException e) {
                System.out.println("Circuit error: " + e.getMessage());
            } catch (IOException e) {
                System.out.println("File error: " + e.getMessage());
            }
        } while (choice != 0);

        sc.close();
    }

    // ------------------------------------------------------------------
    // Menu 1: the user picks ONE gate, then works with it as often as wanted
    // ------------------------------------------------------------------
    private static void singleGate(Scanner sc) throws InvalidCircuitException, IOException {
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
                int n = 1;                                  // NOT has exactly 1 input
                if (!type.equals("NOT")) {
                    n = readRange(sc, "Number of inputs (2 to 4): ", 2, 4);
                }

                // Build a small circuit: inputs A, B, C, D ... and one output Y
                Circuit c = new Circuit();
                String[] inputNames = new String[n];
                for (int i = 0; i < n; i++) {
                    inputNames[i] = String.valueOf((char) ('A' + i));   // 'A', 'B', ...
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

    // ------------------------------------------------------------------
    // Menu 2: RUNTIME POLYMORPHISM (Practical 6/7)
    // An array of Gate references holds different gate objects.
    // update() calls the right evaluate() of each object (dynamic binding).
    // ------------------------------------------------------------------
    private static void allGates(Scanner sc) {
        do {
            System.out.println();
            Wire a = new Wire("A");
            Wire b = new Wire("B");
            a.setValue(readBit(sc, "A") == 1);
            b.setValue(readBit(sc, "B") == 1);
            Wire[] two = {a, b};
            Wire[] one = {a};          // the NOT gate uses only A

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

    // ------------------------------------------------------------------
    // Circuit builders
    // ------------------------------------------------------------------

    // Half adder built in code: SUM = A xor B, CARRY = A and B
    private static Circuit buildHalfAdder() throws InvalidCircuitException {
        Circuit c = new Circuit();
        c.addInput("A");
        c.addInput("B");
        c.addOutput("SUM");
        c.addOutput("CARRY");
        c.addGate("XOR", "SUM", new String[] {"A", "B"});
        c.addGate("AND", "CARRY", new String[] {"A", "B"});
        return c;
    }

    // Full adder built from XOR, AND and OR gates.
    private static Circuit buildFullAdder() throws InvalidCircuitException {
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

    // ------------------------------------------------------------------
    // Menu 5: load circuits from files (as many as the user wants)
    // ------------------------------------------------------------------
    private static void fileCircuit(Scanner sc) throws IOException, InvalidCircuitException {
        do {
            System.out.print("Circuit file (e.g. examples/full_adder.net): ");
            String fileName = readLine(sc);
            Circuit c = CircuitParser.parseFile(fileName);
            circuitMenu(sc, c, "Circuit from " + fileName);
        } while (askYesNo(sc, "Load another file? (y/n): "));
    }

    // ------------------------------------------------------------------
    // Menu used for EVERY circuit (gates, adders, files)
    // ------------------------------------------------------------------
    private static void circuitMenu(Scanner sc, Circuit c, String title)
            throws InvalidCircuitException, IOException {
        int choice;
        do {
            System.out.println();
            System.out.println("--- " + title + " ---");
            System.out.println("1. Enter my own inputs");
            System.out.println("2. Show truth table");
            System.out.println("3. Save truth table to a text file");
            System.out.println("0. Back");
            choice = readChoice(sc, "Enter choice: ");

            switch (choice) {
                case 1:
                    enterInputs(sc, c);
                    break;
                case 2:
                    System.out.print(c.buildTruthTable());
                    break;
                case 3:
                    saveTruthTable(sc, c);
                    break;
                case 0:
                    break;
                default:
                    System.out.println("Invalid choice, try again.");
            }
        } while (choice != 0);
    }

    // The user types a value (0 or 1) for every input; the circuit is simulated.
    // Repeats until the user answers n.
    private static void enterInputs(Scanner sc, Circuit c) throws InvalidCircuitException {
        if (c.getInputCount() == 0 || c.getOutputCount() == 0) {
            throw new InvalidCircuitException("Circuit needs INPUT and OUTPUT lines");
        }
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

    // FILE OUTPUT (Practical 5b): save the truth table in a text file
    private static void saveTruthTable(Scanner sc, Circuit c) throws InvalidCircuitException, IOException {
        String table = c.buildTruthTable();
        System.out.print("File name to save (just press Enter for truth_table.txt): ");
        String name = readLine(sc);
        if (name.length() == 0) {
            name = "truth_table.txt";
        }
        PrintWriter out = new PrintWriter(new FileWriter(name));
        out.print(table);
        out.close();
        System.out.println("Saved to " + name);
    }

    // ------------------------------------------------------------------
    // Input helpers: they keep asking until the user types something valid
    // ------------------------------------------------------------------

    // Reads one line. If the input stream is closed, the program ends cleanly.
    private static String readLine(Scanner sc) {
        if (!sc.hasNextLine()) {
            System.out.println();
            System.out.println("Input ended. Exiting.");
            System.exit(0);
        }
        return sc.nextLine().trim();
    }

    // Reads an integer; returns -1 if the user typed something that is not a number.
    private static int readChoice(Scanner sc, String prompt) {
        System.out.print(prompt);
        try {
            return Integer.parseInt(readLine(sc));
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    // Asks for 0 or 1 until the user types it correctly.
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

    // Asks for a whole number between min and max.
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

    // Asks a yes/no question until the user types y or n.
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
