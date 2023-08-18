package now.eyak.member.exception;

import now.eyak.exception.CustomException;
import org.springframework.http.HttpStatus;

public class DuplicatedNicknameException extends CustomException {
    public DuplicatedNicknameException() {
    }

    public DuplicatedNicknameException(String message) {
        super(message);
    }

    @Override
    public HttpStatus getStatusCode() {
        return HttpStatus.BAD_REQUEST;
    }
}
