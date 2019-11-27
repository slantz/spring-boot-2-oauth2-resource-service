package com.yourproject.resource;

import com.yourproject.resource.currency.CurrencyServiceImpl;
import com.yourproject.resource.model.mongo.Currency;
import com.yourproject.resource.model.mongo.Sample;
import com.yourproject.resource.sample.SampleServiceImpl;
import org.springframework.stereotype.Component;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

/**
 * DEMO dummy component to fill DB with some {@link Currency}s and {@link Sample}s.
 */
@Component
public class DELETEME__dummyObjectsToDb {

    private static final String ADMIN = "admin";
    private static final String GUEST = "guest";

    private static final TimeZone TIMEZONE_UTC = TimeZone.getTimeZone("UTC");
    private static final DateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");

    static {
        DATE_FORMAT.setTimeZone(TIMEZONE_UTC);
    }

    public void createObjects(CurrencyServiceImpl currencyService, SampleServiceImpl sampleService) {
        Currency USD = new Currency("USD", "$");
        Currency EUR = new Currency("EUR", "€");

        List<Currency> currencies;

        try {
            currencies = currencyService.create(List.of(USD, EUR));
        }
        catch (Exception e) {
            return;
        }

        Sample SUPER = new Sample("super", currencies.get(0), formatDate("2019-01-01"), formatDate("2019-02-01"), ADMIN);
        Sample TITLE = new Sample("title", currencies.get(1), formatDate("2019-01-01"), formatDate("2019-02-01"), ADMIN);
        Sample THAT = new Sample("that", currencies.get(0), formatDate("2019-02-01"), formatDate("2019-03-01"), ADMIN);
        Sample DESCRIBES = new Sample("describes", currencies.get(0), formatDate("2019-03-01"), formatDate("2019-04-01"), ADMIN);
        Sample THIS = new Sample("this", currencies.get(1), formatDate("2019-03-01"), formatDate("2019-04-01"), ADMIN);
        Sample RANDOM = new Sample("random", currencies.get(1), formatDate("2019-04-01"), formatDate("2019-05-01"), ADMIN);
        Sample MODEL = new Sample("model", currencies.get(1), formatDate("2019-05-01"), formatDate("2019-06-01"), ADMIN);
        Sample AND = new Sample("and", currencies.get(1), formatDate("2019-01-01"), formatDate("2019-02-01"), GUEST);
        Sample FULFILLS = new Sample("fulfills", currencies.get(0), formatDate("2019-02-01"), formatDate("2019-03-01"), GUEST);
        Sample DATABASE = new Sample("database", currencies.get(0), formatDate("2019-03-01"), formatDate("2019-04-01"), GUEST);
        Sample WITH = new Sample("with", currencies.get(1), formatDate("2019-04-01"), formatDate("2019-05-01"), GUEST);
        Sample DATA = new Sample("data", currencies.get(1), formatDate("2019-05-01"), formatDate("2019-06-01"), GUEST);

        sampleService.create(List.of(SUPER, TITLE, THAT, DESCRIBES, THIS, RANDOM, MODEL, AND, FULFILLS, DATABASE, WITH, DATA));
    }

    private static Date formatDate(String date) {
        try {
            return DATE_FORMAT.parse(date + "T00:00:00.000Z");
        }
        catch (ParseException e) {
            return new Date();
        }
    }
}
