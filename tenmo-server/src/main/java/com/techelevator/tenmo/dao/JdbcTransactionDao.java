package com.techelevator.tenmo.dao;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class JdbcTransactionDao implements TransactionDao {

    // Instance Variables
    private JdbcTemplate jdbcTemplate;

    // Constructor
    public JdbcTransactionDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public BigDecimal findAccountBalanceByUserId(int userId) throws UsernameNotFoundException {
        String sql = "SELECT balance\n" +
                "FROM account\n" +
                "WHERE account.user_id = ?;";
        SqlRowSet rowSet = jdbcTemplate.queryForRowSet(sql, userId);
        if (rowSet.next()){
            return rowSet.getBigDecimal("balance");
        }
        throw new UsernameNotFoundException("User " + userId + " was not found.");
    }
}
