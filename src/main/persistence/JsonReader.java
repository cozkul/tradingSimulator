package persistence;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import model.Account;
import model.Security;
import org.json.*;

// Represents a reader that reads workroom from JSON data stored in file
public class JsonReader {
    private final String source; // Destination for *.json file

    // EFFECTS: constructs reader to read from source file
    public JsonReader(String source) {
        this.source = source;
    }

    // EFFECTS: reads workroom from file and returns it;
    // throws IOException if an error occurs reading data from file
    public Account read() throws IOException {
        String jsonData = readFile(source);
        JSONObject jsonObject = new JSONObject(jsonData);
        return parseAccount(jsonObject);
    }

    // EFFECTS: reads source file as string and returns it
    private String readFile(String source) throws IOException {
        StringBuilder contentBuilder = new StringBuilder();

        try (Stream<String> stream = Files.lines(Paths.get(source), StandardCharsets.UTF_8)) {
            stream.forEach(contentBuilder::append);
        }

        return contentBuilder.toString();
    }

    // EFFECTS: parses account from JSON object and returns it
    private Account parseAccount(JSONObject jsonObject) {
        String name = jsonObject.getString("name");
        double balance = jsonObject.getDouble("balance");
        List<Security> securities = makeFunds(jsonObject.getJSONArray("securities"));

        return new Account(name, balance, securities);
    }

    // EFFECTS: parses a list of funds from JSON array and returns it
    private List<Security> makeFunds(JSONArray jsonFunds) {
        List<Security> securities = new ArrayList<>();
        for (Object json : jsonFunds) {
            Security security = makeFund((JSONObject) json);
            securities.add(security);
        }
        return securities;
    }

    // EFFECTS: parses a Security from JSON object and returns it
    protected Security makeFund(JSONObject jsonObject) {
        String ticker = jsonObject.getString("ticker");
        double yearlyReturn = jsonObject.getDouble("yearlyReturn");
        double volatility = jsonObject.getDouble("volatility");
        List<Double> history = makeHistory(jsonObject.getJSONArray("history"));
        Instant lastUpdate = Instant.parse(jsonObject.getString("lastUpdate"));
        int securityPosition = jsonObject.getInt("securityPosition");

        return new Security(ticker,
                yearlyReturn,
                volatility,
                history,
                lastUpdate,
                securityPosition
        );
    }

    // EFFECTS: parses a list of doubles from JSON array and returns it
    protected List<Double> makeHistory(JSONArray jsonHistory) {
        List<Double> history = new ArrayList<>();
        for (int i = 0; i < jsonHistory.length(); i++) {
            history.add(jsonHistory.getDouble(i));
        }
        return history;
    }
}
