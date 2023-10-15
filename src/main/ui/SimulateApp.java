package ui;

import model.Account;
import model.Fund;
import model.exception.InsufficientBalanceException;
import model.exception.InsufficientFundsException;

import java.util.List;
import java.util.Scanner;

/*
 * Represents the Simulate App. Maintains an account.
 */
public class SimulateApp {
    private final Scanner scanner;
    private Account account;

   /*
    * EFFECTS: Constructs ETF simulator and runs the application.
    */
    public SimulateApp() {
        scanner = new Scanner(System.in);
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
            if (action.equals("exit")) {
                break;
            }
            processActions(action);
        }
    }

    /*
     * EFFECTS: Dispatches user input to appropriate function.
     */
    private void processActions(String action) {
        // Not using switch only for function length requirements
        if (action.equals("acc")) {
            printAccountSummary();
        } else if (action.equals("quote")) {
            printQuote();
        } else if (action.equals("hist")) {
            printHistory();
        } else if (action.equals("buy")) {
            executeBuy();
        } else if (action.equals("sell")) {
            executeSell();
        } else if (action.equals("add")) {
            executeAddFund();
        } else if (action.equals("list")) {
            printAllFunds();
        } else if (action.equals("save")) {
            saveState();
        } else if (action.equals("load")) {
            loadState();
        } else {
            System.out.println("Invalid input. Please try again.");
        }
    }

    private void loadState() {
        // STUB
    }

    private void saveState() {
        // STUB
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

        Fund fund = new Fund(ticker, price, annualReturn, std);
        account.addFund(fund);
        System.out.println("Ticker successfully created.");
    }

    /*
     * EFFECTS: Presents available tickers for the account.
     */
    private void printAllFunds() {
        System.out.println("This account is authorized to trade:");
        List<Fund> funds = account.getFunds();
        StringBuilder ret = new StringBuilder();
        for (int i = 0; i < funds.size(); i++) {
            ret.append(funds.get(i).getTicker());
            ret.append((i == funds.size() - 1) ? "" : ", ");
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
        Fund fund = inputFund();

        System.out.print("Please enter order amount: ");
        int order = Integer.parseInt(scanner.nextLine());

        try {
            account.sellFundAtBidPrice(order, fund);
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
        Fund fund = inputFund();

        System.out.print("Please enter order amount: ");
        int order = Integer.parseInt(scanner.nextLine());

        try {
            account.buyFundAtAskPrice(order, fund);
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
        Fund fund = inputFund();
        System.out.println("Price history for " + fund.getTicker() + ":");
        System.out.println(fund.getHistory());
        System.out.println("Each item in list represents a day.");
    }

    /*
     * EFFECTS: Requests input for a ticker and displays
     *          price current quote for the ticker.
     */
    private void printQuote() {
        Fund fund = inputFund();
        System.out.println("Quote for " + fund.getTicker() + ":");
        String bidPrice = String.format("%.2f", fund.getBidPrice());
        String askPrice = String.format("%.2f", fund.getAskPrice());

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
        System.out.println("(exit) : Exit");
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
    private void initialize() {
        System.out.println("Welcome to ETF Trading Simulator");
        System.out.println("You can simulate buying and selling an ETF without real life consequences.");
        System.out.print("Please enter your name: ");
        String name = scanner.nextLine();
        System.out.print("Please enter initial balance in USD. "
                + "Balance must be greater than $500: ");
        int initialBalance = Integer.parseInt(scanner.nextLine());
        while (initialBalance < 500) {
            System.out.print("Balance must be greater than $400. Please try again: ");
            initialBalance = Integer.parseInt(scanner.nextLine());
        }
        Fund fund = new Fund("SP500", 400, 0.07, .20);
        account = new Account(name, initialBalance, fund);
        System.out.println("Your account has been successfully created.");
        System.out.printf("You are only authorized to trade %s.\n", fund.getTicker());
    }

    /*
     * REQUIRES: Clean input from user. Strings when prompted
     *           for ticker.
     * EFFECTS: Prompts user for a ticker. Checks if the ticker
     *          is available and re-prompts until a valid ticker
     *          is entered. If the input is invalid valid options are
     *          presented using printAllFunds().
     */
    private Fund inputFund() {
        Fund fund = null;
        while (fund == null) {
            System.out.print("Please enter the ticker you're interested: ");
            String name = scanner.nextLine();
            fund = account.findFund(name);
            if (fund == null) {
                System.out.println("Invalid input.");
                printAllFunds();
            }
        }
        return fund;
    }
}
