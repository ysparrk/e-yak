package now.eyak.member.exception;

import now.eyak.exception.CustomException;
import org.springframework.http.HttpStatus;

public class NoSuchMemberException extends CustomException {
    public NoSuchMemberException() {
    }

    public NoSuchMemberException(String message) {
        super(message);
    }

    @Override
    public HttpStatus getStatusCode() {
        return HttpStatus.BAD_REQUEST;
    }
}
