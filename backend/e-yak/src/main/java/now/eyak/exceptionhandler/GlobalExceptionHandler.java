package now.eyak.exceptionhandler;

import lombok.extern.slf4j.Slf4j;
import now.eyak.exception.CustomException;
import now.eyak.member.exception.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.NoSuchElementException;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(value = {
            IllegalArgumentException.class,
            NoSuchElementException.class,
    })
    public ResponseEntity exceptionHandler(Exception e) {
        log.info(addPrefix(e.getMessage()));

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(addPrefix(e.getMessage()));
    }

    @ExceptionHandler(value = {
            NoSuchMemberException.class,
            AlreadySignUpException.class,
            InvalidRefreshTokenException.class,
            InvalidAccessTokenException.class,
            DuplicatedNicknameException.class,
            UnsupportedProviderException.class,
    })
    public ResponseEntity customExceptionHandler(CustomException e) {
        log.info(addPrefix(e.getMessage()));

        return ResponseEntity.status(e.getStatusCode()).body(addPrefix(e.getMessage()));
    }

    private String addPrefix(String message) {
        return "[Error] " + message;
    }
}
