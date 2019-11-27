package com.yourproject.resource.error.response;

import org.springframework.http.HttpStatus;

/**
 * Error response used in {@link com.yourproject.resource.advice.RestApiControllerAdvice}.
 */
public class ErrorResponse {
    private final HttpStatus status;
    private final String message;
    private String contextPath;

    public ErrorResponse(HttpStatus status, String message, String contextPath) {
        this.status = status;
        this.message = message;
        this.contextPath = contextPath;
    }

    public HttpStatus getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }

    public String getContextPath() {
        return contextPath;
    }
}
