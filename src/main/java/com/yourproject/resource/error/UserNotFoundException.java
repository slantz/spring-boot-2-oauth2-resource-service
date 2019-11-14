package com.yourproject.resource.error;

public class UserNotFoundException extends RuntimeException {
    public UserNotFoundException(String userId) {
        super("could not find user [" + userId + "]");
    }
}
