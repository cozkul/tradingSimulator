package model;

import model.exception.InsufficientBalanceException;
import model.exception.InsufficientFundsException;
import org.json.JSONArray;
import org.json.JSONObject;
import persistence.Writable;

import java.util.ArrayList;
import java.util.List;

/*
 * Represents an account having owner name, balance (in dollars), and portfolio of securities.
 * Maintains a list of securities, and other account details
 */
public class Account implements Writable {
    private final String name;             // the account owner name
    private double balance;                // the current balance of the account
    private final List<Security> securities;        // the ETFs allowed to be traded in this account

    /*
     * REQUIRES: accountName.length() > 0, fund not null, initialBalance > 0
     * EFFECTS: name of account is set to accountName, initial balance is
     *          set to initialBalance. A list of securities are initiated and firstSecurity
     *          is added into this list.
     */
    public Account(String accountName, double initialBalance, Security firstSecurity) {
        name = accountName;
        securities = new ArrayList<>();
        securities.add(firstSecurity);
        balance = initialBalance;
    }

    /*
     * REQUIRES: accountName.length() > 0, securities not null, initialBalance > 0
     * EFFECTS: name of account is set to accountName, initial balance is
     *          set to initialBalance. Account starts maintaining the list securities.
     */
    public Account(String accountName, double balance, List<Security> securities) {
        this.name = accountName;
        this.securities = securities;
        this.balance = balance;
    }

    /*
     * REQUIRES: security not null
     * MODIFIES: this
     * EFFECTS: Adds the input security to the account if the ticker
     *          is unique for the provided security.
     */
    public void addFund(Security security) {
        if (findFund(security.getTicker()) == null) {
            securities.add(security);
        }
    }

    /*
     * REQUIRES: security not null, order > 0
     * MODIFIES: this
     * EFFECTS: if the ask price of the security multiplied by
     *          order amount is greater than account balance,
     *          then balance is reduced by order times ask price
     *          of the security and order is added to the position, otherwise
     *          InsufficientBalanceException is thrown.
     */
    public void buyFundAtAskPrice(int order, Security security) throws InsufficientBalanceException {
        double orderAmount = order * security.getAskPrice();
        if (orderAmount > balance) {
            throw new InsufficientBalanceException();
        }
        security.setSecurityPosition(security.getSecurityPosition() + order);
        balance -= orderAmount;
    }

    /*
     * REQUIRES: security not null, order > 0
     * MODIFIES: this
     * EFFECTS: if the order amount is greater than securities owned in the
     *          account an InsufficientFundException is thrown, otherwise
     *          balance is increased by order times bid price of the security
     *          and order is subtracted from the position of the security
     */
    public void sellFundAtBidPrice(int order, Security security) throws InsufficientFundsException {
        if (order > security.getSecurityPosition()) {
            throw new InsufficientFundsException();
        }
        security.setSecurityPosition(security.getSecurityPosition() - order);
        balance += order * security.getBidPrice();
    }

    /*
     * REQUIRES: ticker not null
     * EFFECTS: Searches the list of securities for the given ticker
     *          returns a reference if it can find the fund, returns
     *          null if it cannot.
     */
    public Security findFund(String ticker) {
        Security ans = null;
        for (Security security : securities) {
            if (security.getTicker().equals(ticker)) {
                ans = security;
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
        for (int i = 0; i < securities.size(); i++) {
            Security security = securities.get(i);
            ret.append(security.getTicker());
            ret.append(" position: ");
            ret.append(security.getSecurityPosition());
            ret.append((i == securities.size() - 1) ? " ]" : ", ");
        }
        return ret.toString();
    }

    /*
     * EFFECTS: returns this account as a JSON object
     */
    @Override
    public JSONObject toJson() {
        JSONObject json = new JSONObject();
        json.put("name", name);
        json.put("balance", balance);
        json.put("securities", fundsToJson());
        return json;
    }

    /*
     * EFFECTS: returns securities in this account as a JSON array
     */
    private JSONArray fundsToJson() {
        JSONArray jsonArray = new JSONArray();
        for (Security f : securities) {
            jsonArray.put(f.toJson());
        }
        return jsonArray;
    }

    public List<Security> getSecurities() {
        return securities;
    }

    public double getBalance() {
        return balance;
    }

    public String getName() {
        return name;
    }
}