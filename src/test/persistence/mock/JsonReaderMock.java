package persistence.mock;

import model.Fund;
import model.mock.FundMock;
import org.json.JSONObject;
import persistence.JsonReader;

import java.time.Instant;
import java.util.List;

public class JsonReaderMock extends JsonReader {

    public JsonReaderMock(String source) {
        super(source);
    }

    @Override
    protected Fund makeFund(JSONObject jsonObject) {
        Fund temp = super.makeFund(jsonObject);
        Instant lastUpdate = Instant.parse(jsonObject.getString("lastUpdate"));
        List<Double> history = makeHistory(jsonObject.getJSONArray("history"));

        return new FundMock(temp.getTicker(),
                temp.getYearlyReturn(),
                temp.getVolatility(),
                history,
                lastUpdate,
                temp.getFundPosition()
        );
    }
}
