package com.yourproject.resource.sample;

import com.yourproject.resource.currency.CurrencyService;
import com.yourproject.resource.error.MissingDbModelInstanceException;
import com.yourproject.resource.model.mongo.Currency;
import com.yourproject.resource.model.mongo.Sample;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.NoSuchElementException;

import static com.yourproject.resource.error.constant.ErrorMessages.CurrencyMessages.NO_ELEMENT_BY_USERNAME_EXCEPTION_MESSAGE;

@Service("sampleService")
public class SampleServiceImpl implements SampleService {

    @Value("${rest.api.auth-service-path}")
    private String authServiceApiUrl;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private SampleRepository sampleRepository;

    @Autowired
    private CurrencyService currencyService;

    @Override
    public List<Sample> getSamplesByUsername(String username) {
        return this.sampleRepository.findByUsername(username).orElseThrow(() -> new NoSuchElementException(NO_ELEMENT_BY_USERNAME_EXCEPTION_MESSAGE));
    }

    @Override
    public List<GrantedAuthority> getUsernameAuthorities(String username) {
        ResponseEntity<List<GrantedAuthority>> response =
                restTemplate
                        .exchange(this.authServiceApiUrl + "/users/username/" + username + "/authorities",
                                  HttpMethod.GET,
                                  null,
                                  new ParameterizedTypeReference<List<GrantedAuthority>>() {});

        return null;
    }

    private Currency getExistingCurrency(Currency currency) {
        String currencyCode = currency.getCode();
        Currency existingCurrency;

        try {
            existingCurrency = this.currencyService.getByCode(currencyCode);
        } catch (NoSuchElementException e) {
            throw new MissingDbModelInstanceException(String.format("Looks like no currency with id [%s] exists", currencyCode));
        }

        return existingCurrency;
    }
}
