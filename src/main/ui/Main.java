package ui;

import model.Account;
import model.Fund;

import java.util.Scanner;

public class Main {
    static Scanner scanner = new Scanner(System.in);

    static Fund exchangeTradedFund;
    static Account account;

    public static void main(String[] args) {
        initialize();
        while (true) {
            displayOptions();
            String action = scanner.nextLine();
            if (action.equals("exit")) {
                break;
            }
            processActions(action);
        }
    }

    private static void processActions(String action) {
        switch (action) {
            case "acc":
                printAccountSummary();
                break;
            case "quote":
                printQuote();
                break;
            case "hist":
                printHistory();
                break;
            case "buy":
                executeBuy();
                break;
            case "sell":
                executeSell();
                break;
            default:
                System.out.println("Invalid input. Please try again.");
                break;
        }
    }

    private static void executeSell() {
        // stub
    }

    private static void executeBuy() {
        // stub
    }

    private static void printHistory() {
        System.out.println("Price history for " + exchangeTradedFund.getTicker() + ":");
        System.out.println(exchangeTradedFund.getHistory());
    }

    private static void printQuote() {
        System.out.println("Quote for " + exchangeTradedFund.getTicker() + ":");
        String bidPrice = String.format("%.2f", exchangeTradedFund.getBidPrice());
        String askPrice = String.format("%.2f", exchangeTradedFund.getAskPrice());

        System.out.println("Current bid price: $" + bidPrice
                + ", balance = $" + askPrice + "]");
    }

    private static void printAccountSummary() {
        System.out.println("Your Account Summary is below:");
        System.out.println(account);
    }

    private static void displayOptions() {
        System.out.println("Please select an item from the list below by entering the action number:");
        System.out.println("(acc)  : View Account Summary");
        System.out.println("(quote): Get a Quote for Current Bid and Ask Price");
        System.out.println("(hist) : Get Past Market Prices for SP500");
        System.out.println("(buy)  : Buy SP500 At Current Ask Price");
        System.out.println("(sell) : Sell SP500 At Current Bid Price");
        System.out.println("(exit) : Exit");
    }

    private static void initialize() {
        System.out.println("Welcome to SP500 Trading Simulator");
        System.out.println("You can simulate buying and selling an SP500 ETF without real life consequences.");
        fundInit();
        accountInit();
    }

    private static void fundInit() {
        exchangeTradedFund = new Fund("SP500");
    }

    private static void accountInit() {
        System.out.print("Please enter your name: ");
        String name = scanner.nextLine();
        System.out.print("Please enter initial balance in USD. "
                + "Balance must be greater than $400: ");
        int initialBalance = Integer.parseInt(scanner.nextLine());
        while (initialBalance < 400) {
            System.out.print("Balance must be greater than $400. Please try again: ");
            initialBalance = Integer.parseInt(scanner.nextLine());
        }
        account = new Account(name, initialBalance, exchangeTradedFund);
        System.out.println("Your account has been successfully created.");
        System.out.printf("You are only authorized to trade %s.\n", exchangeTradedFund.getTicker());
    }

}
