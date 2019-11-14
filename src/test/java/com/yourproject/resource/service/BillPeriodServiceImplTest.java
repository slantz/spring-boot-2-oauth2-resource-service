package com.yourproject.resource.service;

import com.yourproject.resource.model.adjusted.Interval;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.junit4.SpringRunner;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import static org.hamcrest.Matchers.emptyCollectionOf;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThat;

@RunWith(SpringRunner.class)
@AutoConfigureMockMvc
public class BillPeriodServiceImplTest {

    private static final SimpleDateFormat SIMPLE_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");

    private final Date START_DATE;
    private final Date END_DATE;
    private final Interval INTERVAL;
    private final Interval NON_EXISTING_INTERVAL;

    @TestConfiguration
    static class BillPeriodServiceImplTestContextConfiguration {
        @Bean
        public BillPeriodService billPeriodService() {
            return new BillPeriodServiceImpl();
        }
    }

    @Autowired
    private BillPeriodService billPeriodService;

    @MockBean
    private BillPeriodRepository billPeriodRepository;

    @MockBean
    private CurrencyService currencyService;

    @MockBean
    private ExpenseService expenseService;

    @Before
    public void setUp() {
        BillPeriod alex =
                new BillPeriod(
                        INTERVAL,
                        BigDecimal.valueOf(100),
                        new Currency("UAH", "₴"),
                        "alex"
                );

        Mockito
                .when(this.billPeriodRepository.findByInterval(INTERVAL, "alex"))
                .thenReturn(Collections.singletonList(alex));

        Mockito
                .when(this.billPeriodRepository.findByInterval(NON_EXISTING_INTERVAL, "alex"))
                .thenReturn(Collections.emptyList());
    }

    public BillPeriodServiceImplTest() throws ParseException {
        START_DATE = SIMPLE_DATE_FORMAT.parse("2018-01-01");
        END_DATE = SIMPLE_DATE_FORMAT.parse("2018-02-01");
        INTERVAL = new Interval(START_DATE, END_DATE);
        NON_EXISTING_INTERVAL = new Interval(SIMPLE_DATE_FORMAT.parse("2018-03-01"), SIMPLE_DATE_FORMAT.parse("2018-03-01"));
    }

    @Test
    public void testGetByInterval() {
        BillPeriod billPeriod = this.billPeriodService.getByInterval(INTERVAL, "alex").get(0);

        assertThat(billPeriod, notNullValue());
        assertThat(billPeriod.getCurrency().getSymbol(), is("₴"));
        assertThat(billPeriod.getInterval().getStart(), is(START_DATE));
        assertThat(billPeriod.getInterval().getEnd(), is(END_DATE));
        assertThat(billPeriod.getTotalExpense(), is(BigDecimal.valueOf(100)));
    }

    @Test
    public void testGetNonExistingBillPeriod() {
        List<BillPeriod> billPeriods = this.billPeriodService.getByInterval(NON_EXISTING_INTERVAL, "ololosha");

        assertThat(billPeriods, emptyCollectionOf(BillPeriod.class));
    }
}