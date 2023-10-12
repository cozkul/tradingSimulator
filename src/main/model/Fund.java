package model;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Fund {
    private final Random random = new Random(1);

    private final int updateInterval = 15;      // History update interval in seconds
    private final int daysPerInterval = 1;      // History days past per interval
    private final int tradingDaysPerYear = 252; // Trading days per year.
    private final double askSpread = 0.01;      // Difference of ask price and mean in dollars
    private final double bidSpread = 0.01;      // Difference of mean and bid price in dollars

    private final String tickerSymbol;      // Ticker symbol that represents fund
    private final double yearlyReturn;      // Average annual percent return of the fund
    private final double volatility;        // Annual standard deviation of the percent return

    private final List<Double> history;        // Price history stored in this list

    /*
     * REQUIRES: ticker.length() > 0
     * EFFECTS: name of account is set to accountName;
     *          if initialBalance >= 0 then balance on account is set to
     *          initialBalance, else balance is set to zero.
     */
    public Fund(String ticker, double initialPrice, double yearlyReturn, double volatility) {
        this.tickerSymbol = ticker;
        this.yearlyReturn = yearlyReturn;
        this.volatility = volatility;
        history = new ArrayList<>();
        history.add(new PriceTime(initialPrice, now(), 0));
    }

    public double getAskPrice() {
        updateHistory();
        return history.get(history.size() - 1).getPrice() + askSpread;
    }

    public double getBidPrice() {
        updateHistory();
        return history.get(history.size() - 1).getPrice() - bidSpread;
    }

    private void updateHistory() {
        int daysPassed = tradingDaysPassed();
        for (int i = daysPassed - 1; i >= 0; i--) {
            int lastTradingDay =
            history.add(new PriceTime(initialPrice, now(), 0));
        }
    }

    private int tradingDaysPassed() {
        Instant now = now();
        Instant last = history.get(history.size() - 1).getInstant();
        Duration elapsed = Duration.between(now, last);
        Duration chunk = Duration.ofSeconds(updateInterval);
        return Math.toIntExact(elapsed.dividedBy(chunk))
                * daysPerInterval;
    }

    protected Instant now() {
        return Instant.now();
    }

    protected double randomReturn() {
        return random.nextGaussian() * stdDevPerDay() // Adjust standard normal
                + returnPerDay();                // distribution to desired normal dist.
    }

    private double returnPerDay() {
        return Math.pow(yearlyReturn, (1.0 / tradingDaysPerYear)); // 1.0 ensures double conversion
    }

    private double stdDevPerDay() {
        return volatility / Math.sqrt(tradingDaysPerYear);
    }

    public String getTicker() {
        return tickerSymbol;
    }

    public List<Double> getHistory() {
        return history;
    }
}