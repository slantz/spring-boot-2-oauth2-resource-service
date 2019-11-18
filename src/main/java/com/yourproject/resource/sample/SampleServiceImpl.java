package com.yourproject.resource.sample;

import com.yourproject.resource.currency.CurrencyService;
import com.yourproject.resource.error.MissingDbModelInstanceException;
import com.yourproject.resource.model.adjust.Authority;
import com.yourproject.resource.model.mongo.Currency;
import com.yourproject.resource.model.mongo.Sample;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Date;
import java.util.List;
import java.util.NoSuchElementException;

import static com.yourproject.resource.error.constant.ErrorMessages.CurrencyMessages.NO_ELEMENT_BY_FIELD_EXCEPTION_MESSAGE;
import static com.yourproject.resource.error.constant.ErrorMessages.CurrencyMessages
        .NO_ELEMENT_BY_NAME_AND_DATE_EXCEPTION_MESSAGE;
import static com.yourproject.resource.error.constant.ErrorMessages.CurrencyMessages
        .NO_ELEMENT_BY_NAME_AND_INCLUSIVE_DATES_EXCEPTION_MESSAGE;
import static com.yourproject.resource.error.constant.ErrorMessages.CurrencyMessages
        .NO_ELEMENT_BY_NAME_AND_OVERLAPPING_DATES_EXCEPTION_MESSAGE;
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
    public List<Sample> getSamplesByUsernameAndPreciseDate(String username, Date date) {
        return this.sampleRepository.findByUsernameAndPreciseDate(username, date).orElseThrow(() -> new NoSuchElementException(NO_ELEMENT_BY_NAME_AND_DATE_EXCEPTION_MESSAGE));
    }

    @Override
    public List<Sample> getSamplesByInclusiveDateRangeAndUsername(String username, Date startDate, Date expiredDate) {
        return this.sampleRepository.findByInclusiveDateRangeAndUsername(username, startDate, expiredDate).orElseThrow(() -> new NoSuchElementException(NO_ELEMENT_BY_NAME_AND_INCLUSIVE_DATES_EXCEPTION_MESSAGE));
    }

    @Override
    public List<Sample> getSamplesByOverlappingDateRangeAndUsername(String username, Date startDate, Date expiredDate) {
        return this.sampleRepository.findByOverlappingDateRangeAndUsername(username, startDate, expiredDate).orElseThrow(() -> new NoSuchElementException(NO_ELEMENT_BY_NAME_AND_OVERLAPPING_DATES_EXCEPTION_MESSAGE));
    }

    @Override
    public List<Sample> getSamplesByTitle(String username, String title) {
        return this.sampleRepository.findByUsernameAndTitle(username, title).orElseThrow(() -> new NoSuchElementException(String.format(NO_ELEMENT_BY_FIELD_EXCEPTION_MESSAGE, "title")));
    }

    @Override
    public List<Sample> getSamplesByUsernameAndDateAndCurrencyCode(String username, Date startDate, Date expiredDate, String currencyCode) {
        return this.sampleRepository.findByUsernameAndDateAndCurrencyCode(username, startDate, expiredDate, currencyCode);
    }

    @Override
    public List<Authority> getUsernameAuthorities(String username) {
        return restTemplate
                .exchange(this.authServiceApiUrl + "/users/username/" + username + "/authorities",
                          HttpMethod.GET,
                          null,
                          new ParameterizedTypeReference<List<Authority>>() {
                          })
                .getBody();
    }

    @Override
    public List<Sample> create(List<Sample> samples) {
        return this.sampleRepository.saveAll(samples);
    }

    @Override
    public List<Sample> get() {
        return this.sampleRepository.findAll();
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
