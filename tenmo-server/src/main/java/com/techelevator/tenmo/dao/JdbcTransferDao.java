package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.TenmoApplication;
import com.techelevator.tenmo.model.Transfer;
import org.springframework.data.relational.core.sql.In;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;

@Component
public class JdbcTransferDao implements TransferDao {

    // Transfer Types
    //TODO --great candidates for enums
    public final String REQUEST = "Request";
    public final String SEND = "Send";

    // Transfer Type Ids
    //TODO -- map to transfer types
    public int requestTypeId = getTransferTypeId(REQUEST);
    public int sendTypeId = getTransferTypeId(SEND);

    // Transfer Statuses
    //TODO --great candidates for enums
    public final String PENDING = "Pending";
    public final String APPROVED = "Approved";
    public final String REJECTED = "Rejected";

    // Transfer Status Ids
    //TODO -- map to transfer statuses
    public int pendingStatusId = getTransferStatusId(PENDING);
    public int approvedStatusId = getTransferStatusId(APPROVED);
    public int rejectedStatusId = getTransferStatusId(REJECTED);

    // SQL Constants
    private final String  transferSql =
            "UPDATE account SET balance = (balance - ?)\n" + // money sent
            "WHERE account_id = ?;\n" + // sender account
            "UPDATE account SET balance = (balance + ?)\n" + // money received
            "WHERE account_id = ?;"; // receiver account
    private final String transferRecordSql =
            "INSERT INTO transfer (transfer_type_id, transfer_status_id, account_from, account_to, amount)\n" +
            "VALUES (?, ?, ?, ?, ?) RETURNING transfer_id;";

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
        // Unpack transfer
        int senderUserId = transfer.getSenderUserId();
        int receiverUserId = transfer.getReceiverUserId();
        BigDecimal amountToTransfer = transfer.getAmountToTransfer();
        String transferType = transfer.getTransferType();
        String transferStatus = transfer.getTransferStatus();
        // define the Transfer object to be returned (this will be created from the transaction record in the database)
        Transfer transferFromDatabase = null;
        // check to make sure this is going to be a valid transfer
        boolean isValidTransfer = validateTransfer(senderUserId, receiverUserId, amountToTransfer, SEND
                , transferType, APPROVED, transferStatus);
        // only conduct the transfer if it is a valid transfer
        if (isValidTransfer) {
            // call some helper methods to finish preparing the SQL query
            int senderAccountId = getAccountByUserId(senderUserId);
            int receiverAccountId = getAccountByUserId(receiverUserId);
            int transferId = -1;
            final String sql = transferSql + "\n" + transferRecordSql;
            SqlRowSet rowSet = jdbcTemplate.queryForRowSet(sql,
                    // these values are used to adjust the balance of each account
                    amountToTransfer, senderUserId, amountToTransfer, receiverUserId,
                    // these values are used to create a record of the transfer
                    sendTypeId, approvedStatusId, senderAccountId, receiverAccountId, amountToTransfer);
            if (rowSet.next()) {
                transferId = rowSet.getInt("transfer_id");
            }
            transferFromDatabase = getTransferById(transferId);
        }
        return transferFromDatabase;
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
        final String sql =
                "SELECT \n" +
                "\ttransfer.transfer_id\n" +
                "\t, transfer_type.transfer_type_desc\n" +
                "\t, transfer_status.transfer_status_desc\n" +
                "\t, (SELECT account.user_id FROM account WHERE account.account_id = transfer.account_from)\n" +
                "\t, (SELECT account.user_id FROM account WHERE account.account_id = transfer.account_to)\n" +
                "\t, amount\n" +
                "FROM transfer\n" +
                "JOIN transfer_type ON transfer_type.transfer_type_id = transfer.transfer_type_id\n" +
                "JOIN transfer_status ON transfer_status.transfer_status_id = transfer.transfer_status_id\n" +
                "WHERE transfer_id = ?;";
        return jdbcTemplate.queryForObject(sql, Transfer.class, transferId);
    }

    public boolean validateTransfer(
            int senderUserId,
            int receiverUserId,
            BigDecimal amountToTransfer,
            String expectedTransferType,
            String actualTransferType,
            String expectedTransferStatus,
            String actualTransferStatus) {
        boolean isTransferValid = true;
        if (senderUserId == receiverUserId) {
            isTransferValid = false;
        }
        if (isTransferAmountValid(amountToTransfer, senderUserId)) {
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
        // make sure that transfer amount is greater than user is above 0
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
}
