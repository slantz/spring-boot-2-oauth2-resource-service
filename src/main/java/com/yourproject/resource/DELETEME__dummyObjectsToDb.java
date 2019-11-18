package com.yourproject.resource;

import com.mongodb.MongoBulkWriteException;
import com.yourproject.resource.currency.CurrencyServiceImpl;
import com.yourproject.resource.model.mongo.Currency;
import com.yourproject.resource.sample.SampleServiceImpl;

import java.util.List;

final class DELETEME__dummyObjectsToDb {
    static void createObjects(CurrencyServiceImpl currencyService, SampleServiceImpl sampleService) {
        Currency USD = new Currency("USD", "$");
        Currency EUR = new Currency("EUR", "€");

        List<Currency> currencies;

        try {
            currencies = currencyService.create(List.of(USD, EUR));
        }
        catch (MongoBulkWriteException e) {
            currencies = currencyService.get();
        }


    }
}
