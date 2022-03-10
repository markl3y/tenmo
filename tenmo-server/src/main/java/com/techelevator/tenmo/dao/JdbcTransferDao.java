package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.model.Transfer;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;

@Component
public class JdbcTransferDao implements TransferDao {

    // Instance Variables
    private JdbcTemplate jdbcTemplate;

    // Constructor
    public JdbcTransferDao(JdbcTemplate jdbcTemplate) {
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

    @Override
    public Transfer sendFunds(Transfer transfer) {
        return null;
    }

    @Override
    public Transfer requestFunds(Transfer transfer) {
        return null;
    }

    @Override
    public List<Transfer> findAllTransfersByUserID(int userId) {
        return null;
    }

    @Override
    public List<Transfer> findTransfersByUserIdAndTransferStatus(int userId, String transferStatus) {
        return null;
    }

    @Override
    public Transfer changeTransferStatus(int transferId, String transferStatus) {
        return null;
    }

    @Override
    public Transfer getTransferById(int transferId) {
        return null;
    }


}
