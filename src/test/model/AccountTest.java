package model;

import model.exception.InsufficientBalanceException;
import model.exception.InsufficientFundsException;
import model.mock.FundMock;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;

import static model.Fund.*;
import static org.junit.jupiter.api.Assertions.*;

public class AccountTest {
    private static final double EPSILON = 0.05;

    Account testAccount;
    Fund firstFund;
    Instant now;

    @BeforeEach
    void runBefore() {
        now = Instant.now();
        firstFund = new FundMock("A500", 100, 0.5, 0, now);
        testAccount = new Account("John Smith", 1000, firstFund);
    }

    @Test
    void testAddFundOnce() {
        List<Fund> funds = testAccount.getFunds();
        assertEquals(1, funds.size());
        assertEquals(firstFund, funds.get(0));
        Fund fundB = new FundMock("B500", 200, 0.5, 0, now);
        testAccount.addFund(fundB);
        assertEquals(2, funds.size());
        assertEquals(firstFund, funds.get(0));
        assertEquals(fundB, funds.get(1));
    }

    @Test
    void testAddFundMultiple() {
        List<Fund> funds = testAccount.getFunds();
        Fund fundB = new FundMock("B500", 200, 0.5, 0, now);
        Fund fundC = new FundMock("C500", 200, 0.5, 0, now);
        // FundD with duplicate ticker should not be added.
        Fund fundD = new FundMock("C500", 200, 0.5, 0, now);
        testAccount.addFund(fundB);
        testAccount.addFund(fundC);
        testAccount.addFund(fundD);

        assertEquals(3, funds.size());
        assertEquals(firstFund, funds.get(0));
        assertEquals(fundB, funds.get(1));
        assertEquals(fundC, funds.get(2));
    }

    @Test
    void testBuyFundAtAskPrice() {
        assertApproxEquals(1000, testAccount.getBalance());
        try {
            testAccount.buyFundAtAskPrice(5, firstFund);
        } catch (Exception ignored) {
            fail();
        }
        assertApproxEquals((1000 - 5 * (100 + ASK_SPREAD)), testAccount.getBalance());
        assertEquals(5, firstFund.getPosition());
    }

    @Test
    void testBuyFundAtAskPriceException() {
        assertThrows(InsufficientBalanceException.class, () ->
                testAccount.buyFundAtAskPrice(500, firstFund));
    }

    @Test
    void testSellFundAtBidPrice() {
        assertApproxEquals(1000, testAccount.getBalance());
        try {
            testAccount.buyFundAtAskPrice(5, firstFund);
            testAccount.sellFundAtBidPrice(3, firstFund);
        } catch (Exception ignored) {
            fail();
        }
        assertApproxEquals(
                (1000 - 5 * (100 + ASK_SPREAD) + 3 * (100 - BID_SPREAD)),
                testAccount.getBalance()
        );
        assertEquals(2, firstFund.getPosition());
    }

    @Test
    void testSellFundAtBidPriceException() {
        assertThrows(InsufficientFundsException.class, () ->
                testAccount.sellFundAtBidPrice(500, firstFund));
    }

    @Test
    void testToString() {
        assertEquals(
                "[ name: John Smith, cash: $1000.00, A500 position: 0 ]",
                testAccount.toString()
        );

        try {
            testAccount.buyFundAtAskPrice(5, firstFund);
        } catch (Exception ignored) {
            fail();
        }
        Fund fundB = new FundMock("B500", 200, 0.5, 0, now);
        testAccount.addFund(fundB);
        assertEquals(
                "[ name: John Smith, cash: $499.95, A500 position: 5, B500 position: 0 ]",
                testAccount.toString()
        );
    }

    @Test
    void testFindFund() {
        Fund fundB = new FundMock("B500", 200, 0.5, 0, now);
        Fund fundC = new FundMock("C500", 200, 0.5, 0, now);
        testAccount.addFund(fundB);
        testAccount.addFund(fundC);

        Fund foundA = testAccount.findFund("A500");
        Fund foundB = testAccount.findFund("A500");
        Fund foundC = testAccount.findFund("A500");
        Fund foundD = testAccount.findFund("D500");
        assertNotNull(foundA);
        assertNotNull(foundB);
        assertNotNull(foundC);
        assertNull(foundD);
    }

    void assertApproxEquals(double a, double b) {
        assertTrue(b < a + EPSILON);
        assertTrue(a - EPSILON < b);
    }
}
