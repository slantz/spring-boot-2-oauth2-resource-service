package com.yourproject.resource.model.adjusted.total;

import org.junit.Before;
import org.junit.Test;

import java.math.BigDecimal;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.*;

public class TotalCategorizedExpenseTest {

    private TotalCategorizedExpense totalCategorizedExpense;

    @Before
    public void setUp() throws Exception {
        this.totalCategorizedExpense = new TotalCategorizedExpense();
    }

    @Test
    public void addCategory() {
        TotalExpense totalExpense = new TotalExpense();
        totalExpense.addExpense("UAH", BigDecimal.TEN);

        this.totalCategorizedExpense.addCategory("food", totalExpense);

        assertThat(this.totalCategorizedExpense.getTotalCategorizedExpenses().keySet().size(), is(1));
        assertThat(this.totalCategorizedExpense.getTotalCategorizedExpenses().get("food").getTotalExpenses().get("UAH"), is(BigDecimal.valueOf(10)));
    }

    @Test
    public void addCategoryExisting() {
        TotalExpense totalExpense = new TotalExpense();
        totalExpense.addExpense("UAH", BigDecimal.TEN);

        TotalExpense otherTotalExpense = new TotalExpense();
        otherTotalExpense.addExpense("UAH", BigDecimal.TEN);

        this.totalCategorizedExpense.addCategory("food", totalExpense);
        this.totalCategorizedExpense.addCategory("food", otherTotalExpense);

        assertThat(this.totalCategorizedExpense.getTotalCategorizedExpenses().keySet().size(), is(1));
        assertThat(this.totalCategorizedExpense.getTotalCategorizedExpenses().get("food").getTotalExpenses().get("UAH"), is(BigDecimal.valueOf(20)));
    }

    @Test
    public void addCategoryNew() {
        TotalExpense totalExpense = new TotalExpense();
        totalExpense.addExpense("UAH", BigDecimal.TEN);

        TotalExpense otherTotalExpense = new TotalExpense();
        otherTotalExpense.addExpense("USD", BigDecimal.TEN);

        this.totalCategorizedExpense.addCategory("food", totalExpense);
        this.totalCategorizedExpense.addCategory("present", otherTotalExpense);

        assertThat(this.totalCategorizedExpense.getTotalCategorizedExpenses().keySet().size(), is(2));
        assertThat(this.totalCategorizedExpense.getTotalCategorizedExpenses().get("food").getTotalExpenses().get("UAH"), is(BigDecimal.valueOf(10)));
        assertThat(this.totalCategorizedExpense.getTotalCategorizedExpenses().get("present").getTotalExpenses().get("USD"), is(BigDecimal.valueOf(10)));
    }

    @Test
    public void extendWithTotalCategorizedExpense() {
        TotalCategorizedExpense totalCategorizedExpenseExtender = new TotalCategorizedExpense();

        TotalExpense totalExpense = new TotalExpense();
        totalExpense.addExpense("USD", BigDecimal.TEN);

        totalCategorizedExpenseExtender.addCategory("food", totalExpense);

        this.totalCategorizedExpense.extendWithTotalCategorizedExpense(totalCategorizedExpenseExtender);

        assertThat(this.totalCategorizedExpense.getTotalCategorizedExpenses().keySet().size(), is(1));
        assertThat(this.totalCategorizedExpense.getTotalCategorizedExpenses().get("food").getTotalExpenses().get("USD"), is(BigDecimal.valueOf(10)));
    }

    @Test
    public void extendWithTotalCategorizedExpenseExisting() {
        TotalExpense totalExpense = new TotalExpense();
        totalExpense.addExpense("UAH", BigDecimal.TEN);

        this.totalCategorizedExpense.addCategory("food", totalExpense);

        TotalCategorizedExpense totalCategorizedExpenseExtender = new TotalCategorizedExpense();

        TotalExpense otherTotalExpense = new TotalExpense();
        otherTotalExpense.addExpense("UAH", BigDecimal.TEN);
        otherTotalExpense.addExpense("USD", BigDecimal.TEN);

        totalCategorizedExpenseExtender.addCategory("food", otherTotalExpense);

        this.totalCategorizedExpense.extendWithTotalCategorizedExpense(totalCategorizedExpenseExtender);

        assertThat(this.totalCategorizedExpense.getTotalCategorizedExpenses().keySet().size(), is(1));
        assertThat(this.totalCategorizedExpense.getTotalCategorizedExpenses().get("food").getTotalExpenses().get("UAH"), is(BigDecimal.valueOf(20)));
        assertThat(this.totalCategorizedExpense.getTotalCategorizedExpenses().get("food").getTotalExpenses().get("USD"), is(BigDecimal.valueOf(10)));
    }

    @Test
    public void extendWithTotalCategorizedExpenseExistingDifferentCategory() {
        TotalExpense totalExpense = new TotalExpense();
        totalExpense.addExpense("UAH", BigDecimal.TEN);

        this.totalCategorizedExpense.addCategory("food", totalExpense);

        TotalCategorizedExpense totalCategorizedExpenseExtender = new TotalCategorizedExpense();

        TotalExpense otherTotalExpense = new TotalExpense();
        otherTotalExpense.addExpense("USD", BigDecimal.TEN);

        totalCategorizedExpenseExtender.addCategory("stuff", otherTotalExpense);

        this.totalCategorizedExpense.extendWithTotalCategorizedExpense(totalCategorizedExpenseExtender);

        assertThat(this.totalCategorizedExpense.getTotalCategorizedExpenses().keySet().size(), is(2));
        assertThat(this.totalCategorizedExpense.getTotalCategorizedExpenses().get("food").getTotalExpenses().get("UAH"), is(BigDecimal.valueOf(10)));
        assertThat(this.totalCategorizedExpense.getTotalCategorizedExpenses().get("stuff").getTotalExpenses().get("USD"), is(BigDecimal.valueOf(10)));
    }
}