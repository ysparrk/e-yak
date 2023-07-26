package now.eyak.exceptionhandler;

import now.eyak.member.exception.AlreadySignUpException;
import now.eyak.member.exception.NoSuchMemberException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.reactive.function.client.WebClientResponseException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(NoSuchMemberException.class)
    public ResponseEntity NoSuchMemberExceptionHandler(NoSuchMemberException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(addPrefix(e.getMessage()));
    }

    @ExceptionHandler(AlreadySignUpException.class)
    public ResponseEntity AlreadySignUpExceptionHandler(AlreadySignUpException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(addPrefix(e.getMessage()));
    }

    @ExceptionHandler(WebClientResponseException.class)
    public ResponseEntity WebClientResponseExceptionHandler(WebClientResponseException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(addPrefix(e.getMessage()));
    }

    private String addPrefix(String message) {
        return "[Error] " + message;
    }
}
