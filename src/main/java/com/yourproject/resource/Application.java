package com.yourproject.resource;

import com.yourproject.resource.currency.CurrencyServiceImpl;
import com.yourproject.resource.sample.SampleServiceImpl;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.security.oauth2.client.OAuth2RestTemplate;
import org.springframework.security.oauth2.client.token.grant.client.ClientCredentialsResourceDetails;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableOAuth2Client;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;

/**
 * curl -X POST -vu web_app:111 -H "Accept: application/json" "http://localhost:9999/oauth/token" -d "grant_type=client_credentials" | jq .
 * curl -G -H "Authorization: Bearer <oauth_token>" "http://localhost:8080/resource-services/api/categories" | jq .
 */
@SpringBootApplication
@EnableOAuth2Client
public class Application implements CommandLineRunner {

	@Bean
	@ConfigurationProperties("security.oauth2.client")
	protected ClientCredentialsResourceDetails oAuthDetails() {
		return new ClientCredentialsResourceDetails();
	}

	@Bean
	protected RestTemplate restTemplate() {
		return new OAuth2RestTemplate(oAuthDetails());
	}

	@Bean
	CommandLineRunner init(CurrencyServiceImpl currencyService, SampleServiceImpl sampleService) {
		return (evt) -> DELETEME__dummyObjectsToDb.createObjects(currencyService, sampleService);
	}

	@Override
	public void run(String... args) {}

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}
}
