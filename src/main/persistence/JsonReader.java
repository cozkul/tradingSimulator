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
import model.Fund;
import org.json.*;

// Represents a reader that reads workroom from JSON data stored in file
public class JsonReader {
    private final String source;

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
        List<Fund> funds = makeFunds(jsonObject.getJSONArray("funds"));

        return new Account(name, balance, funds);
    }

    // EFFECTS: parses a list of funds from JSON array and returns it
    private List<Fund> makeFunds(JSONArray jsonFunds) {
        List<Fund> funds = new ArrayList<>();
        for (Object json : jsonFunds) {
            Fund fund = makeFund((JSONObject) json);
            funds.add(fund);
        }
        return funds;
    }

    // EFFECTS: parses a Fund from JSON object and returns it
    protected Fund makeFund(JSONObject jsonObject) {
        String ticker = jsonObject.getString("ticker");
        double yearlyReturn = jsonObject.getDouble("yearlyReturn");
        double volatility = jsonObject.getDouble("volatility");
        List<Double> history = makeHistory(jsonObject.getJSONArray("history"));
        Instant lastUpdate = Instant.parse(jsonObject.getString("lastUpdate"));
        int fundPosition = jsonObject.getInt("fundPosition");

        return new Fund(ticker,
                yearlyReturn,
                volatility,
                history,
                lastUpdate,
                fundPosition
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
