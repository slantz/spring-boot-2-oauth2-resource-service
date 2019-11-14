package com.yourproject.resource.service;

import com.yourproject.resource.model.rates.DailyCourse;
import com.yourproject.resource.model.rates.Rate;
import com.yourproject.resource.model.rates.RateRatio;
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
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import static com.yourproject.resource.service.BudgetServiceImpl.DATE_FORMAT;
import static com.yourproject.resource.util.TestUtil.addDays;
import static com.yourproject.resource.util.TestUtil.getAnyTitleCategoryAndLocationExpense;
import static com.yourproject.resource.util.TestUtil.getAnyTitlePeriodDescriptionRecurringExpense;
import static com.yourproject.resource.util.TestUtil.getAnyTypeTitleSaving;
import static com.yourproject.resource.util.TestUtil.getMonthlyIncome;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

@RunWith(SpringRunner.class)
@TestPropertySource(properties = {
    "rest.api.rates-service-path=http://localhost/rates",
    "rest.api.auth-service-path=http://localhost/auth"
})
public class BudgetServiceImplTest {

    private static final String DEFAULT_USER_CURRENCY = "UAH";
    private static final String DIFFERENT_CURRENCY = "USD";
    private static final String NOT_USED_CURRENCY = "EUR";
    private static final String START_DATE_STRING = "2019-04-02T00:00:00.000Z";
    private static final String END_DATE_STRING = "2019-04-30T23:59:59.999Z";
    private static Date START_DATE;
    private static Date END_DATE;
    private static final String USERNAME = "MARIO";
    private static final String NOT_EXISTING_USERNAME = "PISTON";

    private static final String ERROR_AUTHORIZATION_SERVICE_RESPONSE = "{\"error\": \"insufficient_scope\",\"error_description\": \"Insufficient scope for this resource\",\"scope\": \"WASTED_RESOURCE_SERVICE\"}";

    @TestConfiguration
    static class BudgetServiceImplTestContextConfiguration {
        @Bean
        public BudgetService budgetService() {
            return new BudgetServiceImpl();
        }
    }

    @Autowired
    private BudgetServiceImpl budgetService;

    @MockBean
    private RestTemplate restTemplate;

    @MockBean
    private RecurringExpensesService recurringExpensesService;

    @MockBean
    private IncomeService incomeService;

    @MockBean
    private ExpenseService expenseService;

    @MockBean
    private SavingService savingService;

    @Before
    public void setUp() throws Exception {
        START_DATE = DATE_FORMAT.parse(START_DATE_STRING);
        END_DATE = DATE_FORMAT.parse(END_DATE_STRING);
        mockDefaultCurrencyRest();
        mockDailyCoursesRest();
    }

    @Test
    public void getBudgetOnlyIncomesInDefaultCurrency() {
        Income incomeRegular = getMonthlyIncome("REGULAR_ID", BigDecimal.TEN, DEFAULT_USER_CURRENCY, START_DATE, END_DATE, USERNAME);
        Income incomeOnce = getMonthlyIncome("ONCE_ID", BigDecimal.ONE, DEFAULT_USER_CURRENCY, START_DATE, END_DATE, USERNAME);

        mockIncomeService(Arrays.asList(incomeRegular, incomeOnce));
        mockRecurringExpensesService(Collections.emptyList());
        mockExpenseService(Collections.emptyList());
        mockSavingService(incomeRegular, Collections.emptyList());
        mockSavingService(incomeOnce, Collections.emptyList());

        BigDecimal budget = this.budgetService.getBudget(USERNAME, START_DATE, END_DATE);

        assertThat(budget, is(BigDecimal.valueOf(11)));
    }

    @Test
    public void getBudgetOnlyIncomesInDifferentCurrencies() {
        Income incomeRegular = getMonthlyIncome("REGULAR_ID", BigDecimal.TEN, DIFFERENT_CURRENCY, START_DATE, END_DATE, USERNAME);
        Income incomeOnce = getMonthlyIncome("ONCE_ID", BigDecimal.ONE, DEFAULT_USER_CURRENCY, START_DATE, END_DATE, USERNAME);

        mockIncomeService(Arrays.asList(incomeRegular, incomeOnce));
        mockRecurringExpensesService(Collections.emptyList());
        mockExpenseService(Collections.emptyList());
        mockSavingService(incomeRegular, Collections.emptyList());
        mockSavingService(incomeOnce, Collections.emptyList());

        BigDecimal budget = this.budgetService.getBudget(USERNAME, START_DATE, END_DATE);

        assertThat(budget, is(BigDecimal.valueOf(201))); // (10 / 0.05) + 1
    }

    @Test
    public void getBudgetIncomesAndSavingsInTheSameCurrency() {
        Income incomeRegular = getMonthlyIncome("REGULAR_ID", BigDecimal.valueOf(12), DEFAULT_USER_CURRENCY, START_DATE, END_DATE, USERNAME);
        Income incomeOnce = getMonthlyIncome("ONCE_ID", BigDecimal.valueOf(7), DEFAULT_USER_CURRENCY, START_DATE, END_DATE, USERNAME);
        Saving savingForRegularIncome = getAnyTypeTitleSaving(incomeRegular, DEFAULT_USER_CURRENCY, BigDecimal.valueOf(5), USERNAME);
        Saving savingForOnceIncome = getAnyTypeTitleSaving(incomeOnce, DEFAULT_USER_CURRENCY, BigDecimal.valueOf(3), USERNAME);

        mockIncomeService(Arrays.asList(incomeRegular, incomeOnce));
        mockRecurringExpensesService(Collections.emptyList());
        mockExpenseService(Collections.emptyList());
        mockSavingService(incomeRegular, Collections.singletonList(savingForRegularIncome));
        mockSavingService(incomeOnce, Collections.singletonList(savingForOnceIncome));

        BigDecimal budget = this.budgetService.getBudget(USERNAME, START_DATE, END_DATE);

        assertThat(budget, is(BigDecimal.valueOf(11))); // (12 - 5) + (7 - 3)
    }

    @Test
    public void getBudgetIncomesInDefaultAndSavingsInDifferentCurrency() {
        Income incomeRegular = getMonthlyIncome("REGULAR_ID", BigDecimal.valueOf(12), DEFAULT_USER_CURRENCY, START_DATE, END_DATE, USERNAME);
        Income incomeOnce = getMonthlyIncome("ONCE_ID", BigDecimal.valueOf(7), DEFAULT_USER_CURRENCY, START_DATE, END_DATE, USERNAME);
        Saving savingForRegularIncome = getAnyTypeTitleSaving(incomeRegular, DIFFERENT_CURRENCY, BigDecimal.valueOf(5), USERNAME);
        Saving savingForOnceIncome = getAnyTypeTitleSaving(incomeOnce, DIFFERENT_CURRENCY, BigDecimal.valueOf(3), USERNAME);

        mockIncomeService(Arrays.asList(incomeRegular, incomeOnce));
        mockRecurringExpensesService(Collections.emptyList());
        mockExpenseService(Collections.emptyList());
        mockSavingService(incomeRegular, Collections.singletonList(savingForRegularIncome));
        mockSavingService(incomeOnce, Collections.singletonList(savingForOnceIncome));

        BigDecimal budget = this.budgetService.getBudget(USERNAME, START_DATE, END_DATE);

        assertThat(budget, is(BigDecimal.valueOf(141).negate())); // (12 - 5 / 0.05) + (7 - 3 / 0.05)
    }

    @Test
    public void getBudgetIncomesInDifferentAndSavingsInDefaultCurrency() {
        Income incomeRegular = getMonthlyIncome("REGULAR_ID", BigDecimal.valueOf(12), DIFFERENT_CURRENCY, START_DATE, END_DATE, USERNAME);
        Income incomeOnce = getMonthlyIncome("ONCE_ID", BigDecimal.valueOf(7), DIFFERENT_CURRENCY, START_DATE, END_DATE, USERNAME);
        Saving savingForRegularIncome = getAnyTypeTitleSaving(incomeRegular, DEFAULT_USER_CURRENCY, BigDecimal.valueOf(5), USERNAME);
        Saving savingForOnceIncome = getAnyTypeTitleSaving(incomeOnce, DEFAULT_USER_CURRENCY, BigDecimal.valueOf(3), USERNAME);

        mockIncomeService(Arrays.asList(incomeRegular, incomeOnce));
        mockRecurringExpensesService(Collections.emptyList());
        mockExpenseService(Collections.emptyList());
        mockSavingService(incomeRegular, Collections.singletonList(savingForRegularIncome));
        mockSavingService(incomeOnce, Collections.singletonList(savingForOnceIncome));

        BigDecimal budget = this.budgetService.getBudget(USERNAME, START_DATE, END_DATE);

        assertThat(budget, is(BigDecimal.valueOf(372))); // (12 / 0.05 - 5) + (7 / 0.05 - 3)
    }

    @Test
    public void getBudgetIncomesSavingsExpensesInDefaultCurrency() {
        Income incomeRegular = getMonthlyIncome("REGULAR_ID", BigDecimal.valueOf(20), DEFAULT_USER_CURRENCY, START_DATE, END_DATE, USERNAME);
        Saving savingForRegularIncome = getAnyTypeTitleSaving(incomeRegular, DEFAULT_USER_CURRENCY, BigDecimal.valueOf(5), USERNAME);
        Expense expenseSmall = getAnyTitleCategoryAndLocationExpense(addDays(START_DATE, 1), BigDecimal.valueOf(2), DEFAULT_USER_CURRENCY, USERNAME);
        Expense expenseLarge = getAnyTitleCategoryAndLocationExpense(addDays(START_DATE, 2), BigDecimal.valueOf(12), DEFAULT_USER_CURRENCY, USERNAME);

        mockIncomeService(Collections.singletonList(incomeRegular));
        mockRecurringExpensesService(Collections.emptyList());
        mockExpenseService(Arrays.asList(expenseSmall, expenseLarge));
        mockSavingService(incomeRegular, Collections.singletonList(savingForRegularIncome));

        BigDecimal budget = this.budgetService.getBudget(USERNAME, START_DATE, END_DATE);

        assertThat(budget, is(BigDecimal.ONE)); // (20 - 5) - (2 + 12)
    }

    @Test
    public void getBudgetIncomesSavingsExpensesInDifferentCurrency() {
        Income incomeRegular = getMonthlyIncome("REGULAR_ID", BigDecimal.valueOf(20), DIFFERENT_CURRENCY, START_DATE, END_DATE, USERNAME);
        Saving savingForRegularIncome = getAnyTypeTitleSaving(incomeRegular, DEFAULT_USER_CURRENCY, BigDecimal.valueOf(5), USERNAME);
        Expense expenseSmall = getAnyTitleCategoryAndLocationExpense(addDays(START_DATE, 1), BigDecimal.valueOf(2), DIFFERENT_CURRENCY, USERNAME);
        Expense expenseLarge = getAnyTitleCategoryAndLocationExpense(addDays(START_DATE, 2), BigDecimal.valueOf(12), DEFAULT_USER_CURRENCY, USERNAME);

        mockIncomeService(Collections.singletonList(incomeRegular));
        mockRecurringExpensesService(Collections.emptyList());
        mockExpenseService(Arrays.asList(expenseSmall, expenseLarge));
        mockSavingService(incomeRegular, Collections.singletonList(savingForRegularIncome));

        BigDecimal budget = this.budgetService.getBudget(USERNAME, START_DATE, END_DATE);

        assertThat(budget, is(BigDecimal.valueOf(343))); // (20 / 0.05 - 5) - (2 / 0.05 + 12)
    }

    @Test
    public void getBudgetIncomesSavingsExpensesRecurringExpensesInDefaultCurrency() {
        Income incomeRegular = getMonthlyIncome("REGULAR_ID", BigDecimal.valueOf(30), DEFAULT_USER_CURRENCY, START_DATE, END_DATE, USERNAME);
        Saving savingForRegularIncome = getAnyTypeTitleSaving(incomeRegular, DEFAULT_USER_CURRENCY, BigDecimal.valueOf(5), USERNAME);
        Expense expenseSmall = getAnyTitleCategoryAndLocationExpense(addDays(START_DATE, 1), BigDecimal.valueOf(2), DEFAULT_USER_CURRENCY, USERNAME);
        Expense expenseLarge = getAnyTitleCategoryAndLocationExpense(addDays(START_DATE, 2), BigDecimal.valueOf(12), DEFAULT_USER_CURRENCY, USERNAME);
        RecurringExpense recurringExpense = getAnyTitlePeriodDescriptionRecurringExpense(DEFAULT_USER_CURRENCY, BigDecimal.valueOf(7), START_DATE, END_DATE, USERNAME);

        mockIncomeService(Collections.singletonList(incomeRegular));
        mockRecurringExpensesService(Collections.singletonList(recurringExpense));
        mockExpenseService(Arrays.asList(expenseSmall, expenseLarge));
        mockSavingService(incomeRegular, Collections.singletonList(savingForRegularIncome));

        BigDecimal budget = this.budgetService.getBudget(USERNAME, START_DATE, END_DATE);

        assertThat(budget, is(BigDecimal.valueOf(4))); // (30 - 5) - 7 - (2 + 12)
    }

    @Test
    public void getBudgetIncomesSavingsExpensesRecurringExpensesInDifferentCurrency() {
        Income incomeRegular = getMonthlyIncome("REGULAR_ID", BigDecimal.valueOf(30), DIFFERENT_CURRENCY, START_DATE, END_DATE, USERNAME);
        Saving savingForRegularIncome = getAnyTypeTitleSaving(incomeRegular, DIFFERENT_CURRENCY, BigDecimal.valueOf(5), USERNAME);
        Expense expenseSmall = getAnyTitleCategoryAndLocationExpense(addDays(START_DATE, 1), BigDecimal.valueOf(2), DIFFERENT_CURRENCY, USERNAME);
        Expense expenseLarge = getAnyTitleCategoryAndLocationExpense(addDays(START_DATE, 2), BigDecimal.valueOf(12), DIFFERENT_CURRENCY, USERNAME);
        RecurringExpense recurringExpense = getAnyTitlePeriodDescriptionRecurringExpense(DIFFERENT_CURRENCY, BigDecimal.valueOf(7), START_DATE, END_DATE, USERNAME);

        mockIncomeService(Collections.singletonList(incomeRegular));
        mockRecurringExpensesService(Collections.singletonList(recurringExpense));
        mockExpenseService(Arrays.asList(expenseSmall, expenseLarge));
        mockSavingService(incomeRegular, Collections.singletonList(savingForRegularIncome));

        BigDecimal budget = this.budgetService.getBudget(USERNAME, START_DATE, END_DATE);

        assertThat(budget, is(BigDecimal.valueOf(80))); // ((30 - 5) - 7 - (2 + 12)) / 0.05
    }

    @Test
    public void getBudgetEmptyRates() {
        mockDailyCoursesRest(Collections.emptyList());

        Income incomeRegular = getMonthlyIncome("REGULAR_ID", BigDecimal.valueOf(30), DIFFERENT_CURRENCY, START_DATE, END_DATE, USERNAME);

        mockIncomeService(Collections.singletonList(incomeRegular));
        mockRecurringExpensesService(Collections.emptyList());
        mockExpenseService(Collections.emptyList());
        mockSavingService(incomeRegular, Collections.emptyList());

        BigDecimal budget = this.budgetService.getBudget(USERNAME, START_DATE, END_DATE);

        assertThat(budget, is(BigDecimal.valueOf(30)));
    }

    @Test
    public void getBudgetEmptyRatios() {
        mockDailyCoursesRest(Collections.singletonList(new Rate(DEFAULT_USER_CURRENCY, Collections.emptyList())));

        Income incomeRegular = getMonthlyIncome("REGULAR_ID", BigDecimal.valueOf(30), DIFFERENT_CURRENCY, START_DATE, END_DATE, USERNAME);

        mockIncomeService(Collections.singletonList(incomeRegular));
        mockRecurringExpensesService(Collections.emptyList());
        mockExpenseService(Collections.emptyList());
        mockSavingService(incomeRegular, Collections.emptyList());

        BigDecimal budget = this.budgetService.getBudget(USERNAME, START_DATE, END_DATE);

        assertThat(budget, is(BigDecimal.valueOf(30)));
    }

    @Test
    public void getBudgetExceptionAuthService() {
        mockAuthorizationError();
        mockDailyCoursesEmpty();

        Income incomeDefault = getMonthlyIncome("REGULAR_ID", BigDecimal.valueOf(30), DEFAULT_USER_CURRENCY, START_DATE, END_DATE, NOT_EXISTING_USERNAME);
        Income incomeDifferent = getMonthlyIncome("REGULAR_ID", BigDecimal.valueOf(70), DIFFERENT_CURRENCY, START_DATE, END_DATE, NOT_EXISTING_USERNAME);

        mockIncomeService(NOT_EXISTING_USERNAME, Arrays.asList(incomeDefault, incomeDifferent));
        mockRecurringExpensesService(NOT_EXISTING_USERNAME, Collections.emptyList());
        mockExpenseService(NOT_EXISTING_USERNAME, Collections.emptyList());
        mockSavingService(NOT_EXISTING_USERNAME, incomeDefault, Collections.emptyList());
        mockSavingService(NOT_EXISTING_USERNAME, incomeDifferent, Collections.emptyList());

        BigDecimal budget = this.budgetService.getBudget(NOT_EXISTING_USERNAME, START_DATE, END_DATE);

        assertThat(budget, is(BigDecimal.valueOf(100))); // despite of income currency, no conversion is done, since no rates are received
    }

    @Test
    public void getBudgetIncomeOverlappingRequestedPeriod() {
        Income incomeRegular = getMonthlyIncome("REGULAR_ID", BigDecimal.valueOf(30), DEFAULT_USER_CURRENCY, addDays(START_DATE, -30), addDays(END_DATE, 30), USERNAME);

        mockIncomeService(Collections.singletonList(incomeRegular));
        mockRecurringExpensesService(Collections.emptyList());
        mockExpenseService(Collections.emptyList());
        mockSavingService(incomeRegular, Collections.emptyList());

        BigDecimal budget = this.budgetService.getBudget(USERNAME, START_DATE, END_DATE);

        assertThat(budget, is(BigDecimal.valueOf(30))); // ((30 - 5) - 7 - (2 + 12)) / 0.05
    }

    private void mockDefaultCurrencyRest() {
        Mockito
                .when(this.restTemplate.getForObject("http://localhost/auth/spenders/username/" + USERNAME + "/default-currency-title", String.class))
                .thenReturn(DEFAULT_USER_CURRENCY);
    }

    private void mockAuthorizationError() {
        Mockito
                .when(this.restTemplate.getForObject("http://localhost/auth/spenders/username/" + NOT_EXISTING_USERNAME + "/default-currency-title", String.class))
                .thenReturn(ERROR_AUTHORIZATION_SERVICE_RESPONSE);
    }

    private void mockDailyCoursesRest() {
        mockDailyCoursesRest(
                Collections.singletonList(
                        new Rate(DEFAULT_USER_CURRENCY,
                                 Arrays.asList(
                                         new RateRatio(NOT_USED_CURRENCY, BigDecimal.valueOf(0.02)),
                                         new RateRatio(DIFFERENT_CURRENCY, BigDecimal.valueOf(0.05))
                                 )
                        )
                )
        );
    }

    private void mockDailyCoursesRest(List<Rate> rates) {
        Mockito
                .when(restTemplate.exchange("http://localhost/rates/daily-courses?exactDate=" + DATE_FORMAT.format(START_DATE) + "&currency=" + DEFAULT_USER_CURRENCY,
                                            HttpMethod.GET,
                                            null,
                                            new ParameterizedTypeReference<List<DailyCourse>>() {}))
                .thenReturn(
                        new ResponseEntity<>(
                                Collections.singletonList(new DailyCourse(new Date(), rates)),
                                HttpStatus.OK
                        )
                );
    }

    private void mockDailyCoursesEmpty() {
        Mockito
                .when(restTemplate.exchange("http://localhost/rates/daily-courses?exactDate=" + DATE_FORMAT.format(START_DATE) + "&currency=" + ERROR_AUTHORIZATION_SERVICE_RESPONSE,
                                            HttpMethod.GET,
                                            null,
                                            new ParameterizedTypeReference<List<DailyCourse>>() {}))
                .thenReturn(
                        new ResponseEntity<>(
                                Collections.emptyList(),
                                HttpStatus.OK
                        )
                );
    }

    private void mockIncomeService(List<Income> incomes) {
        mockIncomeService(USERNAME, incomes);
    }

    private void mockRecurringExpensesService(List<RecurringExpense> recurringExpenses) {
        mockRecurringExpensesService(USERNAME, recurringExpenses);
    }

    private void mockExpenseService(List<Expense> expenses) {
        mockExpenseService(USERNAME, expenses);
    }

    private void mockSavingService(Income income, List<Saving> savings) {
        mockSavingService(USERNAME, income, savings);
    }

    private void mockIncomeService(String username, List<Income> incomes) {
        Mockito
                .when(this.incomeService.findByOverlappingDateRangeAndSpenderUsername(START_DATE, END_DATE, username))
                .thenReturn(incomes);
    }

    private void mockRecurringExpensesService(String username, List<RecurringExpense> recurringExpenses) {
        Mockito
                .when(this.recurringExpensesService.findByOverlappingDateRangeAndSpenderUsername(START_DATE, END_DATE, username))
                .thenReturn(recurringExpenses);
    }

    private void mockExpenseService(String username, List<Expense> expenses) {
        Mockito
                .when(this.expenseService.findBySpenderUsernameAndDate(username, START_DATE, END_DATE))
                .thenReturn(expenses);
    }

    private void mockSavingService(String username, Income income, List<Saving> savings) {
        Mockito
                .when(this.savingService.findByIncomeId(income.getId(), username))
                .thenReturn(savings);
    }
}