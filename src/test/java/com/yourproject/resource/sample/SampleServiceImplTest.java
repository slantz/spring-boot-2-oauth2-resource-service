package com.yourproject.resource.sample;

import com.yourproject.resource.currency.CurrencyService;
import com.yourproject.resource.model.adjust.Authority;
import com.yourproject.resource.model.mongo.Currency;
import com.yourproject.resource.model.mongo.Sample;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.TimeZone;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

@RunWith(SpringRunner.class)
@TestPropertySource(properties = {
    "rest.api.auth-service-path=http://localhost/auth"
})
public class SampleServiceImplTest {

    private static final Currency CURRENCY = new Currency("USD", "symbol");
    private static final String SAMPLE_TITLE = "title";
    private static final String START_DATE_STRING = "2019-04-02T00:00:00.000Z";
    private static final String END_DATE_STRING = "2019-04-30T23:59:59.999Z";
    private static Date START_DATE;
    private static Date END_DATE;
    private static final String USERNAME = "MARIO";
    private static final String NOT_EXISTING_USERNAME = "SOMEUSER";

    private static final String ERROR_AUTHORIZATION_SERVICE_RESPONSE = "{\n" +
            "    \"timestamp\": \"2019-11-21T14:31:16.714+0000\",\n" +
            "    \"status\": 500,\n" +
            "    \"error\": \"Internal Server Error\",\n" +
            "    \"message\": \"401 null\",\n" +
            "    \"path\": \"/resource-services/admin/super-endpoint/" + NOT_EXISTING_USERNAME + "/authorities\"\n" +
            "}";

    private static final TimeZone TIMEZONE_UTC = TimeZone.getTimeZone("UTC");
    private static final DateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");

    static {
        DATE_FORMAT.setTimeZone(TIMEZONE_UTC);
    }

    @TestConfiguration
    static class BudgetServiceImplTestContextConfiguration {
        @Bean
        public SampleService budgetService() {
            return new SampleServiceImpl();
        }
    }

    @Autowired
    private SampleServiceImpl sampleService;

    @MockBean
    private SampleRepository sampleRepository;

    @MockBean
    private RestTemplate restTemplate;

    @MockBean
    private CurrencyService currencyService;

    @Before
    public void setUp() throws Exception {
        START_DATE = DATE_FORMAT.parse(START_DATE_STRING);
        END_DATE = DATE_FORMAT.parse(END_DATE_STRING);
        mockDefaultCurrencyRest();
        mockCurrencyService();
        mockSampleRepositoryFindByUsername(USERNAME);
    }

    @Test
    public void getSamplesByUsername() {
        List<Sample> samples = this.sampleService.getSamplesByUsername(USERNAME);

        assertThat(samples.size(), is(1));
        assertThat(samples.get(0).getTitle(), is(SAMPLE_TITLE));
        assertThat(samples.get(0).getCurrency(), is(CURRENCY));
        assertThat(samples.get(0).getDate(), is(START_DATE));
        assertThat(samples.get(0).getExpiredDate(), is(END_DATE));
        assertThat(samples.get(0).getUsername(), is(USERNAME));
    }

    @Test
    public void getUsernameAuthorities() {
        List<Authority> authorities = this.sampleService.getUsernameAuthorities(USERNAME);

        assertThat(authorities.size(), is(2));
        assertThat(authorities.get(0).getAuthority(), is("ADMIN"));
        assertThat(authorities.get(1).getAuthority(), is("GUEST"));
    }

    @Test(expected = HttpClientErrorException.class)
    public void getAuthoritiesExceptionAuthService() {
        mockAuthorizationError();
        this.sampleService.getUsernameAuthorities(NOT_EXISTING_USERNAME);
    }

    private void mockSampleRepositoryFindByUsername(String username) {
        Mockito
                .when(this.sampleRepository.findByUsername(username))
                .thenReturn(Optional.of(List.of(new Sample(SAMPLE_TITLE, CURRENCY, START_DATE, END_DATE, USERNAME))));
    }

    private void mockDefaultCurrencyRest() {
        Mockito
                .when(this.restTemplate.exchange("http://localhost/auth/users/username/" + USERNAME + "/authorities", HttpMethod.GET, null, new ParameterizedTypeReference<List<Authority>>() {}))
                .thenReturn(new ResponseEntity<>(List.of(new Authority("ADMIN"), new Authority("GUEST")), HttpStatus.OK));
    }

    private void mockAuthorizationError() {
        Mockito
                .when(this.restTemplate.exchange("http://localhost/auth/users/username/" + NOT_EXISTING_USERNAME + "/authorities", HttpMethod.GET, null, new ParameterizedTypeReference<List<Authority>>() {}))
                .thenThrow(new HttpClientErrorException(HttpStatus.UNAUTHORIZED, ERROR_AUTHORIZATION_SERVICE_RESPONSE));
    }

    private void mockCurrencyService() {
        Mockito
                .when(this.currencyService.getByCode(CURRENCY.getCode()))
                .thenReturn(CURRENCY);
    }
}