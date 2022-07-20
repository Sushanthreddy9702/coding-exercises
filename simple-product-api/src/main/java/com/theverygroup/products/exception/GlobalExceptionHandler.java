package com.theverygroup.products.exception;

import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@Order(Ordered.HIGHEST_PRECEDENCE)
@ControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(ResourceExistsException.class)
    public ResponseEntity<Object> handleResourceExistsException(ResourceExistsException ex) {
        return buildResponseEntity(new ApiMsg(HttpStatus.BAD_REQUEST, ex.getMessage()));
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<Object> handleResourceNotFoundException(ResourceNotFoundException ex) {
        return buildResponseEntity(new ApiMsg(HttpStatus.BAD_REQUEST, ex.getMessage()));
    }

    private ResponseEntity<Object> buildResponseEntity(ApiMsg apiMsg) {
        return new ResponseEntity<>(apiMsg, apiMsg.getStatus());
    }

}
