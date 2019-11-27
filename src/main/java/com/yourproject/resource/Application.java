package com.yourproject.resource;

import com.yourproject.resource.currency.CurrencyServiceImpl;
import com.yourproject.resource.sample.SampleServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.security.oauth2.client.OAuth2RestTemplate;
import org.springframework.security.oauth2.client.token.grant.client.ClientCredentialsResourceDetails;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableOAuth2Client;
import org.springframework.web.client.RestTemplate;

/**
 * Sample Spring Boot Resource server.
 *
 * It uses public RSA key to decrypt JWT tokens for users or clients created
 * in the {@see <a href="https://github.com/slantz/spring-boot-2-oauth2-jwt-docker-mongo">Authorization service</a>}.
 *
 * It gets the list of {@link com.yourproject.resource.model.adjust.Authority} from the Authorization service for current user.
 *
 * Stores and gets {@link com.yourproject.resource.model.mongo.Sample}s.
 *
 * Provides ADMIN authority functionality API for administrator users to manipulate {@link com.yourproject.resource.model.mongo.Sample} for any user.
 *
 * Sample communication to this resource service.
 *
 * curl -X POST -vu spring_boot_resource_service:222 -H "Accept: application/json" "http://localhost:9999/oauth/token" -d "grant_type=client_credentials" | jq .
 * curl -G -H "Authorization: Bearer <oauth_token>" "http://localhost:8080/resource-services/samples" | jq .
 */
@SpringBootApplication
@EnableOAuth2Client
public class Application implements CommandLineRunner {

	@Autowired
	private DELETEME__dummyObjectsToDb deleteme__dummyObjectsToDb;

    /**
     * Bean for authenticating resource service to be able to call the authoriation service.
     *
     * @return bean with client credentials.
     */
	@Bean
	@ConfigurationProperties("security.oauth2.client")
	protected ClientCredentialsResourceDetails oAuthDetails() {
		return new ClientCredentialsResourceDetails();
	}

	@Bean
	protected RestTemplate restTemplate() {
		return new OAuth2RestTemplate(oAuthDetails());
	}

    /**
     * TODO: remove this method, for DEMO purposes only.
     *
     * @param currencyService {@link com.yourproject.resource.currency.CurrencyService}.
     * @param sampleService {@link com.yourproject.resource.sample.SampleService}.
     *
     * @return {@link CommandLineRunner}.
     */
	@Bean
	CommandLineRunner init(CurrencyServiceImpl currencyService, SampleServiceImpl sampleService) {
		return (evt) -> deleteme__dummyObjectsToDb.createObjects(currencyService, sampleService);
	}

	@Override
	public void run(String... args) {}

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}
}
