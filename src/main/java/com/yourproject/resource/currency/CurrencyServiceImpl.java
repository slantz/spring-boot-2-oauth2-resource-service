package com.yourproject.resource.currency;

import com.yourproject.resource.model.mongo.Currency;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;

import static com.yourproject.resource.error.constant.ErrorMessages.CurrencyMessages.NO_ELEMENT_BY_FIELD_EXCEPTION_MESSAGE;

@Service("currencyService")
public class CurrencyServiceImpl implements CurrencyService {

    @Autowired
    private CurrencyRepository currencyRepository;

    @Override
    public Currency getByCode(String code) {
        return this.currencyRepository.findByCode(code).orElseThrow(() -> new NoSuchElementException(String.format(NO_ELEMENT_BY_FIELD_EXCEPTION_MESSAGE, "code")));
    }
}
