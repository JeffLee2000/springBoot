package com.example.chapter6.advice;

import com.example.chapter6.exception.BadRequestException;
import com.example.chapter6.payload.response.ApiResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class RestControlAdvice {

    private static final Logger logger = LoggerFactory.getLogger(RestControlAdvice.class);

    @ExceptionHandler(value = BadRequestException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiResponse handleBadRequestException(BadRequestException ex) {
        return new ApiResponse(false, ex.getMessage(), ex.getClass().getName());
    }
}
