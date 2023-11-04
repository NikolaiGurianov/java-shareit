package ru.practicum.shareitserver.exception;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ResponseBodyTest {

    @Test
    void testConstructorAndGetters() {
        String error = "Custom error message";
        ResponseBody responseBody = new ResponseBody(error);

        assertEquals(error, responseBody.getError());
    }
}