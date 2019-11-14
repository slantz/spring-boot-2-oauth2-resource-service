package com.yourproject.resource.controller;

import com.yourproject.resource.constant.AuthorizationGrant;
import com.yourproject.resource.model.adjusted.total.TotalCategorizedExpense;
import com.yourproject.resource.model.adjusted.total.TotalExpense;
import com.yourproject.resource.service.ExpenseService;
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
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.Date;
import java.util.List;

/**
 */
@RestController
@RequestMapping(path = "/expenses")
@PreAuthorize(AuthorizationGrant.AUTHORITY_USER)
public class ExpenseController {

    private static final Logger LOG = LoggerFactory.getLogger(ExpenseController.class);

    @Autowired
    private ExpenseService expenseService;

    @GetMapping
    public ResponseEntity<List<Expense>> getExpenses(Principal principal,
                                                     @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Date startDate,
                                                     @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Date endDate,
                                                     @RequestParam(required = false) String currencyTitle,
                                                     @RequestParam(required = false) String currencyId,
                                                     @RequestParam(required = false) String categoryType,
                                                     @RequestParam(required = false) String categoryId) {

        if (startDate != null && endDate != null && currencyId != null) {
            return new ResponseEntity<>(this.expenseService.findBySpenderUsernameAndDateAndCurrencyId(principal.getName(), startDate, endDate, currencyId), HttpStatus.OK);
        }

        if (startDate != null && endDate != null && categoryId != null) {
            return new ResponseEntity<>(this.expenseService.findBySpenderUsernameAndDateAndCategoryId(principal.getName(), startDate, endDate, categoryId), HttpStatus.OK);
        }

        if (startDate != null && endDate != null && currencyTitle != null) {
            return new ResponseEntity<>(this.expenseService.findBySpenderUsernameAndDateAndCurrencyTitle(principal.getName(), startDate, endDate, currencyTitle), HttpStatus.OK);
        }

        if (startDate != null && endDate != null && categoryType != null) {
            return new ResponseEntity<>(this.expenseService.findBySpenderUsernameAndDateAndCategoryType(principal.getName(), startDate, endDate, categoryType), HttpStatus.OK);
        }

        if (startDate != null && endDate != null) {
            return new ResponseEntity<>(this.expenseService.findBySpenderUsernameAndDate(principal.getName(), startDate, endDate), HttpStatus.OK);
        }

        if (categoryType != null) {
            return new ResponseEntity<>(this.expenseService.findBySpenderUsernameAndCategoryType(principal.getName(), categoryType), HttpStatus.OK);
        }

        return new ResponseEntity<>(this.expenseService.findBySpenderUsername(principal.getName()), HttpStatus.OK);
    }

    @RequestMapping(path = "/{expenseId}", method = RequestMethod.GET)
    public ResponseEntity<Expense> getExpense(@PathVariable String expenseId) {
        return new ResponseEntity<>(this.expenseService.findById(expenseId), HttpStatus.OK);
    }

    @GetMapping(path = "/total")
    @PreAuthorize(AuthorizationGrant.AUTHORITY_ADMIN)
    public ResponseEntity<TotalExpense> getTotal(Principal principal,
                                                 @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Date startDate,
                                                 @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Date endDate) {
        if (startDate != null && endDate != null) {
            return new ResponseEntity<>(this.expenseService.totalBySpenderUsernameAndDates(principal.getName(), startDate, endDate), HttpStatus.OK);
        }

        return new ResponseEntity<>(this.expenseService.totalBySpenderUsername(principal.getName()), HttpStatus.OK);
    }

    @GetMapping(path = "/total/categorized")
    @PreAuthorize(AuthorizationGrant.AUTHORITY_ADMIN)
    public ResponseEntity<TotalCategorizedExpense> getTotalCategorized(Principal principal,
                                                                       @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Date startDate,
                                                                       @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Date endDate) {
        if (startDate != null && endDate != null) {
            return new ResponseEntity<>(this.expenseService.totalCategorizedBySpenderUsernameAndDates(principal.getName(), startDate, endDate), HttpStatus.OK);
        }

        return new ResponseEntity<>(this.expenseService.totalCategorizedBySpenderUsername(principal.getName()), HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<List<Expense>> createExpenses(Principal principal, @RequestBody List<Expense> expenses) {
        return new ResponseEntity<>(this.expenseService.create(expenses, principal.getName()), HttpStatus.OK);
    }

    @PutMapping
    public ResponseEntity<List<Expense>> putExpense(Principal principal, @RequestBody List<Expense> expenses) {
        return new ResponseEntity<>(this.expenseService.update(expenses, principal.getName()), HttpStatus.OK);
    }
}
