package com.yourproject.resource.sample;

import com.yourproject.resource.model.mongo.Sample;

import java.util.Date;
import java.util.List;

public interface SampleCustomRepository {

    List<Sample> findByUsernameAndDateAndCurrencyCode(String username, Date startDate, Date endDate, String currencyCode);
}
