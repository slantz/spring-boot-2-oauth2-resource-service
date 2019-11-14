package com.yourproject.resource.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.expression.method.MethodSecurityExpressionHandler;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.method.configuration.GlobalMethodSecurityConfiguration;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.provider.expression.OAuth2MethodSecurityExpressionHandler;

/**
 * WTF is this?
 *
 * @EnableResourceServer adds ability to check the OAuth2 tokens.
 * @EnableGlobalMethodSecurity(prePostEnabled = true) makes sure that @PreAuthorize annotation works.
 *
 * It's important that these 2 annotations come together on this config for #oauth2.hasScope to work or else just hasAuthority() will work.
 */
@Configuration
@EnableResourceServer // creates WebSecurityConfigurerAdapter with Order(3)
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class MethodSecurityConfig extends GlobalMethodSecurityConfiguration {

    @Override
    protected MethodSecurityExpressionHandler createExpressionHandler() {
        return new OAuth2MethodSecurityExpressionHandler();
    }
}
