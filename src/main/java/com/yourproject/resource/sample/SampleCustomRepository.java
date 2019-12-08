package com.yourproject.resource.sample;

import com.yourproject.resource.currency.Currency;

import java.util.Date;
import java.util.List;

/**
 * {@link Sample} custom repository introducing methods that should be implemented manually in code without auto-generation.
 */
public interface SampleCustomRepository {

    /**
     * Find {@link Sample} for particular user and filter by overlapping date range and currency code.
     *
     * @param username username.
     * @param startDate start date interval.
     * @param endDate end date interval.
     * @param currencyCode {@link Currency} code.
     *
     * @return list of {@link Sample} satisfying filter criteria.
     */
    List<Sample> findByUsernameAndDateAndCurrencyCode(String username, Date startDate, Date endDate, String currencyCode);
}
