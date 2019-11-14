package com.yourproject.resource.model.response;

import org.springframework.http.HttpStatus;

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
