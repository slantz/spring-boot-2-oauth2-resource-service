package com.yourproject.resource.advice;

import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.exc.MismatchedInputException;
import com.mongodb.MongoBulkWriteException;
import com.mongodb.MongoWriteException;
import com.mongodb.WriteError;
import com.yourproject.resource.error.response.ErrorResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

/**
 * Util controller advice class for handling exceptions and converting those to expected error responses.
 */
@ControllerAdvice
class RestApiControllerAdvice {

    private static final Logger LOG = LoggerFactory.getLogger(RestApiControllerAdvice.class);

    @ResponseBody
    @ExceptionHandler(NoSuchElementException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    ResponseEntity<Object> noSuchElementException(NoSuchElementException ex, WebRequest request) {
        ErrorResponse errorResponse = new ErrorResponse(HttpStatus.NOT_FOUND, ex.getMessage(), getRequestURI(request));
        return new ResponseEntity<>(errorResponse, new HttpHeaders(), errorResponse.getStatus());
    }

    @ResponseBody
    @ExceptionHandler(HttpClientErrorException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    ResponseEntity<Object> unauthorizedException(HttpClientErrorException ex, WebRequest request) {
        ErrorResponse errorResponse = new ErrorResponse(HttpStatus.UNAUTHORIZED, ex.getMessage(), getRequestURI(request));
        return new ResponseEntity<>(errorResponse, new HttpHeaders(), errorResponse.getStatus());
    }

    @ResponseBody
    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    ResponseEntity<Object> illegalArgumentException(IllegalArgumentException ex, WebRequest request) {
        ErrorResponse errorResponse = new ErrorResponse(HttpStatus.BAD_REQUEST, ex.getMessage(), getRequestURI(request));
        return new ResponseEntity<>(errorResponse, new HttpHeaders(), errorResponse.getStatus());
    }

    @ResponseBody
    @ExceptionHandler({MismatchedInputException.class, JsonMappingException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    ResponseEntity<Object> mismatchedInputException(JsonMappingException ex, WebRequest request) {
        ErrorResponse errorResponse = new ErrorResponse(HttpStatus.BAD_REQUEST,
                                                        ex.getMessage(),
                                                        getRequestURI(request));
        return new ResponseEntity<>(errorResponse, new HttpHeaders(), errorResponse.getStatus());
    }

    @ResponseBody
    @ExceptionHandler(MongoBulkWriteException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    ResponseEntity<Object> mongoBulkWriteException(MongoBulkWriteException ex, WebRequest request) {
        ErrorResponse errorResponse = new ErrorResponse(HttpStatus.BAD_REQUEST,
                                                        getMessagesFromBulkWriteErrors(ex).toString(),
                                                        getRequestURI(request));
        return new ResponseEntity<>(errorResponse, new HttpHeaders(), errorResponse.getStatus());
    }

    @ResponseBody
    @ExceptionHandler(MongoWriteException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    ResponseEntity<Object> mongoWriteException(MongoWriteException ex, WebRequest request) {
        ErrorResponse errorResponse = new ErrorResponse(HttpStatus.BAD_REQUEST,
                                                        ex.getMessage(),
                                                        getRequestURI(request));
        return new ResponseEntity<>(errorResponse, new HttpHeaders(), errorResponse.getStatus());
    }

    private static String getRequestURI(WebRequest request) {
        String requestURI = ((ServletWebRequest) request).getRequest().getRequestURI();

        try {
            return URLDecoder.decode(requestURI, "UTF-8");
        }
        catch (UnsupportedEncodingException e) {
            LOG.warn("Failed to decode uri context path", e);
        }

        return requestURI;
    }

    private static List<String> getMessagesFromBulkWriteErrors(MongoBulkWriteException ex) {
        return ex.getWriteErrors()
                .stream()
                .map(WriteError::getMessage)
                .collect(Collectors.toList());
    }
}
