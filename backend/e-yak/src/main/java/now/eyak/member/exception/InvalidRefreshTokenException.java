package now.eyak.member.exception;

import now.eyak.exception.CustomException;
import org.springframework.http.HttpStatus;

public class InvalidRefreshTokenException extends CustomException {
    public InvalidRefreshTokenException() {
    }

    public InvalidRefreshTokenException(String message) {
        super(message);
    }

    @Override
    public HttpStatus getStatusCode() {
        return HttpStatus.BAD_REQUEST;
    }
}
