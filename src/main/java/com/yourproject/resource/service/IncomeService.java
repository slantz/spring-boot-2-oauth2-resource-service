package com.yourproject.resource.service;

import java.util.Date;
import java.util.List;

public interface IncomeService {

    List<Income> findAll();

    Income findById(String id);

    Income findByDate(Date date);

    List<Income> findByDate(Date date, String spenderUsername);

    Income findByExpiredDate(Date expiredDate);

    Income findByExpiredDate(Date expiredDate, String spenderUsername);

    List<Income> findByTitle(String title);

    List<Income> findByTitle(String title, String spenderUsername);

    List<Income> findByPeriodId(String periodId);

    List<Income> findByPeriodId(String periodId, String spenderUsername);

    List<Income> findByCurrencyId(String currencyId);

    List<Income> findByCurrencyId(String currencyId, String spenderUsername);

    List<Income> findBySpenderUsername(String spenderUsername);

    List<Income> create(List<Income> newIncomes);

    List<Income> create(List<Income> newIncomes, String spenderUsername);

    List<Income> update(List<Income> modifiableIncomes);

    List<Income> update(List<Income> modifiableIncomes, String spenderUsername);

    List<Income> findByInclusiveDateRangeAndSpenderUsername(Date startDate, Date expiredDate, String spenderUsername);

    List<Income> findByOverlappingDateRangeAndSpenderUsername(Date startDate, Date expiredDate, String spenderUsername);

    boolean deleteById(String id);

    boolean deleteAll();
}
