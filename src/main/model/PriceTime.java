package model;

import java.time.Instant;

public class PriceTime {
    private final double price;
    private final Instant instant;

    public PriceTime(double price, Instant time) {
        this.price = price;
        this.instant = time;
    }

    public double getPrice() {
        return price;
    }

    public Instant getInstant() {
        return instant;
    }

    public int getTradingDay() {
        return tradingDay;
    }

    /*
     * EFFECTS: returns a string representation of price
     */
    @Override
    public String toString() {
        String balanceStr = String.format("%.2f", price);
        return "[ day = " + tradingDay + ", price = $" + price + "]";
    }
}
