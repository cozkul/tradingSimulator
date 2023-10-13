package model;

import model.exception.InsufficientBalanceException;
import model.exception.InsufficientFundsException;

import java.util.ArrayList;
import java.util.List;

/*
 * Represents an account having owner name, balance (in dollars), and portfolio of funds.
 * Maintains a list of funds, and other account details
 */
public class Account {
    private final String name;             // the account owner name
    private double balance;                // the current balance of the account
    private final List<Fund> funds;        // the ETFs allowed to be traded in this account

    /*
     * REQUIRES: accountName.length() > 0, fund not null, initialBalance > 0
     * EFFECTS: name of account is set to accountName, initial balance is
     *          set to initialBalance. A list of funds are initiated and firstFund
     *          is added into this list.
     */
    public Account(String accountName, double initialBalance, Fund firstFund) {
        name = accountName;
        funds = new ArrayList<>();
        funds.add(firstFund);
        balance = initialBalance;
    }

    /*
     * REQUIRES: fund not null
     * MODIFIES: this
     * EFFECTS: Adds the input fund to the account if the ticker
     *          is unique for the provided fund.
     */
    public void addFund(Fund fund) {
        if (findFund(fund.getTicker()) == null) {
            funds.add(fund);
        }
    }

    /*
     * REQUIRES: fund not null, order > 0
     * MODIFIES: this
     * EFFECTS: if the ask price of the fund multiplied by
     *          order amount is greater than account balance,
     *          then balance is reduced by order times ask price
     *          of the fund and order is added to the position, otherwise
     *          InsufficientBalanceException is thrown.
     */
    public void buyFundAtAskPrice(int order, Fund fund) throws InsufficientBalanceException {
        double orderAmount = order * fund.getAskPrice();
        if (orderAmount > balance) {
            throw new InsufficientBalanceException();
        }
        fund.setFundPosition(fund.getFundPosition() + order);
        balance -= orderAmount;
    }

    /*
     * REQUIRES: fund not null, order > 0
     * MODIFIES: this
     * EFFECTS: if the order amount is greater than funds owned in the
     *          account an InsufficientFundException is thrown, otherwise
     *          balance is increased by order times bid price of the fund
     *          and order is subtracted from the position of the fund
     */
    public void sellFundAtBidPrice(int order, Fund fund) throws InsufficientFundsException {
        if (order > fund.getFundPosition()) {
            throw new InsufficientFundsException();
        }
        fund.setFundPosition(fund.getFundPosition() - order);
        balance += order * fund.getBidPrice();
    }

    /*
     * REQUIRES: ticker not null
     * EFFECTS: Searches the list of funds for the given ticker
     *          returns a reference if it can find the fund, returns
     *          null if it cannot.
     */
    public Fund findFund(String ticker) {
        Fund ans = null;
        for (Fund fund : funds) {
            if (fund.getTicker().equals(ticker)) {
                ans = fund;
                break;
            }
        }
        return ans;
    }


    /*
     * EFFECTS: returns a string representation of account
     */
    @Override
    public String toString() {
        StringBuilder ret = new StringBuilder(String.format("[ name: %s, cash: $%.2f, ", name, balance));
        for (int i = 0; i < funds.size(); i++) {
            Fund fund = funds.get(i);
            ret.append(fund.getTicker());
            ret.append(" position: ");
            ret.append(fund.getFundPosition());
            ret.append((i == funds.size() - 1) ? " ]" : ", ");
        }
        return ret.toString();
    }

    public List<Fund> getFunds() {
        return funds;
    }

    public double getBalance() {
        return balance;
    }
}