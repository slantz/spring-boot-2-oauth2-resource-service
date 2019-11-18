package com.yourproject.resource.currency;

import com.yourproject.resource.model.mongo.Currency;

import java.util.List;

public interface CurrencyService {

    Currency getByCode(String code);

    List<Currency> get();

    List<Currency> create(List<Currency> currencies);
}
