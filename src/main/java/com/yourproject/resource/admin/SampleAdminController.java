package com.yourproject.resource.admin;

import com.yourproject.resource.constant.AuthorizationGrant;
import com.yourproject.resource.model.adjust.Authority;
import com.yourproject.resource.sample.Sample;
import com.yourproject.resource.sample.SampleService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * This is the controller for super-endpoint for admin users only.
 *
 * Requires authority ADMIN by default thus restricting access to endpoints for public calls
 * and regular users without administrator privileges.
 */
@RestController
@RequestMapping(path = "/admin/super-endpoint")
@PreAuthorize(AuthorizationGrant.AUTHORITY_ADMIN)
public class SampleAdminController {

    private static final Logger LOG = LoggerFactory.getLogger(SampleAdminController.class);

    @Autowired
    private SampleService sampleService;

    @GetMapping(path = "/samples")
    public ResponseEntity<List<Sample>> getSamples() {
        return new ResponseEntity<>(this.sampleService.get(), HttpStatus.OK);
    }

    @GetMapping(path = "/samples/{username}")
    public ResponseEntity<List<Sample>> getSamplesByUsername(@PathVariable String username) {
        return new ResponseEntity<>(this.sampleService.getSamplesByUsername(username), HttpStatus.OK);
    }

    @GetMapping(path = "/{username}/authorities")
    public ResponseEntity<List<Authority>> getUsernameAuthorities(@PathVariable String username) {
        return new ResponseEntity<>(this.sampleService.getUsernameAuthorities(username), HttpStatus.OK);
    }
}
