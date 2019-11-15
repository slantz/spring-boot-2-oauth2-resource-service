package com.yourproject.resource.sample;

import com.yourproject.resource.constant.AuthorizationGrant;
import com.yourproject.resource.model.mongo.Sample;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.Date;
import java.util.List;

@RestController
@RequestMapping(path = "/super-endpoint")
@PreAuthorize(AuthorizationGrant.AUTHORITY_USER)
public class SampleController {

    private static final Logger LOG = LoggerFactory.getLogger(SampleController.class);

    /**
     * Get Samples by Username.
     *
     * Username is decrypted from JWT and is stored in {@link java.security.Principal}.
     *
     *
     * @return
     */
    @GetMapping(path = "/samples")
    public ResponseEntity<String> getSamples(Principal principal,
                                             @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Date startDate,
                                             @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Date endDate,
                                             @RequestParam(required = false) String title,
                                             @RequestParam(required = false) String currencyTitle) {



        return new ResponseEntity<>("", HttpStatus.OK);
    }
}
