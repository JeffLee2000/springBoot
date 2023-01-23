package com.example.chapter6.payload.response;


import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class ApiResponse {

    private final Boolean success;
    private final String data;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private final LocalDateTime timestamp;
    private final String cause;

    public ApiResponse(Boolean success, String data) {
        this.success = success;
        this.data = data;
        this.timestamp = LocalDateTime.now();
        this.cause = null;
    }

    public ApiResponse(Boolean success, String data, String cause) {
        this.success = success;
        this.data = data;
        this.cause = cause;
        this.timestamp = LocalDateTime.now();
    }
}