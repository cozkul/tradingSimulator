package model.mock;

import model.Fund;

import java.time.Instant;

public class FundMock extends Fund {
    Instant now;

    public FundMock(String ticker, double initialPrice, double yearlyReturn, double volatility, Instant now) {
        super(ticker, initialPrice, yearlyReturn, volatility, now);
        this.now = now;
    }

    @Override
    protected double randomReturn() {
        super.randomReturn(); // Just for code coverage
        return returnPerDay();
    }

    @Override
    protected Instant now() {
        super.now(); // Just for code coverage
        return this.now;
    }

    public void setNow(Instant now) {
        this.now = now;
    }
}
