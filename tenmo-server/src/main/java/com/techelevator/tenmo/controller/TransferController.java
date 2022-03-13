package com.techelevator.tenmo.controller;

import com.techelevator.tenmo.dao.TransferDao;
import com.techelevator.tenmo.exceptions.InvalidTransferException;
import com.techelevator.tenmo.exceptions.TransferNotFoundException;
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

    @PreAuthorize("hasRole('ROLE_USER')")
    @GetMapping(path = "/balance")
    public BigDecimal getUserBalance(Principal principal) {
        int userId = getCurrentUserId(principal);
        return transferDao.findAccountBalanceByUserId(userId);
    }

    @PreAuthorize("hasRole('ROLE_USER')")
    @GetMapping(path = "/user")
    public String getUsername(int userId) {
        for (User user : userDao.findAll()) {
            if (user.getId() == userId) {
                return user.getUsername();
            }
        }
        return null;
    }

    @PreAuthorize("hasRole('ROLE_USER')")
    @GetMapping(path = "/users")
    public List<UserDTO> getOtherUsers(Principal principal) {
        List<UserDTO> otherUsers = new ArrayList<>();
        int currentUserId = getCurrentUserId(principal);
        if (currentUserId != -1) {
            for (User user : userDao.findAll()) {
                if (user.getId() != currentUserId) {
                    UserDTO userDTO = new UserDTO();
                    userDTO.setId(user.getId());
                    userDTO.setUsername(user.getUsername());
                    otherUsers.add(userDTO);
                }
            }
        }
        return otherUsers;
    }

    //This method posts a Transfer of type Send, and returns the transfer from transferDao.
    @PreAuthorize("hasRole('ROLE_USER')")
    @ResponseStatus(HttpStatus.CREATED)
    @RequestMapping(path = "/transfer/send", method = RequestMethod.POST)
    public Transfer sendMoney(
            Principal principal,
            @RequestBody Transfer initialTransfer) throws InvalidTransferException, TransferNotFoundException {
        int id = getCurrentUserId(principal);
        //Sets initial Transfers sender id to current sender id.
        initialTransfer.setSenderUserId(id);
        //Plugs and chugs through the transferDao.
        Transfer returnTransfer = transferDao.sendFunds(initialTransfer);
        if (returnTransfer == null) {
            throw new InvalidTransferException();
        }
        //If the returned transfer is not null, then all is well in the world, and it returns that transfer.
        return returnTransfer;
    }

    //Find specific Transfer with a path variable of the transferId.
    @PreAuthorize("hasRole('ROLE_USER')")
    @RequestMapping(path = "transfers/{id}", method = RequestMethod.GET)
    public Transfer getTransfer(@PathVariable int id) throws TransferNotFoundException {
        return transferDao.getTransferById(id);
    }

    //Returns a list of all Transfers with a receiver or sender account ID matching the current user ID.
    @PreAuthorize("hasRole('ROLE_USER')")
    @RequestMapping(path = "transfers/all", method = RequestMethod.GET)
    public List<Transfer> getAllTransfers(Principal principal) {
        List<Transfer> transfers = new ArrayList<>();
        int userId = getCurrentUserId(principal);
        transfers = transferDao.findAllTransfersByUserID(userId);
        return transfers;
    }

    @PreAuthorize("hasRole('ROLE_USER')")
    @RequestMapping(path = "/users/current_user", method = RequestMethod.GET)
    public User getCurrentUser(Principal principal) {
        int currentUserId = getCurrentUserId(principal);
        return userDao.findUserById(currentUserId);
    }


    // Helper method
    private int getCurrentUserId(Principal principal) {
        String username = principal.getName();
        return userDao.findIdByUsername(username);
    }
}
