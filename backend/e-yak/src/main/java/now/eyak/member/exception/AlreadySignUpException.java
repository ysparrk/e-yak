package now.eyak.member.exception;

import now.eyak.exception.CustomException;
import org.springframework.http.HttpStatus;

public class AlreadySignUpException extends CustomException {

    public AlreadySignUpException() {
    }

    public AlreadySignUpException(String message) {
        super(message);
    }

    @Override
    public HttpStatus getStatusCode() {
        return HttpStatus.BAD_REQUEST;
    }
}
