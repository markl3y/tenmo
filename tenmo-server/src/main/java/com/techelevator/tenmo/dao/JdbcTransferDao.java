package com.techelevator.tenmo.dao;


import com.techelevator.tenmo.exceptions.TransferNotFoundException;
import com.techelevator.tenmo.model.Transfer;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Component
public class JdbcTransferDao implements TransferDao {

    //Instance Vars
    public final String REQUEST = "Request";
    public final String SEND = "Send";
    public int requestTypeId;
    public int sendTypeId;
    public final String PENDING = "Pending";
    public final String APPROVED = "Approved";
    public final String REJECTED = "Rejected";
    public int pendingStatusId;
    public int approvedStatusId;
    public int rejectedStatusId;
    private final String SEND_SQL =
            "UPDATE account SET balance = (balance - ?) " + // money sent
            "WHERE account_id = ?; " + // sender account
            "UPDATE account SET balance = (balance + ?) " + // money received
            "WHERE account_id = ?;"; // receiver account
    private final String RECORD_SQL =
            "INSERT INTO transfer (transfer_type_id, transfer_status_id, account_from, account_to, amount) " +
            "VALUES (?, ?, ?, ?, ?) RETURNING transfer_id;";
    private JdbcTemplate jdbcTemplate;

    // Constructor
    public JdbcTransferDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        requestTypeId = getTransferTypeId(REQUEST);
        sendTypeId = getTransferTypeId(SEND);
        pendingStatusId = getTransferStatusId(PENDING);
        approvedStatusId = getTransferStatusId(APPROVED);
        rejectedStatusId = getTransferStatusId(REJECTED);
    }

    @Override
    public BigDecimal findAccountBalanceByUserId(int userId) throws UsernameNotFoundException {
        String sql = "SELECT balance\n" +
                "FROM account\n" +
                "WHERE user_id = ?;";
        SqlRowSet rowSet = jdbcTemplate.queryForRowSet(sql, userId);
        if (rowSet.next()){
            return rowSet.getBigDecimal("balance");
        }
        throw new UsernameNotFoundException("User " + userId + " was not found.");
    }

    @Override
    public Transfer sendFunds(Transfer transfer) throws TransferNotFoundException{
        // Unpack transfer
        int senderUserId = transfer.getSenderUserId();
        int receiverUserId = transfer.getReceiverUserId();
        BigDecimal transferAmount = transfer.getTransferAmount();
        String transferType = transfer.getTransferType();
        String transferStatus = transfer.getTransferStatus();
        // check to make sure this is going to be a valid transfer
        boolean isValidTransfer = validateTransfer(senderUserId, receiverUserId, transferAmount, SEND
                , transferType, APPROVED, transferStatus);
        // only conduct the transfer if it is a valid transfer
        if (isValidTransfer) {
            // call some helper methods to finish preparing the SQL query
            int senderAccountId = getAccountByUserId(senderUserId);
            int receiverAccountId = getAccountByUserId(receiverUserId);
            Integer transferId = -1;
            jdbcTemplate.update(SEND_SQL, transferAmount, senderAccountId, transferAmount, receiverAccountId);
            transferId = jdbcTemplate.queryForObject(RECORD_SQL,
                    Integer.class,
                    sendTypeId,
                    approvedStatusId,
                    senderAccountId,
                    receiverAccountId,
                    transferAmount);
            return getTransferById(transferId);
        }
        return null;
    }

    @Override
    public Transfer requestFunds(Transfer transfer) {
        Transfer returnTransfer = new Transfer();
        return returnTransfer;
    }

    @Override
    public List<Transfer> findAllTransfersByUserID(int userId) {
        int account = getAccountByUserId(userId);
        List<Transfer> transfers = new ArrayList<>();
        String sql = "SELECT * FROM transfer WHERE (account_from = ? OR (account_to = ?));";
        SqlRowSet rowSet = jdbcTemplate.queryForRowSet(sql, account, account);
        while (rowSet.next()) {
            transfers.add(mapRowToTransfer(rowSet));
        }
        return transfers;
    }

    @Override
    public List<Transfer> findTransfersByUserIdAndTransferStatus(int userId, String transferStatus) {
        return null;
    }

    @Override
    public Transfer changeTransferStatus(Integer transferId, String transferStatus) {
        return null;
    }

    @Override
    public Transfer getTransferById(Integer transferId) throws TransferNotFoundException {
        final String sql = "SELECT * FROM transfer WHERE transfer_id = ?";
//                "SELECT" +
//                "transfer.transfer_id," +
//                "transfer_type.transfer_type_desc," +
//                "transfer_status.transfer_status_desc," +
//                "(SELECT account.user_id FROM account WHERE account.account_id = transfer.account_from)," +
//                "(SELECT account.user_id FROM account WHERE account.account_id = transfer.account_to)," +
//                "amount" +
//                "FROM transfer" +
//                "JOIN transfer_type ON transfer_type.transfer_type_id = transfer.transfer_type_id" +
//                "JOIN transfer_status ON transfer_status.transfer_status_id = transfer.transfer_status_id" +
//                "WHERE transfer_id = ?;";
        SqlRowSet rowSet = jdbcTemplate.queryForRowSet(sql, transferId);
        if (rowSet.next()){
            return mapRowToTransfer(rowSet);
        }
        return null;
    }

    public boolean validateTransfer(
            int senderUserId,
            int receiverUserId,
            BigDecimal transferAmount,
            String expectedTransferType,
            String actualTransferType,
            String expectedTransferStatus,
            String actualTransferStatus) {
        boolean isTransferValid = true;
        if (senderUserId == receiverUserId) {
            isTransferValid = false;
        }
        if (!isTransferAmountValid(transferAmount, senderUserId)) {
            isTransferValid = false;
        }
        if (!Objects.equals(expectedTransferType, actualTransferType)) {
            isTransferValid = false;
        }
        if (!Objects.equals(expectedTransferStatus, actualTransferStatus)) {
            isTransferValid = false;
        }
        return isTransferValid;
    }

    public boolean isTransferAmountValid(BigDecimal transferAmount, int userId) {
        // make sure that transfer amount is greater than 0
        if (transferAmount.compareTo(BigDecimal.ZERO) <= 0) {
            return false;
        }
        // make sure that account balance after transfer will be valid
        BigDecimal userBalance = findAccountBalanceByUserId(userId);
        BigDecimal userBalanceAfterTransfer = userBalance.subtract(transferAmount);
        return userBalanceAfterTransfer.compareTo(BigDecimal.ZERO) >= 0;
    }

    public int getTransferTypeId (String transferType) {
        final String sql = "SELECT transfer_type_id\n" +
                "FROM transfer_type\n" +
                "WHERE transfer_type_desc ILIKE ?;";
        int id = -1;
        SqlRowSet rowSet = jdbcTemplate.queryForRowSet(sql, transferType);
        if (rowSet.next()) {
            id = rowSet.getInt("transfer_type_id");
        }
        return id;
    }

    public int getTransferStatusId (String transferStatus) {
        final String sql = "SELECT transfer_status_id\n" +
                "FROM transfer_status\n" +
                "WHERE transfer_status_desc ILIKE ?;";
        int id = -1;
        SqlRowSet rowSet = jdbcTemplate.queryForRowSet(sql, transferStatus);
        if (rowSet.next()) {
            id = rowSet.getInt("transfer_status_id");
        }
        return id;
    }

    public String getTransferTypeString (int transferType) {
        final String sql = "SELECT transfer_type_desc\n" +
                "FROM transfer_type\n" +
                "WHERE transfer_type_id = ?;";
        String id = "";
        SqlRowSet rowSet = jdbcTemplate.queryForRowSet(sql, transferType);
        if (rowSet.next()) {
            id = rowSet.getString("transfer_type_desc");
        }
        return id;
    }

    public String getTransferStatusString (int transferStatus) {
        final String sql = "SELECT transfer_status_desc\n" +
                "FROM transfer_status\n" +
                "WHERE transfer_status_id = ?;";
        String status = "";
        SqlRowSet rowSet = jdbcTemplate.queryForRowSet(sql, transferStatus);
        if (rowSet.next()) {
            status = rowSet.getString("transfer_status_desc");
        }
        return status;
    }

    public int getAccountByUserId (int userId) {
        final String sql = "SELECT account_id\n" +
                "FROM account\n" +
                "WHERE user_id = ?\n" +
                "LIMIT 1;";
        int id = -1;
        SqlRowSet rowSet = jdbcTemplate.queryForRowSet(sql, userId);
        if (rowSet.next()) {
            id = rowSet.getInt("account_id");
        }
        return id;
    }

    public int getUserIdByAccountId (int accountId) {
        final String sql = "SELECT user_id\n" +
                "FROM account\n" +
                "WHERE account_id = ?\n" +
                "LIMIT 1;";
        int id = -1;
        SqlRowSet rowSet = jdbcTemplate.queryForRowSet(sql, accountId);
        if (rowSet.next()) {
            id = rowSet.getInt("user_id");
        }
        return id;
    }

    private Transfer mapRowToTransfer(SqlRowSet rs) {
        Transfer transfer = new Transfer();
        transfer.setTransferId(rs.getInt("transfer_id"));
        transfer.setSenderUserId(getUserIdByAccountId(rs.getInt("account_from")));
        transfer.setReceiverUserId(getUserIdByAccountId(rs.getInt("account_to")));
        transfer.setTransferAmount(rs.getBigDecimal("amount"));
        transfer.setTransferType(getTransferTypeString(rs.getInt("transfer_type_id")));
        transfer.setTransferStatus(getTransferStatusString(rs.getInt("transfer_status_id")));
        return transfer;
    }
}
