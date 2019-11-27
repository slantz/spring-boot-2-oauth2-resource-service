package com.yourproject.resource.constant;

/**
 * Util class with all possible authority ans scope check values for @PreAuthorize controller method's annotation.
 */
public final class AuthorizationGrant {

    private AuthorizationGrant() {}

    public static final String AUTHORITY_ADMIN = "hasAuthority('ADMIN')";
    public static final String AUTHORITY_USER = "hasAuthority('USER')";
}
