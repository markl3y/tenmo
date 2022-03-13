package com.techelevator.tenmo.services;


import com.techelevator.tenmo.model.Transfer;
import com.techelevator.tenmo.model.User;
import com.techelevator.tenmo.model.UserCredentials;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class ConsoleService {

    private final Scanner scanner = new Scanner(System.in);

    public int promptForMenuSelection(String prompt) {
        int menuSelection;
        System.out.print(prompt);
        try {
            menuSelection = Integer.parseInt(scanner.nextLine());
        } catch (NumberFormatException e) {
            menuSelection = -1;
        }
        return menuSelection;
    }

    public void printGreeting() {
        System.out.println("*********************");
        System.out.println("* Welcome to TEnmo! *");
        System.out.println("*********************");
    }

    public void printLoginMenu() {
        System.out.println();
        System.out.println("1: Register");
        System.out.println("2: Login");
        System.out.println("0: Exit");
        System.out.println();
    }

    public void printMainMenu() {
        System.out.println();
        System.out.println("1: View your current balance");
        System.out.println("2: View your past transfers");
        System.out.println("3: View your pending requests");
        System.out.println("4: Send TE bucks");
        System.out.println("5: Request TE bucks");
        System.out.println("0: Exit");
        System.out.println();
    }

    public UserCredentials promptForCredentials() {
        String username = promptForString("Username: ");
        String password = promptForString("Password: ");
        return new UserCredentials(username, password);
    }

    public String promptForString(String prompt) {
        System.out.print(prompt);
        return scanner.nextLine();
    }

//    public int promptForInt(String prompt) { //PROVIDED
//        System.out.print(prompt);
//        while (true) {
//            try {
//                return Integer.parseInt(scanner.nextLine());
//            } catch (NumberFormatException e) {
//                System.out.println("Please enter a number.");
//            }
//        }
//    }
//
//    public BigDecimal promptForBigDecimal(String prompt) { //PROVIDED
//        System.out.print(prompt);
//        while (true) {
//            try {
//                return new BigDecimal(scanner.nextLine());
//            } catch (NumberFormatException e) {
//                System.out.println("Please enter a decimal number.");
//            }
//        }
//    }

    public Transfer promptForSendTransferData(User[] users) { //***NEW
        Transfer returnTransfer = new Transfer();
        boolean validId = false;

        //List of other user Ids.
        List<Integer> ids = new ArrayList<>();
        //For each user in the other users, add their userID to the list.
        for (User user : users) {
            ids.add(Integer.parseInt(user.getId().toString()));
        }

        //Check validity of selected userId.
        do {
            System.out.print("\nEnter ID of user you are sending to (0 to cancel): ");
            String input = scanner.nextLine();
            try {
                //Tries to parse it into integer.
                int userId = Integer.parseInt(input);

                //If it parses properly, and it is 0, it returns to main menu.
                if (userId == 0) {
                    break;
                }
                //Checks the input integer against all other userIDs.
                for (Integer id : ids) {
                    //If the selected user id is in other users, then its a valid id,
                    //and the receiver is the found users id.
                    if (userId == id) {
                        validId = true;
                        returnTransfer.setReceiverUserId(userId);
                    }
                }
                //Let the user know it's not valid input.
                if (!validId) {
                    System.out.println("\nInvalid ID...");
                }
            } catch (NumberFormatException e) { //If we can't parse...
                validId = false;
                System.out.println("\nCould not find ID: " + input);
            }
        } while (!validId);

        boolean validAmount = false;
        //Check to see if user input is valid amount.
        do {
            System.out.print("\nEnter amount: $");
            String input = scanner.nextLine();
            try {
                BigDecimal amount = new BigDecimal(input);
                if (amount.compareTo(BigDecimal.ZERO) > 0) {
                    System.out.println("\nProcessing...");
                    returnTransfer.setTransferAmount(amount);
                    returnTransfer.setTransferType("Send");
                    returnTransfer.setTransferStatus("Approved");
                    return returnTransfer;
                } else {
                    System.out.println("\nAmount must be greater than 0...");
                }
            } catch (NullPointerException | NumberFormatException e) {
                System.out.println("\nInvalid amount...");
            }
        } while (validAmount);
        return null;
    }

    public void printOtherUsers(User[] otherUsers) { //***NEW
        if (otherUsers != null) {
            System.out.println("-------------------------------------------\n" +
                    "Users\n" +
                    "ID          Name\n" +
                    "-------------------------------------------");
            for (User user : otherUsers) {
                System.out.printf("%d\t\t%s\n", user.getId(), user.getUsername());
            }
        } else {
            System.out.println("Other users not found...");
        }
    }

    public void printTransfers(Transfer[] transfers, User[] users, String currentUserName) {
        System.out.println("-------------------------------------------\n" +
                "Transfers\n" +
                "ID          From/To                 Amount\n" +
                "-------------------------------------------");
        for (Transfer transfer : transfers) {
            System.out.printf("%d\t\t", transfer.getTransferId());
            String otherUserRoleAndName = "";
            for (User user : users) {
                if (transfer.getReceiverUserId() == user.getId() || transfer.getSenderUserId() == user.getId()) {
                    if (transfer.getReceiverUserId() == user.getId()) {
                        otherUserRoleAndName = "  To: " + user.getUsername();
                    } else if (transfer.getSenderUserId() == user.getId()) {
                        otherUserRoleAndName = "From: " + user.getUsername();
                    }
                }
            }
            System.out.printf("%s\t\t\t", otherUserRoleAndName);
            System.out.printf("%s\n", NumberFormat.getCurrencyInstance().format(transfer.getTransferAmount()));
        }
        boolean isValidId = false;
        do {
            System.out.print("\nPlease enter transfer ID to view details (0 to cancel): ");
            String input = scanner.nextLine();
            try {
                int choice = Integer.parseInt(input);
                if (choice != 0) {
                    for (Transfer transfer : transfers) {
                        if (choice == transfer.getTransferId()) {
                            printTransferDetails(transfer, users, currentUserName);
                            isValidId = true;
                        }
                    }
                } else {
                    isValidId = true;
                }
            } catch (NumberFormatException e) {
                System.out.println("\nInvalid transfer ID...");
            }
        } while (!isValidId);
    }

    public void printTransferDetails(Transfer transfer, User[] users, String currentUsername) {
        String userRoleAndName = "";
        String otherUserRoleAndName = "";
        for (User user : users) {
            if (transfer.getReceiverUserId() == user.getId() || transfer.getSenderUserId() == user.getId()) {
                if (transfer.getReceiverUserId() == user.getId()) {
                    otherUserRoleAndName = "To: " + user.getUsername();
                    userRoleAndName = "From: " + currentUsername;
                } else if (transfer.getSenderUserId() == user.getId()) {
                    otherUserRoleAndName = "From: " + user.getUsername();
                    userRoleAndName = "To: " + currentUsername;
                }
            }
        }

        System.out.println("--------------------------------------------\n" +
                "Transfer Details\n" +
                "--------------------------------------------\n" +
                "Id: " + transfer.getTransferId() + "\n" +
                otherUserRoleAndName + "\n" +
                userRoleAndName + "\n" +
                "Type: " + transfer.getTransferType() + "\n" +
                "Status: " + transfer.getTransferStatus() + "\n" +
                "Amount: " + NumberFormat.getCurrencyInstance().format(transfer.getTransferAmount()));
    }


    public void pause() {
        System.out.println("\nPress Enter to continue...");
        scanner.nextLine();
    }

    public void printErrorMessage() {
        System.out.println("\nAn error occurred. Check the log for details.");
    }
}
