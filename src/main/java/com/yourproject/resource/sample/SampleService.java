package com.yourproject.resource.sample;

import com.yourproject.resource.currency.Currency;
import com.yourproject.resource.model.adjust.Authority;

import java.util.Date;
import java.util.List;

/**
 * Service introducing CRUD operations over {@link Sample} and additional filtering methods.
 */
public interface SampleService {

    /**
     * Get list of {@link Sample} by username.
     *
     * @param username username.
     *
     * @return list of {@link Sample} for current user.
     */
    List<Sample> getSamplesByUsername(String username);

    /**
     * Get list of {@link Sample} by username and for precise date.
     *
     * @param username username.
     * @param date precise date.
     *
     * @return list of {@link Sample} for current user and precise date.
     */
    List<Sample> getSamplesByUsernameAndPreciseDate(String username, Date date);

    /**
     * Get list of {@link Sample} by inclusive date range and username.
     *
     * @param username username.
     * @param startDate start date of the interval.
     * @param expiredDate end date of the interval.
     *
     * @return list of {@link Sample} for current user and inclusive date interval.
     */
    List<Sample> getSamplesByInclusiveDateRangeAndUsername(String username, Date startDate, Date expiredDate);

    /**
     * Get list of {@link Sample} by overlapping date range and username.
     *
     * @param username username.
     * @param startDate start date of the interval.
     * @param expiredDate end date of the interval.
     *
     * @return list of {@link Sample} for current user and overlapping date interval.
     */
    List<Sample> getSamplesByOverlappingDateRangeAndUsername(String username, Date startDate, Date expiredDate);

    /**
     * Get list of {@link Sample} by username and title.
     *
     * @param username username.
     * @param title {@link Sample} title.
     *
     * @return list of {@link Sample} fur current user and {@link Sample} title.
     */
    List<Sample> getSamplesByTitle(String username, String title);

    /**
     * Get list of {@link Sample} by username, overlapping date interval and currency code.
     *
     * @param username username.
     * @param startDate start date of the interval.
     * @param expiredDate end date of the interval.
     * @param currencyCode {@link Currency} code.
     *
     * @return list of {@link Sample} for current user filtered by overlapping date interval and {@link Currency} code.
     */
    List<Sample> getSamplesByUsernameAndDateAndCurrencyCode(String username, Date startDate, Date expiredDate, String currencyCode);

    /**
     * Get list of {@link Authority} by username.
     *
     * @param username username.
     *
     * @return list of {@link Authority} fir current user.
     */
    List<Authority> getUsernameAuthorities(String username);

    /**
     * Create list of {@link Sample} and store those to DB.
     *
     * @param samples list of sample to create.
     *
     * @return list of {@link Sample} created and stored in DB with ids.
     */
    List<Sample> create(List<Sample> samples);

    /**
     * Create list of {@link Sample} and store those to DB for particular user.
     *
     * @param samples list of sample to create.
     * @param username username.
     *
     * @return list of {@link Sample} created and stored in DB with ids for current user.
     */
    List<Sample> create(List<Sample> samples, String username);

    /**
     * Get all {@link Sample} from DB for all the users.
     *
     * @return list of all {@link Sample} in DB.
     */
    List<Sample> get();
}
