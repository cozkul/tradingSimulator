package model;

import model.mock.FundMock;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;

import static model.Fund.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class FundTest {
    private static final double EPSILON = 0.05;

    FundMock testFund;

    @BeforeEach
    void runBefore() {
        testFund = new FundMock("A", 100, 0.5, 0, Instant.now());
    }

    @Test
    void testGetAskPrice() {
        assertEquals(100 + ASK_SPREAD, testFund.getAskPrice());
        testFund.passOneIntervalTime();
        assertEquals(100.17 + ASK_SPREAD, testFund.getAskPrice(), EPSILON);
    }

    @Test
    void testGetBidPrice() {
        assertEquals(100 + ASK_SPREAD, testFund.getAskPrice());
        testFund.passOneIntervalTime();
        assertEquals(100.17 - BID_SPREAD, testFund.getAskPrice(), EPSILON);
    }

    @Test
    void testGetHistory() {
        List<Double> history = testFund.getHistory();
        assertEquals(1, history.size());
        assertEquals(100, history.get(0), EPSILON);
        testFund.passManyIntervalTime(2);
        testFund.getHistory();
        assertEquals(3, history.size());
        assertEquals(100, history.get(0), EPSILON);
        assertEquals(100.17, history.get(1), EPSILON);
        assertEquals(100.32, history.get(2), EPSILON);
        System.out.println(history);
    }
}
