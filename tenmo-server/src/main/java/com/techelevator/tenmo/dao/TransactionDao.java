package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.security.dao.UserDao;

import java.math.BigDecimal;

public interface TransactionDao {

    public BigDecimal findAccountBalanceByUserId(int userId);
}
