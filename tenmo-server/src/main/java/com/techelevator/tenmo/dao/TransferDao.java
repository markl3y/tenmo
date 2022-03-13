package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.exceptions.TransferNotFoundException;
import com.techelevator.tenmo.model.Transfer;
import com.techelevator.tenmo.security.dao.UserDao;

import javax.validation.constraints.Null;
import java.math.BigDecimal;
import java.util.List;

public interface TransferDao {

    public BigDecimal findAccountBalanceByUserId(int userId);

    public Transfer sendFunds(Transfer transfer) throws TransferNotFoundException, NullPointerException;

    public Transfer requestFunds(Transfer transfer);

    public List<Transfer> findAllTransfersByUserID(int userId);

    public List<Transfer> findTransfersByUserIdAndTransferStatus(int userId, String transferStatus);

    public Transfer changeTransferStatus(Integer transferId, String transferStatus);

    public Transfer getTransferById(Integer transferId) throws TransferNotFoundException, NullPointerException;
}
