package model.mock;

import model.Fund;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class FundMock extends Fund {
    Instant now;

    public FundMock(String ticker, double initialPrice, double yearlyReturn, double volatility, Instant now) {
        super(ticker, initialPrice, yearlyReturn, volatility, now);
        this.now = now;
    }

    @Override
    protected double randomReturn() {
        Double temp = super.randomReturn();
        assertNotNull(temp);
        return returnPerDay();
    }

    @Override
    protected Instant now() {
        Instant temp = super.now();
        assertNotNull(temp);
        return this.now;
    }

    public void setNow(Instant now) {
        this.now = now;
    }
}
