package model;

import org.json.JSONArray;
import org.json.JSONObject;
import persistence.Writable;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/*
 * Represents an ETF available for trading.
 * Maintains a price history and amount of position held by the account.
 */
public class Security implements Writable {
    private final Random random = new Random();

    public static final int UPDATE_INTERVAL = 15;        // History update interval in seconds. At least 2.
    public static final int DAYS_PER_INTERVAL = 1;       // History days past per interval
    public static final int TRADING_DAYS_PER_INTERVAL = 252; // Trading days per year.
    public static final double ASK_SPREAD = 0.01;         // Difference of ask price and mean in dollars
    public static final double BID_SPREAD = 0.01;         // Difference of mean and bid price in dollars

    private final String tickerSymbol;          // Ticker symbol that represents security

    protected final double yearlyReturn;        // Average annual percent return of the security
    private final double volatility;            // Annual standard deviation of the percent return

    private final List<Double> history;         // Price history stored in this list
    protected Instant lastHistoryUpdate;        // Time of last history update
    private int securityPosition;               // Number of securities account owns

    /*
     * REQUIRES: ticker.length() > 0, initialPrice > 0, yearlyReturn > 0, volatility > 0
     * EFFECTS: A representation of an ETF is created with the input parameters,
     *          ticker, initial price, yearly return and volatility are set
     */
    public Security(String ticker, double initialPrice, double yearlyReturn, double volatility) {
        this.tickerSymbol = ticker;
        this.yearlyReturn = yearlyReturn;
        this.volatility = volatility;
        this.history = new ArrayList<>();
        history.add(initialPrice);
        this.lastHistoryUpdate = now();
    }

    /*
     * REQUIRES: ticker.length() > 0, initialPrice > 0, yearlyReturn > 0, volatility > 0,
     *           history not null, lastUpdate not null, securityPosition > 0
     * EFFECTS: A representation of an ETF is created with the input parameters,
     *          ticker, initial price, yearly return, volatility, securityPosition and lastUpdate.
     *          The fund starts maintaining the history provided.
     */
    public Security(String ticker, double yearlyReturn, double volatility, List<Double> history,
                    Instant lastUpdate, int securityPosition) {
        this.tickerSymbol = ticker;
        this.yearlyReturn = yearlyReturn;
        this.volatility = volatility;
        this.history = history;
        this.lastHistoryUpdate = lastUpdate;
        this.securityPosition = securityPosition;
    }


    /*
     * MODIFIES: this
     * EFFECTS: Updates price history if needed and returns ask price.
     */
    public double getAskPrice() {
        updateHistory();
        return lastPrice() + ASK_SPREAD;
    }

    /*
     * MODIFIES: this
     * EFFECTS: Updates price history if needed and returns bid price.
     */
    public double getBidPrice() {
        updateHistory();
        return lastPrice() - BID_SPREAD;
    }

    /*
     * MODIFIES: this
     * EFFECTS: Updates price history if needed and returns history
     */
    public List<Double> getHistory() {
        updateHistory();
        return history;
    }

    /*
     * EFFECTS: Returns the last price stored in history to caller.
     */
    private double lastPrice() {
        return history.get(history.size() - 1);
    }

    /*
     * MODIFIES: this
     * EFFECTS: If real time passed is greater than set UPDATE_INTERVAL,
     *          then a history is generated using a random return
     *          for total days passed. Stores the time of the last history
     *          update for future reference.
     */
    public void updateHistory() {
        for (int i = tradingDaysPassed() - 1; i >= 0; i--) {
            history.add(lastPrice() * randomReturn());
            lastHistoryUpdate = now();
        }
    }

    /*
     * EFFECTS: Returns Instant.now(). Function for mocking purposes.
     */
    protected Instant now() {
        return Instant.now();
    }

    /*
     * EFFECTS: Calculate trading days passed. This function
     *          converts real time passed to time in simulation.
     */
    private int tradingDaysPassed() {
        Duration elapsed = Duration.between(lastHistoryUpdate, now());
        Duration chunk = Duration.ofSeconds(UPDATE_INTERVAL);
        return Math.toIntExact(elapsed.dividedBy(chunk))
                * DAYS_PER_INTERVAL;
    }

    /*
     * EFFECTS: Generate a random return using standard deviation
     *          and percent return per day.
     */
    protected double randomReturn() {
        // Adjust standard normal distribution to desired normal dist.
        return random.nextGaussian() * stdDevPerDay() + returnPerDay();
    }

    /*
     * EFFECTS: Calculate daily return from annual return.
     */
    protected double returnPerDay() {
        return Math.pow(1.0 + yearlyReturn, (1.0 / TRADING_DAYS_PER_INTERVAL)); // 1.0 ensures double conversion
    }

    /*
     * EFFECTS: Calculate daily standard deviation from annual standard deviation.
     */
    private double stdDevPerDay() {
        return volatility / Math.sqrt(TRADING_DAYS_PER_INTERVAL);
    }

    /*
     * EFFECTS: returns this Security as a JSON object
     */
    @Override
    public JSONObject toJson() {
        JSONObject json = new JSONObject();
        json.put("ticker", tickerSymbol);
        json.put("yearlyReturn", yearlyReturn);
        json.put("volatility", volatility);
        json.put("history", historyToJson());
        json.put("lastUpdate", lastHistoryUpdate.toString());
        json.put("securityPosition", securityPosition);
        return json;
    }

    /*
     * EFFECTS: returns the history as a JSON array
     */
    private JSONArray historyToJson() {
        JSONArray jsonArray = new JSONArray();
        for (Double d : history) {
            jsonArray.put(d);
        }
        return jsonArray;
    }

    public int getSecurityPosition() {
        return securityPosition;
    }

    public void setSecurityPosition(int securityPosition) {
        this.securityPosition = securityPosition;
    }

    public String getTicker() {
        return tickerSymbol;
    }

    public int getPosition() {
        return securityPosition;
    }

    public double getYearlyReturn() {
        return yearlyReturn;
    }

    public double getVolatility() {
        return volatility;
    }

    public Instant getLastHistoryUpdate() {
        return lastHistoryUpdate;
    }
}