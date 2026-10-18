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
public class Main {

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        int choice;

        // do-while loop + switch: menu (Practical 2)
        do {
            System.out.println();
            System.out.println("===== Digital Logic Circuit Simulator =====");
            System.out.println("1. Gate demo (all gate types)");
            System.out.println("2. Half adder truth table");
            System.out.println("3. Full adder truth table");
            System.out.println("4. Load circuit from file -> truth table (saved to a text file)");
            System.out.println("5. Load circuit from file -> test your own inputs");
            System.out.println("0. Exit");
            System.out.print("Enter choice: ");

            try {
                choice = Integer.parseInt(sc.nextLine().trim());
            } catch (NumberFormatException e) {
                choice = -1;
            }

            // MULTIPLE CATCH blocks for one try block (Practical 9)
            try {
                switch (choice) {
                    case 1:
                        gateDemo();
                        break;
                    case 2:
                        System.out.print(buildHalfAdder().buildTruthTable());
                        break;
                    case 3:
                        System.out.print(buildFullAdder().buildTruthTable());
                        break;
                    case 4:
                        truthTableFromFile(sc);
                        break;
                    case 5:
                        testInputsFromFile(sc);
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

    // RUNTIME POLYMORPHISM (Practical 6/7): an array of Gate references
    // holds different gate objects; update() and display() call the right version.
    private static void gateDemo() {
        Wire a = new Wire("A");
        Wire b = new Wire("B");
        a.setValue(true);    // A = 1
        b.setValue(false);   // B = 0
        Wire[] two = {a, b};
        Wire[] one = {a};

        Gate[] gates = new Gate[6];
        gates[0] = new AndGate(new Wire("Y1"), two);
        gates[1] = new OrGate(new Wire("Y2"), two);
        gates[2] = new XorGate(new Wire("Y3"), two);
        gates[3] = new NandGate(new Wire("Y4"), two);
        gates[4] = new NorGate(new Wire("Y5"), two);
        gates[5] = new NotGate(new Wire("Y6"), one);

        for (int i = 0; i < gates.length; i++) {
            gates[i].update();
            gates[i].display();
        }
    }

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

    // Full adder built from two half adders and an OR gate.
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

    private static void truthTableFromFile(Scanner sc) throws IOException, InvalidCircuitException {
        System.out.print("Circuit file (e.g. examples/full_adder.net): ");
        String fileName = sc.nextLine().trim();
        Circuit c = CircuitParser.parseFile(fileName);
        String table = c.buildTruthTable();
        System.out.print(table);

        // FILE OUTPUT (Practical 5b): save the result in a text file
        String outName = "truth_table.txt";
        PrintWriter out = new PrintWriter(new FileWriter(outName));
        out.print(table);
        out.close();
        System.out.println("Saved to " + outName);
    }

    private static void testInputsFromFile(Scanner sc) throws IOException, InvalidCircuitException {
        System.out.print("Circuit file (e.g. examples/full_adder.net): ");
        String fileName = sc.nextLine().trim();
        Circuit c = CircuitParser.parseFile(fileName);

        for (int i = 0; i < c.getInputCount(); i++) {
            int value = -1;
            while (value != 0 && value != 1) {          // while loop: ask until 0 or 1
                System.out.print("Value of " + c.getInputName(i) + " (0 or 1): ");
                try {
                    value = Integer.parseInt(sc.nextLine().trim());
                } catch (NumberFormatException e) {
                    value = -1;
                }
            }
            c.setInput(i, value == 1);
        }
        c.simulate();
        c.displayGates();
        System.out.print("Outputs: ");
        for (int i = 0; i < c.getOutputCount(); i++) {
            System.out.print((c.getOutput(i) ? 1 : 0) + " ");
        }
        System.out.println();
    }
}
