package now.eyak.exceptionhandler;

import now.eyak.member.exception.AlreadySignUpException;
import now.eyak.member.exception.NoSuchMemberException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(NoSuchMemberException.class)
    public ResponseEntity NoSuchMemberExceptionHandler(NoSuchMemberException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("[Error]" + e.getMessage());
    }

    @ExceptionHandler(AlreadySignUpException.class)
    public ResponseEntity AlreadySignUpExceptionHandler(AlreadySignUpException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("[Error]" + e.getMessage());
    }
}
