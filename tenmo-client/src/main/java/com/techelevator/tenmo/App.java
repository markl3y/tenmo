package com.techelevator.tenmo;

import com.techelevator.tenmo.model.AuthenticatedUser;
import com.techelevator.tenmo.model.Transfer;
import com.techelevator.tenmo.model.User;
import com.techelevator.tenmo.model.UserCredentials;
import com.techelevator.tenmo.services.AuthenticationService;
import com.techelevator.tenmo.services.ConsoleService;
import com.techelevator.tenmo.services.TransferService;

import java.text.NumberFormat;

public class App {

    private static final String API_BASE_URL = "http://localhost:8080/";

    private final ConsoleService consoleService = new ConsoleService();
    private final AuthenticationService authenticationService = new AuthenticationService(API_BASE_URL);
    private TransferService transferService;

    private AuthenticatedUser currentUser;

    public static void main(String[] args) {
        App app = new App();
        app.run();
    }

    private void run() {
        consoleService.printGreeting();
        loginMenu();
        if (currentUser != null) {
            mainMenu();
        }
    }
    private void loginMenu() {
        int menuSelection = -1;
        while (menuSelection != 0 && currentUser == null) {
            consoleService.printLoginMenu();
            menuSelection = consoleService.promptForMenuSelection("Please choose an option: ");
            if (menuSelection == 1) {
                handleRegister();
            } else if (menuSelection == 2) {
                handleLogin();
            } else if (menuSelection != 0) {
                System.out.println("Invalid Selection");
                consoleService.pause();
            }
        }
    }

    private void handleRegister() {
        System.out.println("Please register a new user account");
        UserCredentials credentials = consoleService.promptForCredentials();
        if (authenticationService.register(credentials)) {
            System.out.println("Registration successful. You can now login.");
        } else {
            consoleService.printErrorMessage();
        }
    }

    private void handleLogin() {
        UserCredentials credentials = consoleService.promptForCredentials();
        currentUser = authenticationService.login(credentials);
        if (currentUser == null) {
            consoleService.printErrorMessage();
        }
    }

    private void mainMenu() {
        transferService = new TransferService(API_BASE_URL, currentUser);
        int menuSelection = -1;
        while (menuSelection != 0) {
            consoleService.printMainMenu();
            menuSelection = consoleService.promptForMenuSelection("Please choose an option: ");
            if (menuSelection == 1) {
                viewCurrentBalance();
            } else if (menuSelection == 2) {
                viewTransferHistory();
            } else if (menuSelection == 3) {
                viewPendingRequests();
            } else if (menuSelection == 4) {
                sendBucks();
            } else if (menuSelection == 5) {
                requestBucks();
            } else if (menuSelection == 0) {
                continue;
            } else {
                System.out.println("Invalid Selection");
            }
            consoleService.pause();
        }
    }

	private void viewCurrentBalance() {
		// TODO Auto-generated method stub
        if (transferService.getBalance() != null) {
            System.out.println("\nYour current account balance is: " + NumberFormat.getCurrencyInstance().format(transferService.getBalance()));
        } else {
            consoleService.printErrorMessage();
        }
	}

	private void viewTransferHistory() {
		// TODO Auto-generated method stub
        User currentUser = transferService.getCurrentUser();
        String currentUserName = currentUser.getUsername();
        User[] users = transferService.getAllOtherUsers();
        Transfer[] transfers = transferService.getAllTransfers();
		consoleService.printTransfers(transfers, users, currentUserName);
	}

	private void viewPendingRequests() {
		// TODO Auto-generated method stub
		
	}

	private void sendBucks() {
		// TODO Auto-generated method stub
        User[] users = transferService.getAllOtherUsers();
        consoleService.printOtherUsers(users);
        Transfer transferEnteredByUser = consoleService.promptForSendTransferData(users);
        if (transferEnteredByUser != null) {
            User user = transferService.getCurrentUser();
            int userId = Integer.parseInt(user.getId().toString());
            transferEnteredByUser.setSenderUserId(userId);
            Transfer transferFromAPI = transferService.sendBucks(transferEnteredByUser);
            if (transferFromAPI == null) {
                System.out.println("Send unsuccessful.");
            } else System.out.println("Send successful!");
        }
	}

	private void requestBucks() {
		// TODO Auto-generated method stub
		
	}

}
