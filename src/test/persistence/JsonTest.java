package persistence;

import model.Account;
import model.Fund;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class JsonTest {
    static final double EPSILON = 0.05;

    protected void assertAccountsEqual(Account expected, Account received) {
        assertEquals(expected.getName(), received.getName());
        assertEquals(expected.getBalance(), received.getBalance(), EPSILON);

        assertEquals(expected.getFunds().size(), received.getFunds().size());
        for (int i = 0; i < expected.getFunds().size(); i++) {
            assertFundsEqual(expected.getFunds().get(i), received.getFunds().get(i));
        }
    }

    protected void assertFundsEqual(Fund expected, Fund received) {
        assertEquals(expected.getTicker(), received.getTicker());
        assertEquals(expected.getYearlyReturn(), received.getYearlyReturn(), EPSILON);
        assertEquals(expected.getVolatility(), received.getVolatility(), EPSILON);
        assertEquals(expected.getLastHistoryUpdate(), received.getLastHistoryUpdate());
        assertEquals(expected.getFundPosition(), received.getFundPosition());

        assertEquals(expected.getHistory().size(), received.getHistory().size());
        for (int i = 0; i < expected.getHistory().size(); i++) {
            assertEquals(expected.getHistory().get(i), received.getHistory().get(i), EPSILON);
        }
    }
}
