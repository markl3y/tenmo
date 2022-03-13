package com.techelevator.tenmo.services;

import com.techelevator.tenmo.model.AuthenticatedUser;
import com.techelevator.tenmo.model.Transfer;
import com.techelevator.tenmo.model.User;
import com.techelevator.util.BasicLogger;
import org.springframework.http.*;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;

public class TransferService {

    private final String baseURL;
    private final AuthenticatedUser authenticatedUser;
    private final RestTemplate restTemplate = new RestTemplate();

    public TransferService(String url, AuthenticatedUser authenticatedUser) {
        this.baseURL = url;
        this.authenticatedUser = authenticatedUser;
    }

    public User getCurrentUser() {
        User currentUser = null;
        try {
            ResponseEntity<User> response =
                    restTemplate.exchange(baseURL + "/users/current_user",
                            HttpMethod.GET,
                            makeAuthEntity(),
                            User.class);
            currentUser = response.getBody();
        } catch (ResourceAccessException | RestClientResponseException e) {
            BasicLogger.log(e.getMessage());
        }
        return currentUser;
    }

    public BigDecimal getBalance() {
        BigDecimal balance = null;
        try {
            ResponseEntity<BigDecimal> response =
                    restTemplate.exchange(
                            baseURL + "balance",
                            HttpMethod.GET,
                            makeAuthEntity(),
                            BigDecimal.class);
            balance = response.getBody();
        } catch (ResourceAccessException | RestClientResponseException e) {
            BasicLogger.log(e.getMessage());
        }
        return balance;
    }

    public User[] getAllOtherUsers(){
        User[] users = null;
        try {
            ResponseEntity<User[]> response =
                    restTemplate.exchange(baseURL + "users", HttpMethod.GET, makeAuthEntity(), User[].class);
            users = response.getBody();
        } catch (RestClientResponseException | ResourceAccessException e) {
            BasicLogger.log(e.getMessage());
        }
        return users;
    }

    public Transfer sendBucks(Transfer transfer) {
        HttpEntity<Transfer> entity = makeTransferEntity(transfer);
        Transfer returnTransfer = null;
        try {
            returnTransfer = restTemplate.postForObject(baseURL + "transfer/send", entity, Transfer.class);
        } catch (RestClientResponseException | ResourceAccessException e) {
            BasicLogger.log(e.getMessage());
        }
        return returnTransfer;
    }

    public Transfer[] getAllTransfers() {
        Transfer[] transfers = null;
        try {
            ResponseEntity<Transfer[]> response =
                    restTemplate.exchange(baseURL + "transfers/all", HttpMethod.GET, makeAuthEntity(), Transfer[].class);
            transfers = response.getBody();
        } catch (RestClientResponseException | ResourceAccessException e) {
            BasicLogger.log(e.getMessage());
        }
        return transfers;
    }

    public Transfer getTransfer(int transferId) {
        Transfer transfer = null;
        try {
            ResponseEntity<Transfer> response =
                    restTemplate.exchange(baseURL + "transfers/" + transferId, HttpMethod.GET, makeAuthEntity(), Transfer.class);
            transfer = response.getBody();
        } catch (RestClientResponseException | ResourceAccessException e) {
            BasicLogger.log(e.getMessage());
        }
        return transfer;
    }

    private HttpEntity<Transfer> makeTransferEntity(Transfer transfer) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(authenticatedUser.getToken());
        return new HttpEntity<>(transfer, headers);
    }

    /**
     * Returns an HttpEntity with the `Authorization: Bearer:` header
     */
    private HttpEntity<Void> makeAuthEntity() {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(authenticatedUser.getToken());
        return new HttpEntity<>(headers);
    }
}
