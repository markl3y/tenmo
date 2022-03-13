package com.techelevator.tenmo.model;

import java.math.BigDecimal;
import java.text.NumberFormat;

public class Transfer {
    private int transferId;
    private String senderUsername;
    private String receiverUsername;
    private String type;
    private String status;
    private BigDecimal amount;

    public Transfer(){}

    public Transfer(int transferId, String senderUsername, String receiverUsername, String type, String status, BigDecimal amount){
        this.transferId = transferId;
        this.senderUsername = senderUsername;
        this.receiverUsername = receiverUsername;
        this.type = type;
        this.status = status;
        this.amount = amount;
    }

    public int getTransferId() {
        return transferId;
    }

    public String getSenderUsername() {
        return senderUsername;
    }

    public String getReceiverUsername() {
        return receiverUsername;
    }

    public String getType() {
        return type;
    }

    public String getStatus() {
        return status;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setTransferId(int transferId) {
        this.transferId = transferId;
    }

    public void setSenderUsername(String senderUsername) {
        this.senderUsername = senderUsername;
    }

    public void setReceiverUsername(String receiverUsername) {
        this.receiverUsername = receiverUsername;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    @Override
    public String toString() {
        String returnString = "--------------------------------------------\n" +
        "Transfer Details\n" +
                "--------------------------------------------\n" +
                "Id: " + transferId + "\n" +
                "From: " + senderUsername + "\n" +
                "To: " + receiverUsername + "\n" +
                "Type: " + type + "\n" +
                "Status: " + status + "\n" +
                "Amount: " + NumberFormat.getCurrencyInstance().format(amount);
        return returnString;
    }
}
