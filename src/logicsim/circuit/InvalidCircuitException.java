package logicsim.circuit;

// CUSTOM EXCEPTION (Practical 9): thrown when the circuit description is wrong.
public class InvalidCircuitException extends Exception {
    private static final long serialVersionUID = 1L;

    public InvalidCircuitException(String message) {
        super(message);
    }
}
