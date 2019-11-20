package com.yourproject.resource.util;

import com.yourproject.resource.model.mongo.Currency;
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.Calendar;
import java.util.Date;
import java.util.Map;

import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.isEmptyString;

public final class TestUtil {

    public static final String CURRENCY_UAH_ID = "100500";
    public static final String CURRENCY_USD_ID = "100600";
    public static final String ANY_USERNAME = "WHOIS";

    public static ResultMatcher getMatcherForOneEmptyString(String jsonPath) {
        return MockMvcResultMatchers.jsonPath(jsonPath, isEmptyString());
    }

    public static <T> ResultMatcher getMatcherForOneItem(String jsonPath, T expected) {
        return MockMvcResultMatchers.jsonPath(jsonPath, is(expected));
    }

    public static ResultMatcher getMatcherForHasItems(String jsonPath, String... expected) {
        return MockMvcResultMatchers.jsonPath(jsonPath, hasItems(expected));
    }

    public static Currency getCurrency(String title) {
        Currency uah = new Currency("EUR", "€");
        uah.setId(CURRENCY_UAH_ID);

        Currency usd = new Currency("USD", "$");
        uah.setId(CURRENCY_USD_ID);

        Map<String, Currency> currencies = Map.of("EUR", uah, "USD", usd);
        return currencies.get(title);
    }

    public static Date addDays(Date date, int days) {
        Calendar c = Calendar.getInstance();
        c.setTime(date);

        // manipulate date
        c.add(Calendar.DATE, 1); //same with c.add(Calendar.DAY_OF_MONTH, 1);

        // convert calendar to date
        return c.getTime();
    }
}
