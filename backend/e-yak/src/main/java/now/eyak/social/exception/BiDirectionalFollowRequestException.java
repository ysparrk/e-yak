package now.eyak.social.exception;

import now.eyak.exception.CustomException;
import org.springframework.http.HttpStatus;

public class BiDirectionalFollowRequestException extends CustomException {
    public BiDirectionalFollowRequestException() {
    }

    public BiDirectionalFollowRequestException(String message) {
        super(message);
    }

    @Override
    public HttpStatus getStatusCode() {
        return HttpStatus.OK;
    }
}
