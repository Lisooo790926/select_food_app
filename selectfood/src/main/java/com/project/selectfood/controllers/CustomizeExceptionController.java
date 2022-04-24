package com.project.selectfood.controllers;

import com.project.selectfood.payload.response.Response;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.Map;

@ControllerAdvice
@Slf4j
public class CustomizeExceptionController {

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Response> handleException(final Exception e){
        log.error("Raise Error ", e);
        return ResponseEntity.ok(
                Response.builder()
                        .message("Raising the error")
                        .data(Map.of("errorMessage", e.getMessage()))
                        .status(HttpStatus.BAD_REQUEST)
                        .build()
        );
    }
}
