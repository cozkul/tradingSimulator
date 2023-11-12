package model;

import model.exception.InsufficientBalanceException;
import model.exception.InsufficientFundsException;
import model.mock.SecurityMock;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;

import static model.Security.*;
import static org.junit.jupiter.api.Assertions.*;

public class AccountTest {
    private static final double EPSILON = 0.05;

    Account testAccount;
    Security firstSecurity;
    Instant now;

    @BeforeEach
    void runBefore() {
        now = Instant.now();
        firstSecurity = new SecurityMock("A500", 100, 0.5, 0, now);
        testAccount = new Account("John Smith", 1000, firstSecurity);
    }

    @Test
    void testAddFundOnce() {
        List<Security> securities = testAccount.getFunds();
        assertEquals(1, securities.size());
        assertEquals(firstSecurity, securities.get(0));
        Security securityB = new SecurityMock("B500", 200, 0.5, 0, now);
        testAccount.addFund(securityB);
        assertEquals(2, securities.size());
        assertEquals(firstSecurity, securities.get(0));
        assertEquals(securityB, securities.get(1));
    }

    @Test
    void testAddFundMultiple() {
        List<Security> securities = testAccount.getFunds();
        Security securityB = new SecurityMock("B500", 200, 0.5, 0, now);
        Security securityC = new SecurityMock("C500", 200, 0.5, 0, now);
        // FundD with duplicate ticker should not be added.
        Security securityD = new SecurityMock("C500", 200, 0.5, 0, now);
        testAccount.addFund(securityB);
        testAccount.addFund(securityC);
        testAccount.addFund(securityD);

        assertEquals(3, securities.size());
        assertEquals(firstSecurity, securities.get(0));
        assertEquals(securityB, securities.get(1));
        assertEquals(securityC, securities.get(2));
    }

    @Test
    void testBuyFundAtAskPrice() {
        assertEquals(1000, testAccount.getBalance(), EPSILON);
        try {
            testAccount.buyFundAtAskPrice(5, firstSecurity);
        } catch (Exception ignored) {
            fail();
        }
        assertEquals((1000 - 5 * (100 + ASK_SPREAD)), testAccount.getBalance(), EPSILON);
        assertEquals(5, firstSecurity.getPosition());
    }

    @Test
    void testBuyFundAtAskPriceException() {
        assertThrows(InsufficientBalanceException.class, () ->
                testAccount.buyFundAtAskPrice(11, firstSecurity));
    }

    @Test
    void testSellFundAtBidPrice() {
        assertEquals(1000, testAccount.getBalance(), EPSILON);
        try {
            testAccount.buyFundAtAskPrice(5, firstSecurity);
            testAccount.sellFundAtBidPrice(4, firstSecurity);
        } catch (Exception ignored) {
            fail();
        }
        assertEquals(
                (1000 - 5 * (100 + ASK_SPREAD) + 4 * (100 - BID_SPREAD)),
                testAccount.getBalance(),
                EPSILON
        );
        assertEquals(1, firstSecurity.getPosition());
    }

    @Test
    void testSellFundAtBidPriceException() {
        assertThrows(InsufficientFundsException.class, () ->
                testAccount.sellFundAtBidPrice(1, firstSecurity));
    }

    @Test
    void testToString() {
        assertEquals(
                "[ name: John Smith, cash: $1000.00, A500 position: 0 ]",
                testAccount.toString()
        );

        try {
            testAccount.buyFundAtAskPrice(5, firstSecurity);
        } catch (Exception ignored) {
            fail();
        }
        Security securityB = new SecurityMock("B500", 200, 0.5, 0, now);
        testAccount.addFund(securityB);
        assertEquals(
                "[ name: John Smith, cash: $499.95, A500 position: 5, B500 position: 0 ]",
                testAccount.toString()
        );
    }

    @Test
    void testFindFund() {
        Security securityB = new SecurityMock("B500", 200, 0.5, 0, now);
        Security securityC = new SecurityMock("C500", 200, 0.5, 0, now);
        testAccount.addFund(securityB);
        testAccount.addFund(securityC);

        Security foundA = testAccount.findFund("A500");
        Security foundB = testAccount.findFund("B500");
        Security foundC = testAccount.findFund("C500");
        Security foundD = testAccount.findFund("D500");
        assertEquals(firstSecurity, foundA);
        assertEquals(securityB, foundB);
        assertEquals(securityC, foundC);
        assertNull(foundD);
    }
}
