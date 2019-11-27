package com.yourproject.resource.currency;

import com.yourproject.resource.model.mongo.Currency;

import java.util.List;

/**
 * {@link Currency} service.
 */
public interface CurrencyService {

    /**
     * Get currency by currency code.
     *
     * @param code currency code.
     * @return {@link Currency}.
     */
    Currency getByCode(String code);

    /**
     * Get all currencies.
     *
     * @return list of {@link Currency}.
     */
    List<Currency> get();

    /**
     * Create new currencies and save to DB.
     *
     * @param currencies list of {@link Currency}.
     * @return list of {@link Currency} with DB ids.
     */
    List<Currency> create(List<Currency> currencies);
}
