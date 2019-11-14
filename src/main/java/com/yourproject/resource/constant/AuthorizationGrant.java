package com.yourproject.resource.constant;

public final class AuthorizationGrant {

    private AuthorizationGrant() {}

    public static final String AUTHORITY_ADMIN = "hasAuthority('ADMIN')";
    public static final String AUTHORITY_USER = "hasAuthority('USER')";
}
