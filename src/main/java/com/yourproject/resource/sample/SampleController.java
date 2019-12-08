package com.yourproject.resource.sample;

import com.yourproject.resource.constant.AuthorizationGrant;
import com.yourproject.resource.currency.Currency;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.Date;
import java.util.List;

/**
 * Rest controller introducing CRUD actions for working with {@link Sample}.
 *
 * Requires authority USER by default thus restricting access to endpoints for public calls.
 */
@RestController
@RequestMapping(path = "/super-endpoint")
@PreAuthorize(AuthorizationGrant.AUTHORITY_USER)
public class SampleController {

    private static final Logger LOG = LoggerFactory.getLogger(SampleController.class);

    @Autowired
    private SampleService sampleService;

    /**
     * Get Samples by Username.
     *
     * Username is decrypted from JWT and is stored in {@link java.security.Principal}.
     *
     * Gets {@link Sample}s by title, precise date, combination of start and end dates and additional filtering by {@link Currency} code.
     * If no query parameters are set returns all the {@link Sample}s for current user.
     *
     * @param principal {@link Principal} containing username decrypted from JWT.
     * @param startDate start date to search for {@link Sample}.
     * @param endDate end date to search for {@link Sample}.
     * @param date as of date to search {@link Sample} only for this date.
     * @param title {@link Sample} title.
     * @param currencyCode search {@link Sample} by {@link Currency} code.
     *
     * @return {@link ResponseEntity} with list of {@link Sample} from the DB satisfying the search options for current user.
     */
    @GetMapping(path = "/samples")
    public ResponseEntity<List<Sample>> getSamples(Principal principal,
                                                   @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Date startDate,
                                                   @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Date endDate,
                                                   @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Date date,
                                                   @RequestParam(required = false) String title,
                                                   @RequestParam(required = false) String currencyCode) {

        String username = principal.getName();

        if (title != null) {
            return new ResponseEntity<>(this.sampleService.getSamplesByTitle(username, title), HttpStatus.OK);
        }

        if (date != null) {
            return new ResponseEntity<>(this.sampleService.getSamplesByUsernameAndPreciseDate(username, date), HttpStatus.OK);
        }

        if (startDate != null && endDate != null) {
            if (currencyCode != null) {
                return new ResponseEntity<>(this.sampleService.getSamplesByUsernameAndDateAndCurrencyCode(username, startDate, endDate, currencyCode), HttpStatus.OK);
            }

            return new ResponseEntity<>(this.sampleService.getSamplesByOverlappingDateRangeAndUsername(username, startDate, endDate), HttpStatus.OK);
        }

        return new ResponseEntity<>(this.sampleService.getSamplesByUsername(username), HttpStatus.OK);
    }

    /**
     * Create new samples and store those in DB.
     *
     * @param principal {@link Principal} containing username decrypted from JWT.
     * @param samples list of {@link Sample} to be created.
     * @return {@link ResponseEntity} with list of {@link Sample} from the DB with ids set for current user.
     */
    @PostMapping(path = "/samples")
    public ResponseEntity<List<Sample>> createSamples(Principal principal, @RequestBody List<Sample> samples) {
        return new ResponseEntity<>(this.sampleService.create(samples, principal.getName()), HttpStatus.OK);
    }
}
