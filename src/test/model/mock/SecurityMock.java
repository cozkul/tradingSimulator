package model.mock;

import model.Security;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotNull;

public class SecurityMock extends Security {
    Instant lastTime;
    Instant now;

    public SecurityMock(String ticker, double initialPrice, double yearlyReturn, double volatility, Instant now) {
        super(ticker, initialPrice, yearlyReturn, volatility);
        this.now = now;
        this.lastTime = now.plus(1, ChronoUnit.SECONDS);
        this.lastHistoryUpdate = now;
    }

    public SecurityMock(String ticker, double yearlyReturn, double volatility, List<Double> history,
                        Instant now, int fundPosition) {
        super(ticker, yearlyReturn, volatility, history, now, fundPosition);
        this.now = now;
        this.lastTime = now.plus(1, ChronoUnit.SECONDS);

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

    public void passOneIntervalTime() {
        Instant later = lastTime.plus(UPDATE_INTERVAL, ChronoUnit.SECONDS);
        setNow(later);
        lastTime = later;
    }

    public void passManyIntervalTime(int t) {
        for (int i = 0; i < t; i++) {
            passOneIntervalTime();
        }
    }
}
