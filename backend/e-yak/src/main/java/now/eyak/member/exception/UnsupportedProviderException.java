package now.eyak.member.exception;

import now.eyak.exception.CustomException;
import org.springframework.http.HttpStatus;

public class UnsupportedProviderException extends CustomException {
    public UnsupportedProviderException() {
    }

    public UnsupportedProviderException(String message) {
        super(message);
    }

    @Override
    public HttpStatus getStatusCode() {
        return HttpStatus.BAD_REQUEST;
    }
}
