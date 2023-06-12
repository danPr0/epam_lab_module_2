package com.epam.esm.controller;


import jakarta.validation.ConstraintViolationException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.Map;

/**
 * Controller class responsible for globally handling server exceptions.
 *
 * @author Danylo Proshyn
 */

@ControllerAdvice
public class ExceptionController {

    private final Logger logger = LogManager.getLogger(ExceptionController.class);

    @ExceptionHandler(value = {MethodArgumentNotValidException.class, MissingServletRequestParameterException.class,
            ConstraintViolationException.class})
    public ResponseEntity<?> invalidRequest(Exception ex) {

        logger.error(ex);

        return ResponseEntity.badRequest()
                .body(Map.of("errorMessage", "Cannot process the request.", "errorCode", "400"));
    }

    @ExceptionHandler(value = {Exception.class})
    public ResponseEntity<?> handleGeneralException(Exception ex) {

        logger.error(ex);

        return ResponseEntity.badRequest()
                .body(Map.of("errorMessage", "Something went wrong. Please try again.", "errorCode", "400"));
    }
}
