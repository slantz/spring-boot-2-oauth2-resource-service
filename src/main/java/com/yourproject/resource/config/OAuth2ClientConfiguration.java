package com.yourproject.resource.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.client.OAuth2RestTemplate;
import org.springframework.security.oauth2.client.token.grant.client.ClientCredentialsResourceDetails;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableOAuth2Client;

/**
 * Configuration for OAuth2 client to use the Authorization Code Grant from one or OAuth2 Authorization server.
 */
@Configuration
@EnableOAuth2Client
public class OAuth2ClientConfiguration {

    /**
     * Bean for authenticating resource service to be able to call the authorization service.
     *
     * @return bean with client credentials.
     */
    @Bean
    @ConfigurationProperties(prefix = "security.oauth2.client")
    public ClientCredentialsResourceDetails clientCredentialsResourceDetails() {
        return new ClientCredentialsResourceDetails();
    }

    /**
     * OAuth2 rest template bean that is used accross application to communicate with Authorization service
     * with client credentials grants.
     *
     * @return {@link OAuth2RestTemplate}.
     */
    @Bean
    public OAuth2RestTemplate clientCredentialsRestTemplate() {
        return new OAuth2RestTemplate(clientCredentialsResourceDetails());
    }
}
