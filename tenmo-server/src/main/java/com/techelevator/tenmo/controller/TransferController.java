package com.techelevator.tenmo.controller;

import com.techelevator.tenmo.dao.TransferDao;
import com.techelevator.tenmo.exceptions.InvalidTransferException;
import com.techelevator.tenmo.model.Transfer;
import com.techelevator.tenmo.model.dto.UserDTO;
import com.techelevator.tenmo.security.dao.UserDao;
import com.techelevator.tenmo.security.model.User;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

@PreAuthorize("isAuthenticated()")
@RestController
public class TransferController {

    private TransferDao transferDao;
    private UserDao userDao;

    // Constructor
    public TransferController(TransferDao transferDao, UserDao userDao) {
        this.transferDao = transferDao;
        this.userDao = userDao;
    }

    @GetMapping(path = "/balance")
    public BigDecimal getUserBalance(Principal principal) {
        int userId = getCurrentUserId(principal);
        return transferDao.findAccountBalanceByUserId(userId);
    }

    @GetMapping(path = "/user")
    public String getUsername(int userId) {
        for (User user : userDao.findAll()) {
            if (user.getId() == userId) {
                return user.getUsername();
            }
        }
        return null;
    }

    @GetMapping(path = "/users")
    public List<UserDTO> getOtherUsers(Principal principal) {
        List<UserDTO> otherUsers = new ArrayList<>();
        int currentUserId = getCurrentUserId(principal);
        if (currentUserId != -1) {
            for (User user : userDao.findAll()) {
                if (user.getId() != currentUserId) {
                    UserDTO userDTO = new UserDTO();
                    userDTO.setUserId(user.getId());
                    userDTO.setUsername(user.getUsername());
                    otherUsers.add(userDTO);
                }
            }
        }
        return otherUsers;
    }

    @ResponseStatus(HttpStatus.CREATED)
    @RequestMapping(path = "/transfer/send", method = RequestMethod.POST)
    public Transfer sendMoney(
            Principal principal,
            @RequestBody Transfer initialTransfer) throws InvalidTransferException {
        int id = getCurrentUserId(principal);
        initialTransfer.setSenderUserId(id);
        Transfer returnTransfer = transferDao.sendFunds(initialTransfer);
        if (returnTransfer == null) {
            throw new InvalidTransferException();
        }
        return returnTransfer;
    }


    // Helper method
    private int getCurrentUserId(Principal principal) {
        String username = principal.getName();
        return userDao.findIdByUsername(username);
    }
}
