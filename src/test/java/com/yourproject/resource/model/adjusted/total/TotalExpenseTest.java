package com.yourproject.resource.model.adjusted.total;

import org.junit.Before;
import org.junit.Test;

import java.math.BigDecimal;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.*;

public class TotalExpenseTest {

    private TotalExpense totalExpense;

    @Before
    public void setUp() throws Exception {
        this.totalExpense = new TotalExpense();
    }

    @Test
    public void addExpense() {
        this.totalExpense.addExpense("UAH", BigDecimal.TEN);

        assertThat(this.totalExpense.getTotalExpenses().keySet().size(), is(1));
        assertThat(this.totalExpense.getTotalExpenses().get("UAH"), is(BigDecimal.valueOf(10)));
    }

    @Test
    public void addExpenseCurrencyExist() {
        this.totalExpense.addExpense("UAH", BigDecimal.TEN);
        this.totalExpense.addExpense("UAH", BigDecimal.ONE);

        assertThat(this.totalExpense.getTotalExpenses().keySet().size(), is(1));
        assertThat(this.totalExpense.getTotalExpenses().get("UAH"), is(BigDecimal.valueOf(11)));
    }

    @Test
    public void addExpenseNewCurrency() {
        this.totalExpense.addExpense("UAH", BigDecimal.TEN);
        this.totalExpense.addExpense("USD", BigDecimal.TEN);

        assertThat(this.totalExpense.getTotalExpenses().keySet().size(), is(2));
        assertThat(this.totalExpense.getTotalExpenses().get("UAH"), is(BigDecimal.valueOf(10)));
        assertThat(this.totalExpense.getTotalExpenses().get("USD"), is(BigDecimal.valueOf(10)));
    }

    @Test
    public void addExpenseNegative() {
        this.totalExpense.addExpense("UAH", BigDecimal.TEN);
        this.totalExpense.addExpense("UAH", BigDecimal.valueOf(20).negate());

        assertThat(this.totalExpense.getTotalExpenses().keySet().size(), is(1));
        assertThat(this.totalExpense.getTotalExpenses().get("UAH"), is(BigDecimal.valueOf(-10)));
    }

    @Test
    public void extendWithTotalExpense() {
        TotalExpense totalExpenseExtender = new TotalExpense();
        totalExpenseExtender.addExpense("UAH", BigDecimal.TEN);

        this.totalExpense.extendWithTotalExpense(totalExpenseExtender);

        assertThat(this.totalExpense.getTotalExpenses().keySet().size(), is(1));
        assertThat(this.totalExpense.getTotalExpenses().get("UAH"), is(BigDecimal.valueOf(10)));
    }

    @Test
    public void extendWithTotalExpenseCurrencyExist() {
        this.totalExpense.addExpense("UAH", BigDecimal.TEN);

        TotalExpense totalExpenseExtender = new TotalExpense();
        totalExpenseExtender.addExpense("UAH", BigDecimal.TEN);

        this.totalExpense.extendWithTotalExpense(totalExpenseExtender);

        assertThat(this.totalExpense.getTotalExpenses().keySet().size(), is(1));
        assertThat(this.totalExpense.getTotalExpenses().get("UAH"), is(BigDecimal.valueOf(20)));
    }

    @Test
    public void extendWithTotalExpenseNewCurrency() {
        this.totalExpense.addExpense("UAH", BigDecimal.TEN);

        TotalExpense totalExpenseExtender = new TotalExpense();
        totalExpenseExtender.addExpense("USD", BigDecimal.TEN);

        this.totalExpense.extendWithTotalExpense(totalExpenseExtender);

        assertThat(this.totalExpense.getTotalExpenses().keySet().size(), is(2));
        assertThat(this.totalExpense.getTotalExpenses().get("UAH"), is(BigDecimal.valueOf(10)));
        assertThat(this.totalExpense.getTotalExpenses().get("USD"), is(BigDecimal.valueOf(10)));
    }

    @Test
    public void extendWithTotalExpenseNewCurrencies() {
        this.totalExpense.addExpense("UAH", BigDecimal.TEN);
        this.totalExpense.addExpense("USD", BigDecimal.TEN);

        TotalExpense totalExpenseExtender = new TotalExpense();
        totalExpenseExtender.addExpense("UAH", BigDecimal.TEN);
        totalExpenseExtender.addExpense("USD", BigDecimal.TEN);
        totalExpenseExtender.addExpense("EUR", BigDecimal.TEN);

        this.totalExpense.extendWithTotalExpense(totalExpenseExtender);

        assertThat(this.totalExpense.getTotalExpenses().keySet().size(), is(3));
        assertThat(this.totalExpense.getTotalExpenses().get("UAH"), is(BigDecimal.valueOf(20)));
        assertThat(this.totalExpense.getTotalExpenses().get("USD"), is(BigDecimal.valueOf(20)));
        assertThat(this.totalExpense.getTotalExpenses().get("EUR"), is(BigDecimal.valueOf(10)));
    }
}