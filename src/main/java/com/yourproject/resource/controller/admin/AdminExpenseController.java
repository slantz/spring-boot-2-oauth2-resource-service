package com.yourproject.resource.controller.admin;

import com.yourproject.resource.constant.AuthorizationGrant;
import com.yourproject.resource.service.ExpenseService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 */
@RestController
@RequestMapping(path = "/admin/expenses")
@PreAuthorize(AuthorizationGrant.AUTHORITY_ADMIN)
public class AdminExpenseController {

    private static final Logger LOG = LoggerFactory.getLogger(AdminExpenseController.class);

    @Autowired
    private ExpenseService expenseService;

    @GetMapping
    public ResponseEntity<List<Expense>> getExpenses() {
        return new ResponseEntity<>(this.expenseService.findAll(), HttpStatus.OK);
    }

    @GetMapping(path = "/{expenseId}")
    public ResponseEntity<Expense> getExpense(@PathVariable String expenseId) {
        return new ResponseEntity<>(this.expenseService.findById(expenseId), HttpStatus.OK);
    }

    @GetMapping(path = "/spenders/{spenderUsername}")
    public ResponseEntity<List<Expense>> getExpensesBySpenderUsername(@PathVariable String spenderUsername) {
        return new ResponseEntity<>(this.expenseService.findBySpenderUsername(spenderUsername), HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<List<Expense>> createExpenses(@RequestBody List<Expense> expenses) {
        return new ResponseEntity<>(this.expenseService.create(expenses), HttpStatus.OK);
    }

    @PutMapping
    public ResponseEntity<List<Expense>> putExpense(@RequestBody List<Expense> expenses) {
        return new ResponseEntity<>(this.expenseService.update(expenses), HttpStatus.OK);
    }

    @DeleteMapping
    public ResponseEntity<Boolean> deleteAllExpenses() {
        return new ResponseEntity<>(this.expenseService.deleteAll(), HttpStatus.OK);
    }

    @DeleteMapping(path = "/{expenseId}")
    public ResponseEntity<Boolean> deleteExpensesById(@PathVariable String expenseId) {
        return new ResponseEntity<>(this.expenseService.deleteById(expenseId), HttpStatus.OK);
    }
}
