package com.yourproject.resource.service;

import com.yourproject.resource.model.adjusted.Cash;
import com.yourproject.resource.model.rates.DailyCourse;
import com.yourproject.resource.model.rates.Rate;
import com.yourproject.resource.model.rates.RateRatio;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.TimeZone;
import java.util.stream.Collectors;

@Service("budgetService")
public class BudgetServiceImpl implements BudgetService {

    private static final Logger LOG = LoggerFactory.getLogger(BudgetServiceImpl.class);
    protected static final TimeZone TIMEZONE_UTC = TimeZone.getTimeZone("UTC");
    protected static final DateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");

    static {
        DATE_FORMAT.setTimeZone(TIMEZONE_UTC);
    }

    @Value("${rest.api.rates-service-path}")
    private String ratesServiceApiUrl;

    @Value("${rest.api.auth-service-path}")
    private String authServiceApiUrl;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private RecurringExpensesService recurringExpensesService;

    @Autowired
    private IncomeService incomeService;

    @Autowired
    private ExpenseService expenseService;

    @Autowired
    private SavingService savingService;

    @Override
    public BigDecimal getBudget(String username, Date startDate, Date endDate) {
        String defaultCurrency = restTemplate.getForObject(this.authServiceApiUrl + "/spenders/username/" + username + "/default-currency-title", String.class);

        List<Income> incomes = this.incomeService.findByOverlappingDateRangeAndSpenderUsername(startDate, endDate, username);

        List<RecurringExpense> recurringExpenses = this.recurringExpensesService.findByOverlappingDateRangeAndSpenderUsername(startDate, endDate, username);

        List<Expense> expenses = this.expenseService.findBySpenderUsernameAndDate(username, startDate, endDate);

        ResponseEntity<List<DailyCourse>> response =
                restTemplate
                        .exchange(this.ratesServiceApiUrl + "/daily-courses?exactDate=" + DATE_FORMAT.format(startDate) + "&currency=" + defaultCurrency,
                                  HttpMethod.GET,
                                  null,
                                  new ParameterizedTypeReference<List<DailyCourse>>() {});

        List<DailyCourse> dailyCourses = response.getBody();
        Optional<DailyCourse> dailyCourse = dailyCourses.stream().findFirst();

        List<Rate> ratesForDefaultCurrency = getRates(dailyCourse, defaultCurrency);
        List<RateRatio> ratiosForDefaultCurrency = getRateRatios(ratesForDefaultCurrency);

        List<BigDecimal> budgetComponents = new ArrayList<>();

        List<BigDecimal> allIncomesInDefaultCurrency = incomes.stream().map(income -> {
            List<Saving> savings = this.savingService.findByIncomeId(income.getId(), username);
            BigDecimal incomeSum = getValueInDefaultCurrency(defaultCurrency, ratesForDefaultCurrency, ratiosForDefaultCurrency, income);
            List<BigDecimal> sum = savings.stream().map(saving -> getValueInDefaultCurrency(defaultCurrency, ratesForDefaultCurrency, ratiosForDefaultCurrency, saving)).collect(Collectors.toList());
            return sum.stream().reduce(incomeSum, BigDecimal::subtract);
        }).collect(Collectors.toList());

        budgetComponents.addAll(allIncomesInDefaultCurrency);

        List<BigDecimal> allRecurringExpensesInDefaultCurrency = recurringExpenses.stream().map(recurringExpense -> getValueInDefaultCurrency(defaultCurrency, ratesForDefaultCurrency, ratiosForDefaultCurrency, recurringExpense).negate()).collect(Collectors.toList());
        budgetComponents.addAll(allRecurringExpensesInDefaultCurrency);

        List<BigDecimal> allExpensesInDefaultCurrency = expenses.stream().map(expense -> getValueInDefaultCurrency(defaultCurrency, ratesForDefaultCurrency, ratiosForDefaultCurrency, expense).negate()).collect(Collectors.toList());
        budgetComponents.addAll(allExpensesInDefaultCurrency);

        return budgetComponents.stream().reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private List<Rate> getRates(Optional<DailyCourse> dailyCourse, String defaultCurrency) {
        if (dailyCourse.isPresent()) {
            return dailyCourse.get().getRates().stream().filter(rate -> rate.getCurrency().equals(defaultCurrency)).collect(Collectors.toList());
        }
        else {
            return new ArrayList<>();
        }
    }

    private List<RateRatio> getRateRatios(List<Rate> ratesForDefaultCurrency) {
        if (!ratesForDefaultCurrency.isEmpty()) {
            return ratesForDefaultCurrency.get(0).getRatios();
        }
        else {
            return new ArrayList<>();
        }
    }

    private BigDecimal getValueInDefaultCurrency(String defaultCurrency, List<Rate> ratesForDefaultCurrency, List<RateRatio> ratiosForDefaultCurrency, Cash cash) {
        String valuableTitle = cash.getCurrency().getTitle();

        if (valuableTitle.equals(defaultCurrency)) {
            return cash.getCost();
        }

        // todo: exception should be thrown here that no rates are received
        if (ratesForDefaultCurrency.isEmpty() || ratiosForDefaultCurrency.isEmpty()) {
            return cash.getCost();
        }

        List<RateRatio> singletonListOfIncomesCurrencyRatio = ratiosForDefaultCurrency.stream().filter(ratio -> ratio.getCurrency().equals(valuableTitle)).collect(Collectors.toList());

        // todo: exception should be thrown here that no rates for specific currency exist on server
        if (singletonListOfIncomesCurrencyRatio.isEmpty()) {
            return cash.getCost();
        }

        return cash.getCost().divide(singletonListOfIncomesCurrencyRatio.get(0).getRatio(), RoundingMode.HALF_UP);
    }
}
