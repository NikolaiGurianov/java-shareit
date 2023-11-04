package ru.practicum.shareitserver.exception;

public class ErrorException extends RuntimeException {
    public ErrorException(String message) {
        super(message);
    }
}

