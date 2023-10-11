package model;

import model.exception.InsufficientBalanceException;
import model.exception.InsufficientFundsException;

// Represents an account having owner name, balance (in dollars), and portfolio of funds.
public class Account {
    private final String name;             // the account owner name
    private double balance;                // the current balance of the account
    private int fundPosition;              // the total number of funds owned by the owner
    private final Fund exchangeTradedFund; // the fund that is allowed to be traded in this account

    /*
     * REQUIRES: accountName.length() > 0, fund not null
     * EFFECTS: name of account is set to accountName;
     *          if initialBalance >= 0 then balance on account is set to
     *          initialBalance, else balance is set to zero.
     */
    public Account(String accountName, double initialBalance, Fund etf) {
        name = accountName;
        this.exchangeTradedFund = etf;
        if (initialBalance >= 0) {
            balance = initialBalance;
        } else {
            balance = 0;
        }
    }

    public String getName() {
        return name;
    }

    public double getBalance() {
        return balance;
    }

    public double getFundPosition() {
        return fundPosition;
    }

    /*
     * MODIFIES: this
     * EFFECTS: if the ask price of the fund multiplied by
     *          order amount is greater than account balance,
     *          then balance is reduced by order times ask price
     *          of the fund and order is added to the position, otherwise
     *          InsufficientBalanceException is thrown.
     */
    public void buyFundAtAskPrice(int order) throws InsufficientBalanceException {
        double orderAmount = order * exchangeTradedFund.getAskPrice();
        if (orderAmount > balance) {
            throw new InsufficientBalanceException();
        }
        fundPosition += order;
        balance -= orderAmount;
    }

    /*
     * MODIFIES: this
     * EFFECTS: if the order amount is greater than funds owned in the
     *          account an InsufficientFundException is thrown, otherwise
     *          balance is increased by order times bid price of the fund
     *          and order is subtracted from the position
     */
    public void sellFundAtBidPrice(int order) throws InsufficientFundsException {
        if (order > fundPosition) {
            throw new InsufficientFundsException();
        }
        fundPosition -= order;
        balance += order * exchangeTradedFund.getBidPrice();
    }

    /*
     * EFFECTS: returns a string representation of account
     */
    @Override
    public String toString() {
        String balanceStr = String.format("%.2f", balance);
        return "[ name = " + name + ", cash = $" + balanceStr
                + " total " + exchangeTradedFund.getTicker() + "position: "
                + fundPosition + "]";
    }
}