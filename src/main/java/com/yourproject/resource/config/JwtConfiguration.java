package com.yourproject.resource.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.JwtTokenStore;
import org.springframework.util.FileCopyUtils;

import java.io.IOException;

/**
 * Configuration for JWT.
 *
 * Introduces:
 *  - token store.
 *  - reads public RSA key for the service and adds it to the JWT converter.
 */
@Configuration
public class JwtConfiguration {

    private static final Logger LOG = LoggerFactory.getLogger(JwtConfiguration.class);

    private static final String PUBLIC_KEY = "public.cert";

    @Autowired
    JwtAccessTokenConverter jwtAccessTokenConverter;

    @Bean
    @Qualifier("tokenStore")
    public TokenStore tokenStore() {
        LOG.info("Created JwtTokenStore");

        return new JwtTokenStore(this.jwtAccessTokenConverter);
    }

    @Bean
    protected JwtAccessTokenConverter jwtTokenEnhancer() {
        JwtAccessTokenConverter converter =  new JwtAccessTokenConverter();
        Resource resource = new ClassPathResource(PUBLIC_KEY);
        String publicKey;

        try {
            publicKey = new String(FileCopyUtils.copyToByteArray(resource.getInputStream()));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        converter.setVerifierKey(publicKey);

        return converter;
    }
}