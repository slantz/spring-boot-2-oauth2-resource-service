package com.yourproject.resource.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.autoconfigure.security.servlet.EndpointRequest;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;

/**
 * Configuration describing resource server.
 *
 * Adds basic HTTP web security to authenticate service and enables a Spring Security filter that authenticates requests
 * via an incoming OAuth2 token.
 */
@Configuration
@EnableWebSecurity
@EnableResourceServer
public class ResourceServerConfiguration extends ResourceServerConfigurerAdapter {

    private static final Logger LOG = LoggerFactory.getLogger(ResourceServerConfiguration.class);

    private static final String RESOURCE_ID = "any_your_project_id";

    private final TokenStore tokenStore;
    private final JwtAccessTokenConverter tokenConverter;

    @Autowired
    public ResourceServerConfiguration(TokenStore tokenStore, JwtAccessTokenConverter tokenConverter) {
        this.tokenStore = tokenStore;
        this.tokenConverter = tokenConverter;
    }

    /**
     * Some examples:
     * - .antMatchers(HttpMethod.GET, "/foo").hasAuthority("FOO_READ");
     * - .antMatchers("/**").authenticated();
     * - .antMatchers("/**").permitAll();
     *
     * @param http http security holding all the configurations.
     * @throws Exception in case something goes wrong.
     */
    @Override
    public void configure(HttpSecurity http) throws Exception {
        http.csrf().disable().authorizeRequests().requestMatchers(EndpointRequest.to("status", "info", "health")).permitAll().antMatchers("/**").authenticated();
    }


    @Override
    public void configure(ResourceServerSecurityConfigurer resources) {
        LOG.info(String.format("Configuring ResourceServerSecurityConfigurer and setting the resource id [%s]", RESOURCE_ID));

        resources.resourceId(RESOURCE_ID).tokenStore(this.tokenStore);
    }
}