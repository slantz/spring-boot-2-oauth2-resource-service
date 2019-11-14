package com.yourproject.resource.service;

import com.yourproject.resource.error.MissingDbModelInstanceException;
import com.yourproject.resource.error.MissingDbRefException;
import com.yourproject.resource.error.MissingIdException;
import com.yourproject.resource.error.constant.ErrorMessages;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

import static com.yourproject.resource.error.constant.ErrorMessages.CurrencyMessages.NO_ELEMENT_BY_ID_EXCEPTION_MESSAGE;
import static com.yourproject.resource.error.constant.ErrorMessages.CurrencyMessages
        .NO_ELEMENT_BY_SPENDER_USERNAME_EXCEPTION_MESSAGE;

@Service("incomeService")
public class IncomeServiceImpl implements IncomeService {

    @Autowired
    private IncomeRepository incomeRepository;

    @Autowired
    private PeriodService periodService;

    @Autowired
    private CurrencyService currencyService;

    @Override
    public List<Income> findAll() {
        return this.incomeRepository.findAll();
    }

    @Override
    public Income findById(String id) {
        return this.incomeRepository.findById(id).orElseThrow(() -> new NoSuchElementException(ErrorMessages.CurrencyMessages.NO_ELEMENT_BY_ID_EXCEPTION_MESSAGE));
    }

    @Override
    public Income findByDate(Date date) {
        return this.incomeRepository.findByDate(date).orElseThrow(() -> new NoSuchElementException(
                NO_ELEMENT_BY_ID_EXCEPTION_MESSAGE));
    }

    @Override
    public List<Income> findByDate(Date date, String spenderUsername) {
        return this.incomeRepository.findByDateAndSpenderUsername(date, spenderUsername).orElseThrow(() -> new NoSuchElementException(
                NO_ELEMENT_BY_ID_EXCEPTION_MESSAGE));
    }

    @Override
    public Income findByExpiredDate(Date expiredDate) {
        return this.incomeRepository.findByExpiredDate(expiredDate).orElseThrow(() -> new NoSuchElementException(
                NO_ELEMENT_BY_ID_EXCEPTION_MESSAGE));
    }

    @Override
    public Income findByExpiredDate(Date expiredDate, String spenderUsername) {
        return this.incomeRepository.findByExpiredDateAndSpenderUsername(expiredDate, spenderUsername).orElseThrow(() -> new
                NoSuchElementException(
                NO_ELEMENT_BY_ID_EXCEPTION_MESSAGE));
    }

    @Override
    public List<Income> findByTitle(String title) {
        return this.incomeRepository.findByTitle(title).orElseThrow(() -> new NoSuchElementException(
                NO_ELEMENT_BY_ID_EXCEPTION_MESSAGE));
    }

    @Override
    public List<Income> findByTitle(String title, String spenderUsername) {
        return this.incomeRepository.findByTitleAndSpenderUsername(title, spenderUsername).orElseThrow(() -> new
                NoSuchElementException(
                NO_ELEMENT_BY_ID_EXCEPTION_MESSAGE));
    }

    @Override
    public List<Income> findByPeriodId(String periodId) {
        return this.incomeRepository.findByPeriodId(periodId).orElseThrow(() -> new NoSuchElementException("no elements by period id"));
    }

    @Override
    public List<Income> findByPeriodId(String periodId, String spenderUsername) {
        return this.incomeRepository.findByPeriodIdAndSpenderUsername(periodId, spenderUsername).orElseThrow(() -> new NoSuchElementException("no elements by period id"));
    }

    @Override
    public List<Income> findByCurrencyId(String currencyId) {
        return this.incomeRepository.findByCurrencyId(currencyId).orElseThrow(() -> new NoSuchElementException("no elements by currency id"));
    }

    @Override
    public List<Income> findByCurrencyId(String currencyId, String spenderUsername) {
        return this.incomeRepository.findByCurrencyIdAndSpenderUsername(currencyId, spenderUsername).orElseThrow(() -> new NoSuchElementException("no elements by currency id and spender username"));
    }

    @Override
    public List<Income> findBySpenderUsername(String spenderUsername) {
        return this.incomeRepository.findBySpenderUsername(spenderUsername).orElseThrow(() -> new NoSuchElementException(
                NO_ELEMENT_BY_SPENDER_USERNAME_EXCEPTION_MESSAGE));
    }

    @Override
    public List<Income> create(List<Income> newIncomes) {
        validateIncomes(newIncomes);
        return this.incomeRepository.saveAll(calculateExpiredDataBasedOnPeriodDuration(newIncomes));
    }

    @Override
    public List<Income> create(List<Income> newIncomes, String spenderUsername) {
        validateIncomes(newIncomes);
        updateIncomesWithSpenderUsername(newIncomes, spenderUsername);
        return this.incomeRepository.saveAll(calculateExpiredDataBasedOnPeriodDuration(newIncomes));
    }

    @Override
    public List<Income> update(List<Income> modifiableIncomes) {
        if (modifiableIncomes.stream().anyMatch(income -> income.getId() == null)) {
            throw new MissingIdException("Some ids are missing for bulk currency update.");
        }

        validateIncomes(modifiableIncomes);

        return this.incomeRepository.saveAll(modifiableIncomes);
    }

    @Override
    public List<Income> update(List<Income> modifiableIncomes, String spenderUsername) {
        if (modifiableIncomes.stream().anyMatch(income -> income.getId() == null)) {
            throw new MissingIdException("Some ids are missing for bulk currency update.");
        }

        validateIncomes(modifiableIncomes);
        updateIncomesWithSpenderUsername(modifiableIncomes, spenderUsername);

        return this.incomeRepository.saveAll(modifiableIncomes);
    }

    @Override
    public List<Income> findByInclusiveDateRangeAndSpenderUsername(Date startDate, Date expiredDate, String spenderUsername) {
        return this.incomeRepository.findByInclusiveDateRangeAndSpenderUsername(startDate, expiredDate, spenderUsername)
                .orElseThrow(() -> new NoSuchElementException(
                NO_ELEMENT_BY_ID_EXCEPTION_MESSAGE));
    }

    @Override
    public List<Income> findByOverlappingDateRangeAndSpenderUsername(Date startDate, Date expiredDate, String spenderUsername) {
        return this.incomeRepository.findByOverlappingDateRangeAndSpenderUsername(startDate, expiredDate, spenderUsername)
                .orElseThrow(() -> new NoSuchElementException(
                NO_ELEMENT_BY_ID_EXCEPTION_MESSAGE));
    }

    @Override
    public boolean deleteById(String id) {
        this.incomeRepository.deleteById(id);
        return true;
    }

    @Override
    public boolean deleteAll() {
        this.incomeRepository.deleteAll();
        return true;
    }

    protected List<Income> calculateExpiredDataBasedOnPeriodDuration(List<Income> incomes) {
        return incomes.stream().peek(income -> {
            Period period = income.getPeriod();
            if (income.getDate() == null) {
                income.setDate(new Date());
            }
            if (!period.isRepeatable()) {
                ZoneId zone = ZoneId.systemDefault();
                LocalDateTime localDateTime = income.getDate().toInstant().atZone(zone).toLocalDateTime();
                Instant instant = localDateTime.plusHours(period.getDuration()).atZone(zone).toInstant();
                Date expiredDate = Date.from(instant);
                income.setExpiredDate(expiredDate);
            }
        }).collect(Collectors.toList());
    }

    private void updateIncomesWithSpenderUsername(List<Income> newIncomes, String spenderUsername) {
        newIncomes.forEach(newIncome -> newIncome.setSpenderUsername(spenderUsername));
    }

    private void validateIncomes(List<Income> newIncomes) {
        newIncomes.forEach(newIncome -> {

            Period period = newIncome.getPeriod();
            Currency currency = newIncome.getCurrency();

            if (period == null || currency == null) {
                throw new MissingDbRefException("Some period or currency not passed");
            }

            Period existingPeriod = getExistingPeriod(period);
            Currency existingCurrency = getExistingCurrency(currency);

            newIncome.setPeriod(existingPeriod);
            newIncome.setCurrency(existingCurrency);

        });
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

    private Period getExistingPeriod(Period period) {
        String periodId = period.getId();
        Period existingPeriod;

        try {
            existingPeriod = this.periodService.findById(periodId);
        } catch (NoSuchElementException e) {
            throw new MissingDbModelInstanceException(String.format("Looks like no period with id [%s] exists", periodId));
        }

        return existingPeriod;
    }
}
