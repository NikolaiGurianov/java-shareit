package ru.practicum.shareit.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.server.ResponseStatusException;

@Slf4j
@ControllerAdvice
public class CentralizedErrorHandler {

    @ExceptionHandler(ErrorException.class)
    public ResponseEntity<?> handleUserException(final ErrorException e) {
        return new ResponseEntity<>(new ResponseBody(String.valueOf(e.getClass()), e.getMessage()), HttpStatus.valueOf(500));

    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<?> handleUserNotFoundException(final NotFoundException e) {
        return new ResponseEntity<>(new ResponseBody(String.valueOf(e.getClass()), e.getMessage()), HttpStatus.valueOf(404));
    }

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<?> handleException(final ResponseStatusException e) {
        return new ResponseEntity<>(new ResponseBody(String.valueOf(e.getClass()), e.getReason()), e.getStatus());
    }
}


