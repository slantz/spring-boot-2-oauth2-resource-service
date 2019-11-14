package com.yourproject.resource.service;

import java.math.BigDecimal;
import java.util.Date;

/**
 * Algorithm 1st version:
 * ----------------------
 * 1. get user default currency
 * 2. get recurring expenses, incomes, expenses and savings for the requested month
 * 3. convert all the costs to the default currency
 * 4. calculate the budget based on all the data
 * <p>
 * Algorithm 2nd version:
 * ----------------------
 * 1. get the budget mongo collection for the requested date
 * 2. if it's absent do the 1st algorithm.
 * 3. store budget to the db
 * <p>
 * <p>
 * How to calculate currency rate:
 * ------------------------------
 * - iterate through every income and get the startDate of the period for rate
 * - for expenses apply the currency rate of the requested startDate
 * - for recurring expenses iterate through every and get the startDate of the period for Rate
 * - for savings get the necessary saving for each income
 */
public interface BudgetService {

    /**
     * Method to retrieve budget amount for the requested period range.
     *
     * @param username username to get default currency, incomes, expenses, savings and recurring expenses.
     * @param startDate start date of the requested date range.
     * @param endDate end date of the requested date range.
     *
     * @return budget amount, just 1 number.
     */
    BigDecimal getBudget(String username, Date startDate, Date endDate);
}
