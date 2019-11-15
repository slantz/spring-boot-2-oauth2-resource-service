package com.yourproject.resource.currency;

import com.yourproject.resource.model.mongo.Currency;

public interface CurrencyService {

    Currency getByCode(String code);
}
