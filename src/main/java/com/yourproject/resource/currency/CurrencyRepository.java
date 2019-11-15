package com.yourproject.resource.currency;

import com.yourproject.resource.model.mongo.Currency;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface CurrencyRepository extends MongoRepository<Currency, String> {

    public Optional<Currency> findByCode(String code);
}
