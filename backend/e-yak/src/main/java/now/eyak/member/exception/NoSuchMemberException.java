package now.eyak.member.exception;

public class NoSuchMemberException extends RuntimeException {
    public NoSuchMemberException() {
    }

    public NoSuchMemberException(String message) {
        super(message);
    }
}
