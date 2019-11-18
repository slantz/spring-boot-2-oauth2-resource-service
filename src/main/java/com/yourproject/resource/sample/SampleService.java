package com.yourproject.resource.sample;

import com.yourproject.resource.model.adjust.Authority;
import com.yourproject.resource.model.mongo.Sample;

import java.util.Date;
import java.util.List;

public interface SampleService {

    List<Sample> getSamplesByUsername(String username);

    List<Sample> getSamplesByUsernameAndPreciseDate(String username, Date date);

    List<Sample> getSamplesByInclusiveDateRangeAndUsername(String username, Date startDate, Date expiredDate);

    List<Sample> getSamplesByOverlappingDateRangeAndUsername(String username, Date startDate, Date expiredDate);

    List<Sample> getSamplesByTitle(String username, String title);

    List<Sample> getSamplesByUsernameAndDateAndCurrencyCode(String username, Date startDate, Date expiredDate, String currencyCode);

    List<Authority> getUsernameAuthorities(String username);
}
