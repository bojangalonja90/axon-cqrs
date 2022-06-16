package com.axoncqrs.products.core.errorhandling;

import org.axonframework.commandhandling.CommandExecutionException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

import java.util.Date;

@ControllerAdvice
public class ProductSeviceErrorHandler {

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<ErrorMessage> handleIllegalStateException(IllegalStateException ie, WebRequest request){

        ErrorMessage errorMessage = new ErrorMessage(new Date(), ie.getMessage());

        return new ResponseEntity<ErrorMessage>(errorMessage, new HttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorMessage> handleOtherExceptions(Exception e, WebRequest request){
        ErrorMessage errorMessage = new ErrorMessage(new Date(), e.getMessage());

        return new ResponseEntity<ErrorMessage>(errorMessage, new HttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(CommandExecutionException.class)
    public ResponseEntity<ErrorMessage> handleCommandExecutionExceptions(CommandExecutionException e, WebRequest request){
        ErrorMessage errorMessage = new ErrorMessage(new Date(), e.getMessage());

        return new ResponseEntity<ErrorMessage>(errorMessage, new HttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
