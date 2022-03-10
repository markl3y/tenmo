package com.techelevator.tenmo.model;

import com.techelevator.tenmo.security.model.User;

import java.math.BigDecimal;

public class Transfer {

    // Instance variables
    int transferId;
    int senderUserId;
    int receiverUserId;
    BigDecimal amountToTransfer;
    String transferType;
    String transferStatus;

    // Constructors
    public Transfer() {}

    public Transfer(int transferId, int senderUserId, int receiverUserId, BigDecimal amountToTransfer,
                    String transferType, String transferStatus) {
        this.transferId = transferId;
        this.senderUserId = senderUserId;
        this.receiverUserId = receiverUserId;
        this.amountToTransfer = amountToTransfer;
        this.transferType = transferType;
        this.transferStatus = transferStatus;
    }

    // Getters & Setters
    public int getTransferId() {
        return transferId;
    }

    public void setTransferId(int transferId) {
        this.transferId = transferId;
    }

    public int getSenderUserId() {
        return senderUserId;
    }

    public void setSenderUserId(int senderUserId) {
        this.senderUserId = senderUserId;
    }

    public int getReceiverUserId() {
        return receiverUserId;
    }

    public void setReceiverUserId(int receiverUserId) {
        this.receiverUserId = receiverUserId;
    }

    public BigDecimal getAmountToTransfer() {
        return amountToTransfer;
    }

    public void setAmountToTransfer(BigDecimal amountToTransfer) {
        this.amountToTransfer = amountToTransfer;
    }

    public String getTransferType() {
        return transferType;
    }

    public void setTransferType(String transferType) {
        this.transferType = transferType;
    }

    public String getTransferStatus() {
        return transferStatus;
    }

    public void setTransferStatus(String transferStatus) {
        this.transferStatus = transferStatus;
    }
}
