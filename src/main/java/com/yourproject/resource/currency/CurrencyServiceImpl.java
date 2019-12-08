package com.yourproject.resource.currency;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;

import static com.yourproject.resource.error.constant.ErrorMessages.CurrencyMessages.NO_ELEMENT_BY_FIELD_EXCEPTION_MESSAGE;

/**
 * Implementation of {@link CurrencyService}.
 */
@Service("currencyService")
public class CurrencyServiceImpl implements CurrencyService {

    @Autowired
    private CurrencyRepository currencyRepository;

    /**
     * {@inheritDoc}
     */
    @Override
    public Currency getByCode(String code) {
        return this.currencyRepository.findByCode(code).orElseThrow(() -> new NoSuchElementException(String.format(NO_ELEMENT_BY_FIELD_EXCEPTION_MESSAGE, "code")));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Currency> get() {
        return this.currencyRepository.findAll();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Currency> create(List<Currency> currencies) {
        return this.currencyRepository.saveAll(currencies);
    }
}
