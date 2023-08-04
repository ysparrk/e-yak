package now.eyak.exception;

public class NoPermissionException extends RuntimeException {
    public NoPermissionException() {
    }

    public NoPermissionException(String message) {
        super(message);
    }
}
