package com.techelevator.tenmo.services;

import com.techelevator.tenmo.model.AuthenticatedUser;
import com.techelevator.tenmo.model.Transfer;
import com.techelevator.util.BasicLogger;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
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

    public Transfer sendMoney(String receiverUsername, BigDecimal amount) {
        Transfer returnTransfer = null;
        return returnTransfer;
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
