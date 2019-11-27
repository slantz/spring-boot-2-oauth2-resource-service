package com.yourproject.resource.sample;

import com.yourproject.resource.currency.CurrencyService;
import com.yourproject.resource.error.MissingDbModelInstanceException;
import com.yourproject.resource.error.MissingDbRefException;
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

/**
 * Service implementing {@link SampleService}.
 * Gets {@link Sample} from DB.
 * Gets {@link Authority} from the authorization service.
 */
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

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Sample> getSamplesByUsername(String username) {
        return this.sampleRepository.findByUsername(username).orElseThrow(() -> new NoSuchElementException(NO_ELEMENT_BY_USERNAME_EXCEPTION_MESSAGE));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Sample> getSamplesByUsernameAndPreciseDate(String username, Date date) {
        return this.sampleRepository.findByUsernameAndPreciseDate(username, date).orElseThrow(() -> new NoSuchElementException(NO_ELEMENT_BY_NAME_AND_DATE_EXCEPTION_MESSAGE));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Sample> getSamplesByInclusiveDateRangeAndUsername(String username, Date startDate, Date expiredDate) {
        return this.sampleRepository.findByInclusiveDateRangeAndUsername(username, startDate, expiredDate).orElseThrow(() -> new NoSuchElementException(NO_ELEMENT_BY_NAME_AND_INCLUSIVE_DATES_EXCEPTION_MESSAGE));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Sample> getSamplesByOverlappingDateRangeAndUsername(String username, Date startDate, Date expiredDate) {
        return this.sampleRepository.findByOverlappingDateRangeAndUsername(username, startDate, expiredDate).orElseThrow(() -> new NoSuchElementException(NO_ELEMENT_BY_NAME_AND_OVERLAPPING_DATES_EXCEPTION_MESSAGE));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Sample> getSamplesByTitle(String username, String title) {
        return this.sampleRepository.findByUsernameAndTitle(username, title).orElseThrow(() -> new NoSuchElementException(String.format(NO_ELEMENT_BY_FIELD_EXCEPTION_MESSAGE, "title")));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Sample> getSamplesByUsernameAndDateAndCurrencyCode(String username, Date startDate, Date expiredDate, String currencyCode) {
        return this.sampleRepository.findByUsernameAndDateAndCurrencyCode(username, startDate, expiredDate, currencyCode);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Authority> getUsernameAuthorities(String username) {
        return restTemplate
                .exchange(this.authServiceApiUrl + "/users/username/" + username + "/authorities",
                          HttpMethod.GET,
                          null,
                          new ParameterizedTypeReference<List<Authority>>() {})
                .getBody();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Sample> create(List<Sample> samples) {
        validateSamples(samples);
        return this.sampleRepository.saveAll(samples);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Sample> create(List<Sample> samples, String username) {
        validateSamples(samples);
        updateSamplesWithUsername(samples, username);
        return this.sampleRepository.saveAll(samples);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Sample> get() {
        return this.sampleRepository.findAll();
    }

    /**
     * Util method to set username from JWT to all samples that are gonna be created and to bind those to particular user.
     *
     * @param newSamples list of {@link Sample} to be created.
     * @param username username to bind to new {@link Sample}.
     */
    private void updateSamplesWithUsername(List<Sample> newSamples, String username) {
        newSamples.forEach(sample -> sample.setUsername(username));
    }

    /**
     * Validate samples for and BD refs to be existing in DB before storing new {@link Sample}s.
     *
     * @param newSamples list of {@link Sample} to be created.
     */
    private void validateSamples(List<Sample> newSamples) {
        newSamples.forEach(sample -> {

            Currency currency = sample.getCurrency();

            if (currency == null) {
                throw new MissingDbRefException("Some currency not passed");
            }

            Currency existingCurrency = getExistingCurrency(currency);

            sample.setCurrency(existingCurrency);
        });
    }

    /**
     * Util method to call {@link CurrencyService} and find {@link Currency} by code to check whether it exists in DB.
     *
     * @param currency {@link Currency} to be checked against DB.
     *
     * @return {@link Currency} if it exists in DB or else through a {@link MissingDbModelInstanceException}.
     */
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
