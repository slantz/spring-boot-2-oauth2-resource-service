package com.yourproject.resource.error;

public class MissingIdException extends MissingFieldException {

    /**
     * Constructs an instance of this class with the unmatched format specifier.
     */
    public MissingIdException(String s) {
        super("id", s);
    }
}
