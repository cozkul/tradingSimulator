package model;

import model.mock.FundMock;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

import static model.Fund.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class FundTest {
    private static final double EPSILON = 0.05;

    FundMock testFund;
    Instant lastTime;

    @BeforeEach
    void runBefore() {
        lastTime = Instant.now();
        testFund = new FundMock("A", 100, 0.5, 0, lastTime);
        lastTime = lastTime.plus(1, ChronoUnit.SECONDS);
    }

    @Test
    void testGetAskPrice() {
        assertEquals(100 + ASK_SPREAD, testFund.getAskPrice());
        passOneIntervalTime();
        assertApproxEquals(100.17 + ASK_SPREAD, testFund.getAskPrice());
    }

    @Test
    void testGetBidPrice() {
        assertEquals(100 + ASK_SPREAD, testFund.getAskPrice());
        passOneIntervalTime();
        assertApproxEquals(100.17 - BID_SPREAD, testFund.getAskPrice());
    }

    @Test
    void testGetHistory() {
        List<Double> history = testFund.getHistory();
        assertEquals(1, history.size());
        assertApproxEquals(100, history.get(0));
        passOneIntervalTime();
        passOneIntervalTime();
        testFund.getHistory();
        assertEquals(3, history.size());
        assertApproxEquals(100, history.get(0));
        assertApproxEquals(100.17, history.get(1));
        assertApproxEquals(100.32, history.get(2));
        System.out.println(history);
    }

    void assertApproxEquals(double a, double b) {
        assertTrue(b < a + EPSILON);
        assertTrue(a - EPSILON < b);
    }

    void passOneIntervalTime() {
        Instant later = lastTime.plus(UPDATE_INTERVAL, ChronoUnit.SECONDS);
        testFund.setNow(later);
        lastTime = later;
    }
}