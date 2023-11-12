package persistence;

import model.Account;
import model.Security;
import model.mock.SecurityMock;
import org.junit.jupiter.api.Test;
import persistence.mock.JsonReaderMock;

import java.io.IOException;
import java.time.Instant;

import static org.junit.jupiter.api.Assertions.fail;

class JsonWriterTest extends JsonTest {
    //NOTE TO CPSC 210 STUDENTS: the strategy in designing tests for the JsonWriter is to
    //write data to a file and then use the reader to read it back in and check that we
    //read in a copy of what was written out.

    @Test
    void testWriterInvalidFile() {
        try {
            JsonWriter writer = new JsonWriter("./data/my\0illegal:fileName.json");
            writer.open();
            fail("IOException was expected");
        } catch (IOException e) {
            // pass
        }
    }

    @Test
    void testWriterConstructedAccount() {
        try {
            Instant now = Instant.now();
            Security firstSecurity = new SecurityMock("A500", 100, 0.5, 0, now);
            Account testAccountWrite = new Account("John Smith", 1000, firstSecurity);

            JsonWriter writer = new JsonWriter("./data/testWriterConstructedAccount.json");
            writer.open();
            writer.write(testAccountWrite);
            writer.close();

            JsonReader reader = new JsonReaderMock("./data/testWriterConstructedAccount.json");
            Account testAccountRead = reader.read();

            assertAccountsEqual(testAccountWrite, testAccountRead);
        } catch (IOException e) {
            fail("Exception should not have been thrown");
        }
    }

    @Test
    void testWriterGeneralWorkroom() {
        try {
            Instant now = Instant.now();
            SecurityMock firstFund = new SecurityMock("A500", 100, 0.5, 0, now);
            SecurityMock secondFund = new SecurityMock("B500", 50, 0.1, 0.2, now);
            Account testAccountWrite = new Account("John Smith", 1000, firstFund);
            testAccountWrite.addFund(secondFund);

            firstFund.passManyIntervalTime(5);
            secondFund.passManyIntervalTime(5);
            firstFund.getHistory();
            secondFund.getHistory();

            JsonWriter writer = new JsonWriter("./data/testWriterGeneralAccount.json");
            writer.open();
            writer.write(testAccountWrite);
            writer.close();

            JsonReader reader = new JsonReaderMock("./data/testWriterGeneralAccount.json");
            Account testAccountRead = reader.read();

            assertAccountsEqual(testAccountWrite, testAccountRead);
        } catch (IOException e) {
            fail("Exception should not have been thrown");
        }
    }
}