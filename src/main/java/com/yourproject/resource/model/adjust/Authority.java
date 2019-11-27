package com.yourproject.resource.model.adjust;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.security.core.GrantedAuthority;

/**
 * Authority model necessary for deserialization authorities from the authorization service.
 */
public class Authority implements GrantedAuthority {

    @JsonProperty
    private String authority;

    private Authority() {}

    /**
     * Create new {@link Authority} from the granted authority string and serializes it to JSON.
     * @param grantedAuthority
     */
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
