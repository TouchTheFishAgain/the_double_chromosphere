package example.ljava.exception;

public class RoutineException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public RoutineException(String msg) {
        super(msg);
    }
}
