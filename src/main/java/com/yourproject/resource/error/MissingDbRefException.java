package com.yourproject.resource.error;

public class MissingDbRefException extends RuntimeException {

    private final String s;

    /**
     * Constructs an instance of this class with the unmatched format specifier.
     */
    public MissingDbRefException(String s) {
        if (s == null)
            throw new NullPointerException();
        this.s = s;
    }

    public String getMessage() {
        return "DB reference is missing in deserialized JSON ['" + s + "']";
    }
}