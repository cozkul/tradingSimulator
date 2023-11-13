package persistence.mock;

import model.Security;
import model.mock.SecurityMock;
import org.json.JSONObject;
import persistence.JsonReader;

import java.time.Instant;
import java.util.List;

public class JsonReaderMock extends JsonReader {

    public JsonReaderMock(String source) {
        super(source);
    }

    @Override
    protected Security makeFund(JSONObject jsonObject) {
        Security temp = super.makeFund(jsonObject);
        Instant lastUpdate = Instant.parse(jsonObject.getString("lastUpdate"));
        List<Double> history = makeHistory(jsonObject.getJSONArray("history"));

        return new SecurityMock(temp.getTicker(),
                temp.getYearlyReturn(),
                temp.getVolatility(),
                history,
                lastUpdate,
                temp.getSecurityPosition()
        );
    }
}
