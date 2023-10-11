package model;

import java.time.LocalDateTime;

public class PriceTime {
    private double price;
    private LocalDateTime time;

    /*
     * EFFECTS: returns a string representation of price
     */
    @Override
    public String toString() {
        String balanceStr = String.format("%.2f", price);
        return "[ time = " + time + ", price = $" + price + "]";
    }
}
