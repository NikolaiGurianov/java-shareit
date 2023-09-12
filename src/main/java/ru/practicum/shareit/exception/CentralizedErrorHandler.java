package ru.practicum.shareit.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.server.ResponseStatusException;

@Slf4j
@ControllerAdvice
public class CentralizedErrorHandler {


    @ExceptionHandler({ErrorException.class})
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ResponseEntity<?> handleUserException(final ErrorException e) {
        return new ResponseEntity<>(new ResponseBody(e.getMessage()), HttpStatus.valueOf(500));
    }

    @ExceptionHandler(NotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ResponseEntity<?> handleUserNotFoundException(final NotFoundException e) {
        return new ResponseEntity<>(new ResponseBody(e.getMessage()), HttpStatus.valueOf(404));
    }

    @ExceptionHandler(ValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<?> handleUserValidException(final ValidException e) {
        return new ResponseEntity<>(new ResponseBody(e.getMessage()), HttpStatus.valueOf(400));
    }

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<?> handleException(final ResponseStatusException e) {
        return new ResponseEntity<>(new ResponseBody(e.getReason()), e.getStatus());
    }

    @ExceptionHandler(UnknownStateException.class)
    public ResponseEntity<?> handleUnknownStateException(final UnknownStateException e) {
        return new ResponseEntity<>(new ResponseBody(e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);

    }


}


