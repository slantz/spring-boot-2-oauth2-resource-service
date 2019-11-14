package com.yourproject.resource.service;

import com.yourproject.resource.model.adjusted.total.TotalCategorizedExpense;
import com.yourproject.resource.model.adjusted.total.TotalExpense;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.junit4.SpringRunner;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.Map;
import java.util.Optional;

import static com.yourproject.resource.util.TestUtil.ANY_USERNAME;
import static com.yourproject.resource.util.TestUtil.getAnyTitleUserNameLocationExpense;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

@RunWith(SpringRunner.class)
@Ignore
public class ExpenseServiceImplTest {

    private static final DateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");
    private static Date START_DATE;
    private static Date END_DATE;

    @TestConfiguration
    static class ExpenseServiceImplTestContextConfiguration {
        @Bean
        public ExpenseService expenseService() {
            return new ExpenseServiceImpl();
        }
    }

    @Autowired
    private ExpenseServiceImpl expenseService;

    @MockBean
    private ExpenseRepository expenseRepository;

    @MockBean
    private CategoryService categoryService;

    @MockBean
    private CurrencyService currencyService;

    @MockBean
    private LocationService locationService;

    @BeforeClass
    public static void setAll() throws Exception {
        START_DATE = DATE_FORMAT.parse("2019-04-08");
        END_DATE = DATE_FORMAT.parse("2019-04-10");
    }

    @Before
    public void setUp() throws Exception {
        mockExpensesByUsername();
        mockExpensesByUsernameAndDates();
    }

    @Test
    public void totalBySpenderUsername() {
        TotalExpense totalExpense = this.expenseService.totalBySpenderUsername(ANY_USERNAME);

        assertThat(totalExpense.getTotalExpenses(),
                   is(Map.of(
                        "UAH", BigDecimal.valueOf(13),
                        "USD", BigDecimal.valueOf(3),
                        "EUR", BigDecimal.valueOf(14),
                        "GBP", BigDecimal.valueOf(60)
                   )));
    }

    @Test
    public void totalBySpenderUsernameAndDates() {
        TotalExpense totalExpense = this.expenseService.totalBySpenderUsernameAndDates(ANY_USERNAME, START_DATE, END_DATE);

        assertThat(totalExpense.getTotalExpenses(),
                   is(Map.of(
                       "UAH", BigDecimal.valueOf(29), // 2 + 5 + 10 + 12
                       "USD", BigDecimal.valueOf(3),
                       "EUR", BigDecimal.valueOf(14)
                   )));
    }

    @Test
    public void totalCategorizedBySpenderUsername() {
        TotalCategorizedExpense totalCategorizedExpense = this.expenseService.totalCategorizedBySpenderUsername(ANY_USERNAME);

        assertThat(
                totalCategorizedExpense.getTotalCategorizedExpenses().get("FOOD"),
                is(Map.of(
                        "UAH", BigDecimal.valueOf(8) // 2 + 6
                )));
        assertThat(
                totalCategorizedExpense.getTotalCategorizedExpenses().get("GAS"),
                is(Map.of(
                        "UAH", BigDecimal.valueOf(5)
                )));
        assertThat(
                totalCategorizedExpense.getTotalCategorizedExpenses().get("DRIVE"),
                is(Map.of(
                        "USD", BigDecimal.valueOf(3)
                )));
        assertThat(
                totalCategorizedExpense.getTotalCategorizedExpenses().get("PRESENT"),
                is(Map.of(
                        "EUR", BigDecimal.valueOf(14)
                )));
        assertThat(
                totalCategorizedExpense.getTotalCategorizedExpenses().get("VISA"),
                is(Map.of(
                        "GBP", BigDecimal.valueOf(60)
                )));
    }

    @Test
    public void totalCategorizedBySpenderUsernameAndDates() {
        TotalCategorizedExpense totalCategorizedExpense = this.expenseService.totalCategorizedBySpenderUsernameAndDates(ANY_USERNAME, START_DATE, END_DATE);

        assertThat(
                totalCategorizedExpense.getTotalCategorizedExpenses().get("FOOD"),
                is(Map.of(
                        "UAH", BigDecimal.valueOf(2)
                )));
        assertThat(
                totalCategorizedExpense.getTotalCategorizedExpenses().get("GAS"),
                is(Map.of(
                        "UAH", BigDecimal.valueOf(15) // 5 + 10
                )));
        assertThat(
                totalCategorizedExpense.getTotalCategorizedExpenses().get("DRIVE"),
                is(Map.of(
                        "USD", BigDecimal.valueOf(3)
                )));
        assertThat(
                totalCategorizedExpense.getTotalCategorizedExpenses().get("PS"),
                is(Map.of(
                        "UAH", BigDecimal.valueOf(12),
                        "EUR", BigDecimal.valueOf(14)
                )));
    }

    private void mockExpensesByUsernameAndDates() {
        Mockito.when(this.expenseRepository.findBySpenderUsernameAndDate(ANY_USERNAME, START_DATE, END_DATE))
                .thenReturn(
                        Optional.of(
                                Arrays.asList(
                                        getAnyTitleUserNameLocationExpense(START_DATE, BigDecimal.valueOf(2), "UAH", "FOOD"),
                                        getAnyTitleUserNameLocationExpense(START_DATE, BigDecimal.valueOf(5), "UAH", "GAS"),
                                        getAnyTitleUserNameLocationExpense(START_DATE, BigDecimal.valueOf(3), "USD", "DRIVE"),
                                        getAnyTitleUserNameLocationExpense(END_DATE, BigDecimal.valueOf(10), "UAH", "GAS"),
                                        getAnyTitleUserNameLocationExpense(END_DATE, BigDecimal.valueOf(14), "EUR", "PS"),
                                        getAnyTitleUserNameLocationExpense(END_DATE, BigDecimal.valueOf(12), "UAH", "PS")
                                )
                        )
                );
    }

    private void mockExpensesByUsername() {
        Mockito
                .when(this.expenseRepository.findBySpenderUsername(ANY_USERNAME))
                .thenReturn(
                        Optional.of(
                                Arrays.asList(
                                        getAnyTitleUserNameLocationExpense(START_DATE, BigDecimal.valueOf(2), "UAH", "FOOD"),
                                        getAnyTitleUserNameLocationExpense(START_DATE, BigDecimal.valueOf(5), "UAH", "GAS"),
                                        getAnyTitleUserNameLocationExpense(START_DATE, BigDecimal.valueOf(6), "UAH", "FOOD"),
                                        getAnyTitleUserNameLocationExpense(START_DATE, BigDecimal.valueOf(3), "USD", "DRIVE"),
                                        getAnyTitleUserNameLocationExpense(START_DATE, BigDecimal.valueOf(14), "EUR", "PRESENT"),
                                        getAnyTitleUserNameLocationExpense(START_DATE, BigDecimal.valueOf(60), "GBP", "VISA")
                                )
                        )
                );
    }
}