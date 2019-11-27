package com.yourproject.resource.util;

import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.Calendar;
import java.util.Date;

import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;

public final class TestUtil {
    public static <T> ResultMatcher getMatcherForOneItem(String jsonPath, T expected) {
        return MockMvcResultMatchers.jsonPath(jsonPath, is(expected));
    }

    public static ResultMatcher getMatcherForHasItems(String jsonPath, String... expected) {
        return MockMvcResultMatchers.jsonPath(jsonPath, hasItems(expected));
    }

    public static ResultMatcher getMatcherForHasSize(String jsonPath, int size) {
        return MockMvcResultMatchers.jsonPath(jsonPath, hasSize(size));
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
