package now.eyak.survey.exception;

import now.eyak.exception.CustomException;
import org.springframework.http.HttpStatus;

public class DuplicatedContentResultException extends CustomException {


    public DuplicatedContentResultException() {
    }

    public DuplicatedContentResultException(String message) {
        super(message);
    }

    @Override
    public HttpStatus getStatusCode() {
        return HttpStatus.BAD_REQUEST;
    }
}
