package com.yourproject.resource.service;

import com.yourproject.resource.error.MissingDbModelInstanceException;
import com.yourproject.resource.error.MissingDbRefException;
import com.yourproject.resource.error.MissingIdException;
import com.yourproject.resource.error.constant.ErrorMessages;
import com.yourproject.resource.model.adjusted.total.TotalCategorizedExpense;
import com.yourproject.resource.model.adjusted.total.TotalExpense;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static com.yourproject.resource.error.constant.ErrorMessages.CurrencyMessages.NO_ELEMENT_BY_ID_EXCEPTION_MESSAGE;
import static com.yourproject.resource.error.constant.ErrorMessages.CurrencyMessages.NO_ELEMENT_BY_SPENDER_USERNAME_EXCEPTION_MESSAGE;

@Service("expenseService")
public class ExpenseServiceImpl implements ExpenseService {

    @Autowired
    private ExpenseRepository expenseRepository;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private CurrencyService currencyService;

    @Autowired
    private LocationService locationService;

    @Override
    public List<Expense> findAll() {
        return this.expenseRepository.findAll();
    }

    @Override
    public Expense findById(String id) {
        return this.expenseRepository.findById(id).orElseThrow(() -> new NoSuchElementException(ErrorMessages.CurrencyMessages.NO_ELEMENT_BY_ID_EXCEPTION_MESSAGE));
    }

    @Override
    public List<Expense> findByIds(List<String> expenseIds) {
        return this.expenseRepository.findByIdIn(expenseIds).orElseThrow(() -> new NoSuchElementException(
                NO_ELEMENT_BY_SPENDER_USERNAME_EXCEPTION_MESSAGE));
    }

    @Override
    public List<Expense> findBySpenderUsername(String spenderUsername) {
        return this.expenseRepository.findBySpenderUsername(spenderUsername).orElseThrow(() -> new NoSuchElementException(
                NO_ELEMENT_BY_SPENDER_USERNAME_EXCEPTION_MESSAGE));
    }

    @Override
    public List<Expense> create(List<Expense> newExpenses) {
        validateExpenses(newExpenses);
        return this.expenseRepository.saveAll(newExpenses);
    }

    @Override
    public List<Expense> create(List<Expense> newExpenses, String spenderUsername) {
        validateExpenses(newExpenses);
        updateExpensesWithSpenderUsername(newExpenses, spenderUsername);
        return this.expenseRepository.saveAll(newExpenses);
    }

    @Override
    public List<Expense> update(List<Expense> modifiableExpenses) {
        if (modifiableExpenses.stream().anyMatch(expense -> expense.getId() == null)) {
            throw new MissingIdException("Some ids are missing for bulk expense update.");
        }

        validateExpenses(modifiableExpenses);

        return this.expenseRepository.saveAll(modifiableExpenses);
    }

    @Override
    public List<Expense> update(List<Expense> modifiableExpenses, String spenderUsername) {
        if (modifiableExpenses.stream().anyMatch(expense -> expense.getId() == null)) {
            throw new MissingIdException("Some ids are missing for bulk expense update.");
        }

        validateExpenses(modifiableExpenses);
        updateExpensesWithSpenderUsername(modifiableExpenses, spenderUsername);

        return this.expenseRepository.saveAll(modifiableExpenses);
    }

    @Override
    public boolean deleteById(String id) {
        this.expenseRepository.deleteById(id);
        return true;
    }

    @Override
    public boolean deleteAll() {
        this.expenseRepository.deleteAll();
        return true;
    }

    @Override
    public List<Expense> findBySpenderUsernameAndDate(String spenderUsername, Date startDate, Date endDate) {
        return this.expenseRepository.findBySpenderUsernameAndDate(spenderUsername, startDate, endDate).orElseThrow(() -> new
                NoSuchElementException(
                NO_ELEMENT_BY_ID_EXCEPTION_MESSAGE));
    }

    @Override
    public List<Expense> findBySpenderUsernameAndDateAndCurrencyId(String spenderUsername, Date startDate, Date endDate, String currencyId) {
        return this.expenseRepository.findBySpenderUsernameAndDateAndCurrencyId(spenderUsername, startDate, endDate,
                                                                                currencyId).orElseThrow(() -> new NoSuchElementException(
                NO_ELEMENT_BY_ID_EXCEPTION_MESSAGE));
    }

    @Override
    public List<Expense> findBySpenderUsernameAndDateAndCategoryId(String spenderUsername, Date startDate, Date endDate, String categoryId) {
        return this.expenseRepository.findBySpenderUsernameAndDateAndCategoryId(spenderUsername, startDate, endDate, categoryId).orElseThrow(() -> new NoSuchElementException(
                NO_ELEMENT_BY_ID_EXCEPTION_MESSAGE));
    }

    @Override
    public List<Expense> findBySpenderUsernameAndDateAndCurrencyTitle(String spenderUsername, Date startDate, Date endDate, String currencyTitle) {
        return this.expenseRepository.findBySpenderUsernameAndDateAndCurrencyTitle(spenderUsername, startDate, endDate, currencyTitle);
    }

    @Override
    public List<Expense> findBySpenderUsernameAndDateAndCategoryType(String spenderUsername, Date startDate, Date endDate, String categoryType) {
        return this.expenseRepository.findBySpenderUsernameAndDateAndCategoryType(spenderUsername, startDate, endDate, categoryType);
    }

    @Override
    public List<Expense> findBySpenderUsernameAndCategoryType(String spenderUsername, String categoryType) {
        return this.expenseRepository.findBySpenderUsernameAndCategoryType(spenderUsername, categoryType);
    }

    @Override
    public TotalExpense totalBySpenderUsername(String spenderUsername) {
        Optional<List<Expense>> bySpenderUsername = this.expenseRepository.findBySpenderUsername(spenderUsername);
        return calculateTotalExpense(bySpenderUsername);
    }

    @Override
    public TotalExpense totalBySpenderUsernameAndDates(String spenderUsername, Date startDate, Date endDate) {
        Optional<List<Expense>> bySpenderUsername = this.expenseRepository.findBySpenderUsernameAndDate(spenderUsername, startDate, endDate);
        return calculateTotalExpense(bySpenderUsername);
    }

    @Override
    public TotalCategorizedExpense totalCategorizedBySpenderUsername(String spenderUsername) {
        Optional<List<Expense>> bySpenderUsername = this.expenseRepository.findBySpenderUsername(spenderUsername);
        return calculateTotalCategorizedExpense(bySpenderUsername);
    }

    @Override
    public TotalCategorizedExpense totalCategorizedBySpenderUsernameAndDates(String spenderUsername, Date startDate, Date endDate) {
        Optional<List<Expense>> bySpenderUsername = this.expenseRepository.findBySpenderUsernameAndDate(spenderUsername, startDate, endDate);
        return calculateTotalCategorizedExpense(bySpenderUsername);
    }

    private TotalExpense calculateTotalExpense(Optional<List<Expense>> bySpenderUsername) {
        TotalExpense totalExpense = new TotalExpense();

        if (!bySpenderUsername.isPresent()) {
            return totalExpense;
        }

        List<Expense> expenses = bySpenderUsername.get();

        totalExpense = expenses.stream().reduce(totalExpense,
                                                (partialTotalExpense, element) -> partialTotalExpense.addExpense(element.getCurrency().getTitle(), element.getCost()),
                                                TotalExpense::extendWithTotalExpense);

        return totalExpense;
    }

    private TotalCategorizedExpense calculateTotalCategorizedExpense(Optional<List<Expense>> bySpenderUsername) {
        TotalCategorizedExpense totalCategorizedExpense = new TotalCategorizedExpense();

        if (!bySpenderUsername.isPresent()) {
            return totalCategorizedExpense;
        }

        List<Expense> expenses = bySpenderUsername.get();

        totalCategorizedExpense = expenses.stream().reduce(totalCategorizedExpense,
                                                           (partialTotalCategorizedExpense, element) -> {
                                                               TotalExpense totalExpense = new TotalExpense();
                                                               totalExpense.addExpense(element.getCurrency().getTitle(), element.getCost());
                                                               return partialTotalCategorizedExpense.addCategory(element.getCategory().getType(), totalExpense);
                                                           },
                                                           TotalCategorizedExpense::extendWithTotalCategorizedExpense);

        return totalCategorizedExpense;
    }

    private void updateExpensesWithSpenderUsername(List<Expense> newExpenses, String spenderUsername) {
        newExpenses.forEach(expense -> expense.setSpenderUsername(spenderUsername));
    }

    private void validateExpenses(List<Expense> newExpenses) {
        newExpenses.forEach(expense -> {

            Currency currency = expense.getCurrency();
            Category category = expense.getCategory();
            Location location = expense.getLocation();

            if (category == null || currency == null || location == null) {
                throw new MissingDbRefException("Some category or currency or location not passed");
            }

            Category existingCategory = getExistingCategory(category);
            Currency existingCurrency = getExistingCurrency(currency);
            Location existingLocation = getExistingLocation(location);

            expense.setCategory(existingCategory);
            expense.setCurrency(existingCurrency);
            expense.setLocation(existingLocation);

        });
    }

    private Location getExistingLocation(Location location) {
        String locationId = location.getId();
        Location existingLocation;

        try {
            existingLocation = this.locationService.findById(locationId);
        } catch (NoSuchElementException e) {
            throw new MissingDbModelInstanceException(String.format("Looks like no location with id [%s] exists", locationId));
        }

        return existingLocation;
    }

    private Currency getExistingCurrency(Currency currency) {
        String currencyId = currency.getId();
        Currency existingCurrency;

        try {
            existingCurrency = this.currencyService.findById(currencyId);
        } catch (NoSuchElementException e) {
            throw new MissingDbModelInstanceException(String.format("Looks like no currency with id [%s] exists", currencyId));
        }

        return existingCurrency;
    }

    private Category getExistingCategory(Category category) {
        String categoryId = category.getId();
        Category existingCategory;

        try {
            existingCategory = this.categoryService.findById(categoryId);
        } catch (NoSuchElementException e) {
            throw new MissingDbModelInstanceException(String.format("Looks like no category with id [%s] exists", categoryId));
        }

        return existingCategory;
    }

}
