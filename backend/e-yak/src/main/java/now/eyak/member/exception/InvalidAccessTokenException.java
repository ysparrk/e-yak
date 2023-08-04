package now.eyak.member.exception;

public class InvalidAccessTokenException extends RuntimeException {

    public InvalidAccessTokenException(String message) {
        super(message);
    }
}
