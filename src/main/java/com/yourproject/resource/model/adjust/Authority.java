package com.yourproject.resource.model.adjust;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.security.core.GrantedAuthority;

public class Authority implements GrantedAuthority {

    @JsonProperty
    private String authority;

    private Authority() {}

    @JsonCreator
    public Authority(String grantedAuthority) {
        this.authority = grantedAuthority;
    }

    public void setAuthority(String authority) {
        this.authority = authority;
    }

    @Override
    public String getAuthority() {
        return this.authority;
    }
}
