package ru.practicum.shareit.exception;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.server.ResponseStatusException;

import static org.junit.jupiter.api.Assertions.*;

class CentralizedErrorHandlerTest {
    private CentralizedErrorHandler centralizedErrorHandler;

    @BeforeEach
    void setUp() {
        centralizedErrorHandler = new CentralizedErrorHandler();
    }

    @Test
    void testHandleUserException() {
        ErrorException errorException = new ErrorException("Custom error message");
        ResponseEntity<?> responseEntity = centralizedErrorHandler.handleUserException(errorException);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, responseEntity.getStatusCode());
        assertEquals("Custom error message", ((ResponseBody) responseEntity.getBody()).getError());
    }

    @Test
    void testHandleUserNotFoundException() {
        NotFoundException notFoundException = new NotFoundException("Not found error message");
        ResponseEntity<?> responseEntity = centralizedErrorHandler.handleUserNotFoundException(notFoundException);

        assertEquals(HttpStatus.NOT_FOUND, responseEntity.getStatusCode());
        assertEquals("Not found error message", ((ResponseBody) responseEntity.getBody()).getError());
    }

    @Test
    void testHandleUserValidException() {
        ValidException validException = new ValidException("Bad request error message");
        ResponseEntity<?> responseEntity = centralizedErrorHandler.handleUserValidException(validException);

        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
        assertEquals("Bad request error message", ((ResponseBody) responseEntity.getBody()).getError());
    }

    @Test
    void testHandleException() {
        ResponseStatusException responseStatusException =
                new ResponseStatusException(HttpStatus.BAD_GATEWAY, "Gateway error");
        ResponseEntity<?> responseEntity = centralizedErrorHandler.handleException(responseStatusException);

        assertEquals(HttpStatus.BAD_GATEWAY, responseEntity.getStatusCode());
        assertEquals("Gateway error", ((ResponseBody) responseEntity.getBody()).getError());
    }

    @Test
    void testHandleUnknownStateException() {
        UnknownStateException unknownStateException = new UnknownStateException("Unknown state error message");
        ResponseEntity<?> responseEntity = centralizedErrorHandler.handleUnknownStateException(unknownStateException);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, responseEntity.getStatusCode());
        assertEquals("Unknown state error message", ((ResponseBody) responseEntity.getBody()).getError());
    }
}