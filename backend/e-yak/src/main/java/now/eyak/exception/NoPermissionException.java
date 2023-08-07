package now.eyak.exception;

import org.springframework.http.HttpStatus;

public class NoPermissionException extends CustomException {

    public NoPermissionException() {
    }

    public NoPermissionException(String message) {
        super(message);
    }

    @Override
    public HttpStatus getStatusCode() {
        return HttpStatus.BAD_REQUEST;
    }
}
