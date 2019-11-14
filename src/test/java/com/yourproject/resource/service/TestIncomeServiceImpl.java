package com.yourproject.resource.service;

import com.yourproject.resource.util.TestUtil;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.junit4.SpringRunner;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.List;

import static com.yourproject.resource.util.TestUtil.CURRENCY_UAH_ID;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;

@RunWith(SpringRunner.class)
public class TestIncomeServiceImpl {

    private static final String REPEATABLE_PERIOD_ID = "100500";
    private static final String NON_REPEATABLE_PERIOD_ID = "100501";
    private static final SimpleDateFormat FORMAT = new SimpleDateFormat("yyyy-MM-dd");
    private static final Currency CURRENCY = TestUtil.getCurrency("UAH");
    private static final Period REPEATABLE = TestUtil.getMonthlyPeriod(REPEATABLE_PERIOD_ID, 730);
    private static final Period NON_REPEATABLE = TestUtil.getThisPeriod(NON_REPEATABLE_PERIOD_ID, 720);

    @TestConfiguration
    static class TestIncomeServiceImplContextConfiguration {
        @Bean
        public IncomeServiceImpl incomeService() {
            return new IncomeServiceImpl();
        }
    }

    @Autowired
    private IncomeServiceImpl incomeService;

    @MockBean
    private PeriodService periodService;

    @MockBean
    private CurrencyService currencyService;

    @MockBean
    private IncomeRepository incomeRepository;

    @Before
    public void setUp() {
        Mockito
                .when(this.periodService.findById(REPEATABLE_PERIOD_ID))
                .thenReturn(REPEATABLE);
        Mockito
                .when(this.periodService.findById(NON_REPEATABLE_PERIOD_ID))
                .thenReturn(NON_REPEATABLE);
        Mockito
                .when(this.currencyService.findById(CURRENCY_UAH_ID))
                .thenReturn(CURRENCY);
    }

    @Test
    public void createIncomeWithRepeatablePeriod() throws ParseException {
        Income repeatableIncome = new Income("Salary",
                                             BigDecimal.valueOf(100),
                                             REPEATABLE,
                                             CURRENCY,
                                             FORMAT.parse("2019-03-04"),
                                             "alex");
        List<Income> incomesWithExpiredDate =
                this.incomeService.calculateExpiredDataBasedOnPeriodDuration(Collections.singletonList(repeatableIncome));

        assertNull(incomesWithExpiredDate.get(0).getExpiredDate());
    }

    @Test
    public void createIncomeWithNotRepeatablePeriod() throws ParseException {
        Income nonRepeatableIncome = new Income("Freelance",
                                                BigDecimal.valueOf(315),
                                                NON_REPEATABLE,
                                                CURRENCY,
                                                FORMAT.parse("2019-03-04"),
                                                "alex");
        List<Income> incomesWithExpiredDate =
                this.incomeService.calculateExpiredDataBasedOnPeriodDuration(Collections.singletonList(nonRepeatableIncome));

        assertThat(incomesWithExpiredDate.get(0).getExpiredDate(), is(FORMAT.parse("2019-04-03")));
    }
}