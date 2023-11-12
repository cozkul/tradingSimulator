package ui;

import model.Account;
import model.Security;
import model.exception.InsufficientBalanceException;
import model.exception.InsufficientFundsException;
import persistence.JsonReader;
import persistence.JsonWriter;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import java.util.Scanner;

import static java.lang.System.exit;

/*
 * Represents the Simulate App. Maintains an account.
 */
public class TradingSimulatorCLI {
    private static final String JSON_STORE = "./data/user.json";
    private final JsonWriter jsonWriter;
    private final JsonReader jsonReader;

    private final Scanner scanner;
    private Account account;

   /*
    * EFFECTS: Constructs ETF simulator and runs the application.
    */
    public TradingSimulatorCLI() {
        scanner = new Scanner(System.in);
        jsonWriter = new JsonWriter(JSON_STORE);
        jsonReader = new JsonReader(JSON_STORE);
        run();
    }

    /*
     * EFFECTS: Presents user setup and menu, prompts for
     *          user input until user commands exit.
     */
    public void run() {
        initialize();
        while (true) {
            printMenu();
            String action = scanner.nextLine();
            if (action.contains("new")) {
                action = "";
            }
            processActions(action);
        }
    }

    /*
     * EFFECTS: Dispatches user input to appropriate function.
     */
    @SuppressWarnings("methodlength")
    private void processActions(String action) {
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
            case "add":
                executeAddFund();
                break;
            case "list":
                printAllFunds();
                break;
            case "save":
                saveState();
                break;
            case "load":
                loadState();
                break;
            case "new":
                createAccount();
                break;
            case "exit":
                exit(0);
            default:
                System.out.println("Invalid input. Please try again.");
                break;
        }
    }

    // MODIFIES: this
    // EFFECTS: loads account from file
    private void loadState() {
        try {
            account = jsonReader.read();
            System.out.println("Loaded " + account.getName() + " from " + JSON_STORE);
        } catch (IOException e) {
            System.out.println("Unable to read from file: " + JSON_STORE);
        }
    }

    // EFFECTS: saves the account to file
    private void saveState() {
        try {
            jsonWriter.open();
            jsonWriter.write(account);
            jsonWriter.close();
            System.out.println("Saved " + account.getName() + " to " + JSON_STORE);
        } catch (FileNotFoundException e) {
            System.out.println("Unable to write to file: " + JSON_STORE);
        }
    }

    /*
     * REQUIRES: Clean input from user. Strings when prompted
     *           for ticker, double when prompted for numbers.
     * MODIFIES: account
     * EFFECTS: Presents inputs for ETF creation. Creates an ETF and calls
     *          account.addFund(...) with the inputs from the user.
     */
    private void executeAddFund() {
        System.out.print("Please enter ticker for ETF: ");
        String ticker = scanner.nextLine();

        System.out.print("Please enter initial price for ETF in USD: ");
        double price = Double.parseDouble(scanner.nextLine());

        System.out.print("Please enter expected annual return (0<=r<=0.20): ");
        double annualReturn = Double.parseDouble(scanner.nextLine());

        System.out.print("Please enter annual standard deviation for this ETF (0<=sd<=0.5): ");
        double std = Double.parseDouble(scanner.nextLine());

        Security security = new Security(ticker, price, annualReturn, std);
        account.addFund(security);
        System.out.println("Ticker successfully created.");
    }

    /*
     * EFFECTS: Presents available tickers for the account.
     */
    private void printAllFunds() {
        System.out.println("This account is authorized to trade:");
        List<Security> securities = account.getFunds();
        StringBuilder ret = new StringBuilder();
        for (int i = 0; i < securities.size(); i++) {
            ret.append(securities.get(i).getTicker());
            ret.append((i == securities.size() - 1) ? "" : ", ");
        }
        System.out.println(ret);
    }

    /*
     * REQUIRES: Clean input from user. Strings when prompted
     *           for ticker, integer when prompted for number.
     * MODIFIES: account
     * EFFECTS: Present input for sell order creation. Calls
     *          account.sellFundAtBidPrice(...) with the inputs.
     *          Notifies the user if the order was successfully
     *          executed or not.
     */
    private void executeSell() {
        Security security = inputFund();

        System.out.print("Please enter order amount: ");
        int order = Integer.parseInt(scanner.nextLine());

        try {
            account.sellFundAtBidPrice(order, security);
            System.out.print("Order successfully executed. ");
        } catch (InsufficientFundsException e) {
            System.out.print("You do not have enough of this position to sell.");
        }

        printAccountSummary();
    }

    /*
     * REQUIRES: Clean input from user. Strings when prompted
     *           for ticker, integer when prompted for number.
     * MODIFIES: account
     * EFFECTS: Present input for buy order creation. Calls
     *          account.sellFundAtBidPrice(...) with the inputs.
     *          Notifies the user if the order was successfully
     *          executed or not.
     */
    private void executeBuy() {
        Security security = inputFund();

        System.out.print("Please enter order amount: ");
        int order = Integer.parseInt(scanner.nextLine());

        try {
            account.buyFundAtAskPrice(order, security);
            System.out.print("Order successfully executed.");
        } catch (InsufficientBalanceException e) {
            System.out.print("You do not have enough cash. ");
        }

        printAccountSummary();
    }

    /*
     * EFFECTS: Requests input for a ticker and displays
     *          price history for the ticker.
     */
    private void printHistory() {
        Security security = inputFund();
        System.out.println("Price history for " + security.getTicker() + ":");
        System.out.println(security.getHistory());
        System.out.println("Each item in list represents a day.");
    }

    /*
     * EFFECTS: Requests input for a ticker and displays
     *          price current quote for the ticker.
     */
    private void printQuote() {
        Security security = inputFund();
        System.out.println("Quote for " + security.getTicker() + ":");
        String bidPrice = String.format("%.2f", security.getBidPrice());
        String askPrice = String.format("%.2f", security.getAskPrice());

        System.out.println("Current bid price: $" + bidPrice
                + ", ask price: $" + askPrice);
    }

    /*
     * EFFECTS: Prints account summary.
     */
    private void printAccountSummary() {
        System.out.println("Your Account Summary is below:");
        System.out.println(account);
    }

    /*
     * EFFECTS: Available options for the user.
     */
    private void printMenu() {
        System.out.println("\nPlease select an item from the list below by entering the action:");
        System.out.println("(acc)  : View Account Summary");
        System.out.println("(quote): Get a Quote for Current Bid and Ask Price of an ETF");
        System.out.println("(hist) : Get Past Market Prices for of an ETF");
        System.out.println("(buy)  : Buy ETF At Current Ask Price");
        System.out.println("(sell) : Sell ETF At Current Bid Price");
        System.out.println("(add)  : Add an ETF to the simulation");
        System.out.println("(list) : List all ETFs that the account is authorized to trade");
        System.out.println("(load) : Load from a previous save file");
        System.out.println("(save) : Save current state to a file");
        System.out.println("(exit) : Exit");
    }

    /*
     * EFFECTS: Provides user options for creating an account or
     *          load data.
     */
    private void initialize() {
        System.out.println("Welcome to ETF Trading Simulator");
        System.out.println("You can simulate buying and selling an ETF without real life consequences.\n");
        while (account == null) {
            System.out.println("Please select to create a new account or load a previous save:");
            System.out.println("(load) : Load from a previous save file");
            System.out.println("(new)  : Create a new account");
            System.out.println("(exit) : Exit");
            String action = scanner.nextLine();
            if (!(action.contains("new") || action.contains("load") || action.contains("exit"))) {
                action = "";
            }
            processActions(action);
        }
    }

    /*
     * REQUIRES: Clean input from user. Strings when prompted
     *           for name, double when prompted for number.
     * MODIFIES: this, account
     * EFFECTS: Prompts user for account information input, name
     *          and initial balance. Creates an account for the user.
     *          Sets up an initial ETF SP500 available to be traded and
     *          adds this to the account.
     */
    private void createAccount() {
        System.out.print("Please enter your name: ");
        String name = scanner.nextLine();
        System.out.print("Please enter initial balance in USD. "
                + "Balance must be greater than $500: ");
        int initialBalance = Integer.parseInt(scanner.nextLine());
        while (initialBalance < 500) {
            System.out.print("Balance must be greater than $400. Please try again: ");
            initialBalance = Integer.parseInt(scanner.nextLine());
        }
        Security security = new Security("SP500", 400, 0.07, .20);
        account = new Account(name, initialBalance, security);
        System.out.println("Your account has been successfully created.");
        System.out.printf("You are only authorized to trade %s.\n", security.getTicker());
    }

    /*
     * REQUIRES: Clean input from user. Strings when prompted
     *           for ticker.
     * EFFECTS: Prompts user for a ticker. Checks if the ticker
     *          is available and re-prompts until a valid ticker
     *          is entered. If the input is invalid valid options are
     *          presented using printAllFunds().
     */
    private Security inputFund() {
        Security security = null;
        while (security == null) {
            System.out.print("Please enter the ticker you're interested: ");
            String name = scanner.nextLine();
            security = account.findFund(name);
            if (security == null) {
                System.out.println("Invalid input.");
                printAllFunds();
            }
        }
        return security;
    }
}
