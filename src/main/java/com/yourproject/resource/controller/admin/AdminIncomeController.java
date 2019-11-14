package com.yourproject.resource.controller.admin;

import com.yourproject.resource.service.IncomeService;
import com.yourproject.resource.constant.AuthorizationGrant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.List;

/**
 */
@RestController
@RequestMapping(path = "/admin/incomes")
@PreAuthorize(AuthorizationGrant.AUTHORITY_ADMIN)
public class AdminIncomeController {

    private static final Logger LOG = LoggerFactory.getLogger(com.yourproject.resource.controller.admin.AdminIncomeController.class);

    @Autowired
    private IncomeService incomeService;

    @Autowired
    private CurrencyService currencyService;

    @Autowired
    private PeriodService periodService;

    @GetMapping
    public ResponseEntity<List<Income>> getIncomes() {
        return new ResponseEntity<>(this.incomeService.findAll(), HttpStatus.OK);
    }

    @GetMapping(path = "/{incomeId}")
    public ResponseEntity<Income> getIncomeById(@PathVariable String incomeId) {
        return new ResponseEntity<>(this.incomeService.findById(incomeId), HttpStatus.OK);
    }

    @GetMapping(path = "/title/{incomeTitle}")
    public ResponseEntity<List<Income>> getIncomeByTitle(@PathVariable String incomeTitle) {
        return new ResponseEntity<>(this.incomeService.findByTitle(incomeTitle), HttpStatus.OK);
    }

    @GetMapping(path = "/period/id/{periodId}")
    public ResponseEntity<List<Income>> getIncomesByPeriodId(@PathVariable String periodId) {
        return new ResponseEntity<>(this.incomeService.findByPeriodId(periodId), HttpStatus.OK);
    }

    @GetMapping(path = "/period/key/{periodKey}")
    public ResponseEntity<List<Income>> getIncomesByPeriodKey(@PathVariable String periodKey) {
        Period period = this.periodService.findByKey(periodKey);
        return new ResponseEntity<>(this.incomeService.findByPeriodId(period.getId()), HttpStatus.OK);
    }

    @GetMapping(path = "/currency/id/{currencyId}")
    public ResponseEntity<List<Income>> getIncomesByCurrencyId(@PathVariable String currencyId) {
        return new ResponseEntity<>(this.incomeService.findByCurrencyId(currencyId), HttpStatus.OK);
    }

    @GetMapping(path = "/currency/title/{currencyTitle}")
    public ResponseEntity<List<Income>> getIncomesByCurrencyTitle(@PathVariable String currencyTitle) {
        Currency currency = this.currencyService.findByTitle(currencyTitle);
        return new ResponseEntity<>(this.incomeService.findByCurrencyId(currency.getId()), HttpStatus.OK);
    }

    @GetMapping(path = "/date/{incomeDate}")
    public ResponseEntity<Income> getIncomeByDate(@PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Date incomeDate) {
        return new ResponseEntity<>(this.incomeService.findByDate(incomeDate), HttpStatus.OK);
    }

    @GetMapping(path = "/expired-date/{expiredDate}")
    public ResponseEntity<Income> getIncomeByExpiredDate(@PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Date expiredDate) {
        return new ResponseEntity<>(this.incomeService.findByExpiredDate(expiredDate), HttpStatus.OK);
    }

    @GetMapping(path = "/spenders/{spenderUsername}")
    public ResponseEntity<List<Income>> getIncomeBySpenderUsername(@PathVariable String spenderUsername) {
        return new ResponseEntity<>(this.incomeService.findBySpenderUsername(spenderUsername), HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<List<Income>> createIncomes(@RequestBody List<Income> incomes) {
        return new ResponseEntity<>(this.incomeService.create(incomes), HttpStatus.OK);
    }

    @PutMapping
    public ResponseEntity<List<Income>> updateIncomes(@RequestBody List<Income> incomes) {
        return new ResponseEntity<>(this.incomeService.update(incomes), HttpStatus.OK);
    }

    @DeleteMapping
    public ResponseEntity<Boolean> deleteAllIncomes() {
        return new ResponseEntity<>(this.incomeService.deleteAll(), HttpStatus.OK);
    }

    @DeleteMapping(path = "/{incomeId}")
    public ResponseEntity<Boolean> deleteIncomeById(@PathVariable String incomeId) {
        return new ResponseEntity<>(this.incomeService.deleteById(incomeId), HttpStatus.OK);
    }
}