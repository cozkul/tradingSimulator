package persistence;

import model.Account;
import model.mock.FundMock;
import org.junit.jupiter.api.Test;
import persistence.mock.JsonReaderMock;

import java.io.IOException;
import java.time.Instant;

import static org.junit.jupiter.api.Assertions.fail;

class JsonReaderTest extends JsonTest {

    @Test
    void testReaderNonExistentFile() {
        JsonReader reader = new JsonReader("./data/noSuchFile.json");
        try {
            reader.read();
            fail("IOException expected");
        } catch (IOException e) {
            // pass
        }
    }

    @Test
    void testReaderEmptyWorkRoom() {
        Instant now = Instant.parse("2023-10-17T04:33:00.144500Z");
        FundMock firstFund = new FundMock("A500", 100, 0.5, 0, now);
        Account testAccountExpected = new Account("John Smith", 1000, firstFund);

        JsonReader reader = new JsonReaderMock("./data/testReaderConstructedAccount.json");
        try {
            Account readAccount = reader.read();
            assertAccountsEqual(testAccountExpected, readAccount);
        } catch (IOException e) {
            fail("Couldn't read from file");
        }
    }

    @Test
    void testReaderGeneralWorkRoom() {
        Instant now = Instant.parse("2023-10-17T04:33:00.122226Z");
        FundMock firstFund = new FundMock("A500", 100, 0.5, 0, now);
        FundMock secondFund = new FundMock("B500", 50, 0.1, 0.2, now);
        Account testAccountExpected = new Account("John Smith", 1000, firstFund);
        testAccountExpected.addFund(secondFund);

        firstFund.passManyIntervalTime(5);
        secondFund.passManyIntervalTime(5);
        firstFund.getHistory();
        secondFund.getHistory();

        JsonReader reader = new JsonReaderMock("./data/testReaderGeneralAccount.json");
        try {
            Account readAccount = reader.read();
            assertAccountsEqual(testAccountExpected, readAccount);
        } catch (IOException e) {
            fail("Couldn't read from file");
        }
    }
}