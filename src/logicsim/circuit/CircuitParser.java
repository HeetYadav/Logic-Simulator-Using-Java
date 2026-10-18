package logicsim.circuit;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

// Reads a circuit from a text file (STRING HANDLING + FILE INPUT, Practical 5).
//
// File format (one statement per line, # starts a comment):
//   INPUT  A B
//   OUTPUT SUM CARRY
//   XOR    SUM A B        <- GATETYPE  outputWire  inputWire...
//   AND    CARRY A B
public class CircuitParser {

    public static Circuit parseFile(String fileName) throws IOException, InvalidCircuitException {
        Circuit circuit = new Circuit();
        BufferedReader reader = new BufferedReader(new FileReader(fileName));
        try {
            String line;
            int lineNo = 0;
            while ((line = reader.readLine()) != null) {
                lineNo++;
                try {
                    parseLine(circuit, line);
                } catch (InvalidCircuitException e) {
                    // add the line number so the user can find the mistake
                    throw new InvalidCircuitException("line " + lineNo + ": " + e.getMessage());
                }
            }
        } finally {
            reader.close();
        }
        return circuit;
    }

    private static void parseLine(Circuit circuit, String line) throws InvalidCircuitException {
        int hash = line.indexOf('#');
        if (hash >= 0) {
            line = line.substring(0, hash);       // remove comment
        }
        line = line.trim();
        if (line.length() == 0) {
            return;                               // empty line
        }

        String[] words = line.split("\\s+");      // split on one or more spaces
        String keyword = words[0].toUpperCase();

        if (keyword.equals("INPUT") || keyword.equals("OUTPUT")) {
            if (words.length < 2) {
                throw new InvalidCircuitException(keyword + " needs at least one wire name");
            }
            for (int i = 1; i < words.length; i++) {
                if (keyword.equals("INPUT")) {
                    circuit.addInput(words[i]);
                } else {
                    circuit.addOutput(words[i]);
                }
            }
        } else {
            if (words.length < 3) {
                throw new InvalidCircuitException("gate line needs: TYPE output input...");
            }
            String[] inputNames = new String[words.length - 2];
            for (int i = 2; i < words.length; i++) {
                inputNames[i - 2] = words[i];
            }
            circuit.addGate(keyword, words[1], inputNames);
        }
    }
}
