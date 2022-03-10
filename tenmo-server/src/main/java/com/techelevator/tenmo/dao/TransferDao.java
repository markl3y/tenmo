package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.model.Transfer;
import com.techelevator.tenmo.security.dao.UserDao;

import java.math.BigDecimal;
import java.util.List;

public interface TransferDao {

    public BigDecimal findAccountBalanceByUserId(int userId);

    public Transfer sendFunds(Transfer transfer);

    public Transfer requestFunds(Transfer transfer);

    public List<Transfer> findAllTransfersByUserID(int userId);

    public List<Transfer> findTransfersByUserIdAndTransferStatus(int userId, String transferStatus);

    public Transfer changeTransferStatus(int transferId, String transferStatus);

    public Transfer getTransferById(int transferId);
}
