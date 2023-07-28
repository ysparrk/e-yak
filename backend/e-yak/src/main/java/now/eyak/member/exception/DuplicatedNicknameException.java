package now.eyak.member.exception;

public class DuplicatedNicknameException extends RuntimeException {
    public DuplicatedNicknameException() {
    }

    public DuplicatedNicknameException(String message) {
        super(message);
    }
}
