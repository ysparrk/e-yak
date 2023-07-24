package now.eyak.member.exception;

public class AlreadySignUpException extends RuntimeException{

    public AlreadySignUpException() {
    }

    public AlreadySignUpException(String message) {
        super(message);
    }
}
