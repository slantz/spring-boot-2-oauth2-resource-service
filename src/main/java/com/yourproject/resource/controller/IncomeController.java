package com.yourproject.resource.controller;

import com.yourproject.resource.constant.AuthorizationGrant;
import com.yourproject.resource.service.IncomeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.Date;
import java.util.List;

/**
 */
@RestController
@RequestMapping(path = "/incomes")
@PreAuthorize(AuthorizationGrant.AUTHORITY_USER)
public class IncomeController {

    private static final Logger LOG = LoggerFactory.getLogger(IncomeController.class);

    @Autowired
    private IncomeService incomeService;

    @Autowired
    private CurrencyService currencyService;

    @Autowired
    private PeriodService periodService;

    @GetMapping
    public ResponseEntity<List<Income>> getIncomes(Principal principal,
                                                   @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Date asOfDate) {
        if (asOfDate != null) {
            return new ResponseEntity<>(this.incomeService.findByOverlappingDateRangeAndSpenderUsername(asOfDate, asOfDate, principal.getName()), HttpStatus.OK);
        }

        return new ResponseEntity<>(this.incomeService.findBySpenderUsername(principal.getName()), HttpStatus.OK);
    }

    @GetMapping(path = "/{incomeId}")
    public ResponseEntity<Income> getIncomeById(@PathVariable String incomeId) {
        return new ResponseEntity<>(this.incomeService.findById(incomeId), HttpStatus.OK);
    }

    @GetMapping(path = "/title/{incomeTitle}")
    public ResponseEntity<List<Income>> getIncomeByTitle(Principal principal, @PathVariable String incomeTitle) {
        return new ResponseEntity<>(this.incomeService.findByTitle(incomeTitle, principal.getName()), HttpStatus.OK);
    }

    @GetMapping(path = "/period/id/{periodId}")
    public ResponseEntity<List<Income>> getIncomesByPeriodId(Principal principal, @PathVariable String periodId) {
        return new ResponseEntity<>(this.incomeService.findByPeriodId(periodId, principal.getName()), HttpStatus.OK);
    }

    @GetMapping(path = "/period/key/{periodKey}")
    public ResponseEntity<List<Income>> getIncomesByPeriodKey(Principal principal, @PathVariable String periodKey) {
        Period period = this.periodService.findByKey(periodKey);
        return new ResponseEntity<>(this.incomeService.findByPeriodId(period.getId(), principal.getName()), HttpStatus.OK);
    }

    @GetMapping(path = "/currency/id/{currencyId}")
    public ResponseEntity<List<Income>> getIncomesByCurrencyId(Principal principal, @PathVariable String currencyId) {
        return new ResponseEntity<>(this.incomeService.findByCurrencyId(currencyId, principal.getName()), HttpStatus.OK);
    }

    @GetMapping(path = "/currency/title/{currencyTitle}")
    public ResponseEntity<List<Income>> getIncomesByCurrencyTitle(Principal principal, @PathVariable String currencyTitle) {
        Currency currency = this.currencyService.findByTitle(currencyTitle);
        return new ResponseEntity<>(this.incomeService.findByCurrencyId(currency.getId(), principal.getName()), HttpStatus.OK);
    }

    @GetMapping(path = "/date/{incomeDate}")
    public ResponseEntity<List<Income>> getIncomeByDate(Principal principal, @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Date incomeDate) {
        return new ResponseEntity<>(this.incomeService.findByDate(incomeDate, principal.getName()), HttpStatus.OK);
    }

    @GetMapping(path = "/expired-date/{expiredDate}")
    public ResponseEntity<Income> getIncomeByExpiredDate(Principal principal, @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Date expiredDate) {
        return new ResponseEntity<>(this.incomeService.findByExpiredDate(expiredDate, principal.getName()), HttpStatus.OK);
    }

    @GetMapping(path = "/date/{incomeDate}/expired-date/{expiredDate}")
    public ResponseEntity<List<Income>> getIncomeByDate(Principal principal,
                                                        @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Date incomeDate,
                                                        @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Date expiredDate,
                                                        @RequestParam(required = false) Boolean isOverlapping) {
        if (isOverlapping != null && isOverlapping) {
            return new ResponseEntity<>(this.incomeService.findByOverlappingDateRangeAndSpenderUsername(incomeDate, expiredDate, principal.getName()), HttpStatus.OK);
        }

        return new ResponseEntity<>(this.incomeService.findByInclusiveDateRangeAndSpenderUsername(incomeDate, expiredDate, principal.getName()), HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<List<Income>> createIncomes(Principal principal, @RequestBody List<Income> incomes) {
        return new ResponseEntity<>(this.incomeService.create(incomes, principal.getName()), HttpStatus.OK);
    }

    @PutMapping
    public ResponseEntity<List<Income>> updateIncomes(Principal principal, @RequestBody List<Income> incomes) {
        return new ResponseEntity<>(this.incomeService.update(incomes, principal.getName()), HttpStatus.OK);
    }
}