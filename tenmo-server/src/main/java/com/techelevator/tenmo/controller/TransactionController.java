package com.techelevator.tenmo.controller;

import com.techelevator.tenmo.dao.TransactionDao;
import com.techelevator.tenmo.security.dao.UserDao;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.security.Principal;

@PreAuthorize("isAuthenticated()")
@RestController
public class TransactionController {

    private TransactionDao transactionDao;
    private UserDao userDao;

    // Constructor
    public TransactionController(TransactionDao transactionDao, UserDao userDao) {
        this.transactionDao = transactionDao;
        this.userDao = userDao;
    }

    @GetMapping(path = "/balance")
    public BigDecimal getUserBalance(Principal principal) {
        int userId = getCurrentUserId(principal);
        return transactionDao.findAccountBalanceByUserId(userId);
    }

    // Helper method
    private int getCurrentUserId(Principal principal) {
        String username = principal.getName();
        return userDao.findIdByUsername(username);
    }
}
