package com.yourproject.resource.error;

/**
 * Runtime exception for missing DB model instance.
 */
public class MissingDbModelInstanceException extends RuntimeException {

    private final String s;

    /**
     * Constructs an instance of this class with the unmatched format specifier.
     */
    public MissingDbModelInstanceException(String s) {
        if (s == null)
            throw new NullPointerException();
        this.s = s;
    }

    public String getMessage() {
        return "DB model instance is missing in when fetched by id ['" + s + "']";
    }
}
