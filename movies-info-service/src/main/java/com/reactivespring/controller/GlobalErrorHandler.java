package com.reactivespring.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.reactivespring.exception.ErrorResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.support.WebExchangeBindException;

import java.util.List;

@ControllerAdvice(assignableTypes = MoviesInfoController.class)
@Slf4j
public class GlobalErrorHandler {

    @ExceptionHandler(WebExchangeBindException.class)
    public ResponseEntity<String> handleException(WebExchangeBindException e) throws JsonProcessingException {
        log.error("Caught handleException: {}", e.getMessage(), e);
        List<String> errorMsgs = e.getBindingResult().getAllErrors().stream()
                .map(DefaultMessageSourceResolvable::getDefaultMessage)
                .sorted().toList();
        ObjectMapper mapper = new ObjectMapper();
        ErrorResponse errors = new ErrorResponse("400", "Bad Request", errorMsgs);
        String errorMsg = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(errors);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorMsg);
    }
}
