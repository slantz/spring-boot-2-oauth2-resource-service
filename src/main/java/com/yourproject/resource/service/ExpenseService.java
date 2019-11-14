package com.yourproject.resource.service;

import com.yourproject.resource.model.adjusted.total.TotalCategorizedExpense;
import com.yourproject.resource.model.adjusted.total.TotalExpense;

import java.util.Date;
import java.util.List;

public interface ExpenseService {

    List<Expense> findAll();

    Expense findById(String id);

    List<Expense> findByIds(List<String> expenseIds);

    List<Expense> findBySpenderUsername(String spenderUsername);

    List<Expense> create(List<Expense> newExpenses);

    List<Expense> create(List<Expense> newExpenses, String spenderUsername);

    List<Expense> update(List<Expense> modifiableExpenses);

    List<Expense> update(List<Expense> modifiableExpenses, String spenderUsername);

    boolean deleteById(String id);

    boolean deleteAll();

    List<Expense> findBySpenderUsernameAndDate(String spenderUsername, Date startDate, Date endDate);

    List<Expense> findBySpenderUsernameAndDateAndCurrencyId(String spenderUsername, Date startDate, Date endDate, String currencyId);

    List<Expense> findBySpenderUsernameAndDateAndCategoryId(String spenderUsername, Date startDate, Date endDate, String categoryId);

    List<Expense> findBySpenderUsernameAndDateAndCurrencyTitle(String spenderUsername, Date startDate, Date endDate, String currencyTitle);

    List<Expense> findBySpenderUsernameAndDateAndCategoryType(String spenderUsername, Date startDate, Date endDate, String categoryType);

    List<Expense> findBySpenderUsernameAndCategoryType(String spenderUsername, String categoryType);

    TotalExpense totalBySpenderUsername(String spenderUsername);

    TotalExpense totalBySpenderUsernameAndDates(String spenderUsername, Date startDate, Date endDate);

    TotalCategorizedExpense totalCategorizedBySpenderUsername(String spenderUsername);

    TotalCategorizedExpense totalCategorizedBySpenderUsernameAndDates(String spenderUsername, Date startDate, Date endDate);
}
