package persistence;

import model.Account;
import model.Security;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class JsonTest {
    static final double EPSILON = 0.05;

    protected void assertAccountsEqual(Account expected, Account received) {
        assertEquals(expected.getName(), received.getName());
        assertEquals(expected.getBalance(), received.getBalance(), EPSILON);

        assertEquals(expected.getSecurities().size(), received.getSecurities().size());
        for (int i = 0; i < expected.getSecurities().size(); i++) {
            assertFundsEqual(expected.getSecurities().get(i), received.getSecurities().get(i));
        }
    }

    protected void assertFundsEqual(Security expected, Security received) {
        assertEquals(expected.getTicker(), received.getTicker());
        assertEquals(expected.getYearlyReturn(), received.getYearlyReturn(), EPSILON);
        assertEquals(expected.getVolatility(), received.getVolatility(), EPSILON);
        assertEquals(expected.getLastHistoryUpdate(), received.getLastHistoryUpdate());
        assertEquals(expected.getSecurityPosition(), received.getSecurityPosition());

        assertEquals(expected.getHistory().size(), received.getHistory().size());
        for (int i = 0; i < expected.getHistory().size(); i++) {
            assertEquals(expected.getHistory().get(i), received.getHistory().get(i), EPSILON);
        }
    }
}
