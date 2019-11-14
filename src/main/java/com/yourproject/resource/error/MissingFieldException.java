package com.yourproject.resource.error;

public class MissingFieldException extends RuntimeException {

    private final String s;
    private final String field;

    /**
     * Constructs an instance of this class with the unmatched format specifier.
     */
    public MissingFieldException(String field, String s) {
        if (s == null)
            throw new NullPointerException();
        this.s = s;
        this.field = field;
    }

    public String getMessage() {
        return "DB object is missing ['" + field + "'] ['" + s + "']";
    }
}
