package com.yourproject.resource.util;

import com.yourproject.resource.model.adjusted.location.Latitude;
import com.yourproject.resource.model.adjusted.location.Longitude;
import com.yourproject.resource.model.constant.SavingType;
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.math.BigDecimal;
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
        Currency uah = new Currency("UAH", "₴");
        uah.setId(CURRENCY_UAH_ID);

        Currency usd = new Currency("USD", "$");
        uah.setId(CURRENCY_USD_ID);

        Map<String, Currency> currencies = Map.of("UAH", uah, "USD", usd);
        return currencies.get(title);
    }

    public static Period getMonthlyPeriod(String id, int value) {
        return getPeriod(id, "Monthly", "MONTH", true, value);
    }

    public static Period getMonthlyPeriod(int value) {
        return getPeriod("0", "Monthly", "MONTH", true, value);
    }

    public static Period getThisPeriod(String id, int value) {
        return getPeriod(id, "This month", "THIS", false, value);
    }

    public static Period getThisPeriod(int value) {
        return getPeriod("0", "This month", "THIS", false, value);
    }

    public static Income getMonthlyIncome(BigDecimal value, String currency, Date startDate, Date endDate, String username) {
        return getIncome("Monthly Income Title", value, getMonthlyPeriod(720), getCurrency(currency), startDate, endDate, username);
    }

    public static Income getMonthlyIncome(String id, BigDecimal value, String currency, Date startDate, Date endDate, String username) {
        Income income = getIncome("Monthly Income Title",
                                  value,
                                  getMonthlyPeriod(720),
                                  getCurrency(currency),
                                  startDate,
                                  endDate,
                                  username);

        income.setId(id);
        return income;
    }

    private static Income getIncome(String title, BigDecimal value, Period period, Currency currency, Date startDate, Date endDate, String username) {
        return new Income(title,
                          value,
                          period,
                          currency,
                          startDate,
                          endDate,
                          username);
    }

    public static Saving getAnyTypeTitleSaving(Income income, String currency, BigDecimal cost, String spenderUsername) {
        return getSaving(SavingType.SUM, "Any title", income, getCurrency(currency), cost, spenderUsername);
    }

    public static RecurringExpense getAnyTitlePeriodDescriptionRecurringExpense(String currency, BigDecimal cost, Date date, Date expiredDate, String spenderUsername) {
        return getRecurringExpense(getCurrency(currency), "Any title", getMonthlyPeriod(720), cost, date, expiredDate, "Any description", spenderUsername);
    }

    public static Expense getAnyTitleCategoryAndLocationExpense(Date date, BigDecimal cost, String currency, String spenderUsername) {
        return getExpense("Any title", "Any description", date, cost, getCurrency(currency), spenderUsername, getAnyCategory(), getAnyLocation());
    }

    public static Expense getAnyTitleExpense(Date date, BigDecimal cost, Currency currency, String spenderUsername, Category category, Location location) {
        return getExpense("Any title", "Any description", date, cost, currency, spenderUsername, category, location);
    }

    public static Expense getAnyTitleUserNameLocationExpense(Date date, BigDecimal cost, String currency, String category) {
        return getExpense("Any title", "Any description", date, cost, getCurrency(currency), ANY_USERNAME, getCategory(category), getAnyLocation());
    }

    public static Date addDays(Date date, int days) {
        Calendar c = Calendar.getInstance();
        c.setTime(date);

        // manipulate date
        c.add(Calendar.DATE, 1); //same with c.add(Calendar.DAY_OF_MONTH, 1);

        // convert calendar to date
        return c.getTime();
    }

    public static Category getCategory(String category) {
        return new Category(category);
    }

    private static Saving getSaving(SavingType type, String title, Income income, Currency currency, BigDecimal cost, String spenderUsername) {
        return new Saving(type, title, income, currency, cost, spenderUsername);
    }

    private static Expense getExpense(String title, String description, Date date, BigDecimal cost, Currency currency, String spenderUsername, Category category, Location location) {
        return new Expense(title, description, date, cost, currency, spenderUsername, category, location);
    }

    private static RecurringExpense getRecurringExpense(Currency currency, String title, Period period, BigDecimal cost, Date date, Date expiredDate, String description, String spenderUsername) {
        return new RecurringExpense(currency, title, period, cost, date, expiredDate, description, spenderUsername);
    }

    private static Period getPeriod(String id, String title, String key, boolean isRepeatable, int value) {
        Period period = new Period(title,
                                   key,
                                   isRepeatable,
                                   value);
        period.setId(id);
        return period;
    }

    private static Category getAnyCategory() {
        return new Category("FOOD");
    }

    private static Location getAnyLocation() {
        return new Location(new Latitude("45.22234"),
                            new Longitude("37.86953"),
                            "User",
                            "Falsburg",
                            "Laplandia",
                            "24");
    }
}
