package com.techelevator.tenmo.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.NOT_FOUND, reason = "Transfer Not Found")
public class TransferNotFoundException extends Exception {
    private static final long serialVersionUID = -1L;
    public TransferNotFoundException() {super("Transfer Not Found");}
}
