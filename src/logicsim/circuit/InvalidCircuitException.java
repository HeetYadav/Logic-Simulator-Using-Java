package logicsim.circuit;

public class InvalidCircuitException extends Exception {
    private static final long serialVersionUID = 1L;

    public InvalidCircuitException(String message) {
        super(message);
    }
}
