package now.eyak.social.exception;

import now.eyak.exception.CustomException;
import org.springframework.http.HttpStatus;

public class AlreadyFollowedException extends CustomException {

    public AlreadyFollowedException() {
    }

    public AlreadyFollowedException(String message) {
        super(message);
    }

    @Override
    public HttpStatus getStatusCode() {
        return HttpStatus.OK;
    }
}
