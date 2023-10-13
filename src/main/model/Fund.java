package model;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/*
 * Represents an ETF available for trading.
 * Maintains a price history and amount of position held by the account.
 */
public class Fund {
    private final Random random = new Random(1);

    public static final int UPDATE_INTERVAL = 15;        // History update interval in seconds. At least 2.
    public static final int DAYS_PER_INTERVAL = 1;       // History days past per interval
    public static final int TRADING_DAYS_PER_INTERVAL = 252; // Trading days per year.
    public static final double ASK_SPREAD = 0.01;         // Difference of ask price and mean in dollars
    public static final double BID_SPREAD = 0.01;         // Difference of mean and bid price in dollars

    private final String tickerSymbol;          // Ticker symbol that represents fund
    protected final double yearlyReturn;        // Average annual percent return of the fund
    private final double volatility;            // Annual standard deviation of the percent return

    private final List<Double> history;         // Price history stored in this list
    private Instant lastHistoryUpdate;          // Time of last history update
    private int fundPosition;                   // Number uf funds account owns

    /*
     * REQUIRES: ticker.length() > 0
     * EFFECTS: A representation of an ETF is created with the input parameters,
     *          ticker, initial price, yearly return and volatility are set
     */
    public Fund(String ticker, double initialPrice, double yearlyReturn, double volatility) {
        this.tickerSymbol = ticker;
        this.yearlyReturn = yearlyReturn;
        this.volatility = volatility;
        history = new ArrayList<>();
        history.add(initialPrice);
        lastHistoryUpdate = Instant.now();
    }

    /*
     * REQUIRES: ticker.length() > 0
     * EFFECTS: A representation of an ETF is created with the input parameters,
     *          ticker, initial price, yearly return and volatility are set, last history
     *          update is set to provided Instant now.
     */
    protected Fund(String ticker, double initialPrice, double yearlyReturn, double volatility, Instant now) {
        this(ticker, initialPrice, yearlyReturn, volatility);
        lastHistoryUpdate = now;
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
     * EFFECTS: If tradingDaysPassed() are greater than 0,
     *          then a history is generated using a random number
     *          for total days passed. Updates the last history
     *          update for future reference.
     */
    private void updateHistory() {
        for (int i = tradingDaysPassed() - 1; i >= 0; i--) {
            history.add(lastPrice() * randomReturn());
            lastHistoryUpdate = now();
        }
    }

    /*
     * EFFECTS: Returns Instant.now() Function for mocking purposes.
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
     * EFFECTS: Generate a random return using stander deviation
     *          and percent return per day.
     */
    protected double randomReturn() {
        return random.nextGaussian() * stdDevPerDay() // Adjust standard normal
                + returnPerDay();                     // distribution to desired normal dist.
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

    public int getFundPosition() {
        return fundPosition;
    }

    public void setFundPosition(int fundPosition) {
        this.fundPosition = fundPosition;
    }

    public String getTicker() {
        return tickerSymbol;
    }

    public int getPosition() {
        return fundPosition;
    }
}