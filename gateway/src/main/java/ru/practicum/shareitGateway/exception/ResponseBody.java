package ru.practicum.shareitGateway.exception;

import lombok.Data;

@Data
public class ResponseBody {
    private String error;

    public ResponseBody(String error) {
        this.error = error;
    }

}