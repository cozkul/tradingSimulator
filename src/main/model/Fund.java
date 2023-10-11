package model;

import java.util.List;

public class Fund {
    private final int updateInterval = 15;  // History update interval in seconds
    private final int daysPerInterval = 1;  // History dates past per interval

    private final String tickerSymbol;      // Ticker symbol that represents fund
    private List<PriceTime> history;        // Price history stored in this list

    /*
     * REQUIRES: ticker.length() > 0
     * EFFECTS: name of account is set to accountName;
     *          if initialBalance >= 0 then balance on account is set to
     *          initialBalance, else balance is set to zero.
     */
    public Fund(String ticker) {
        tickerSymbol = ticker;
        updateHistory();
    }

    public double getAskPrice() {
        updateHistory();
        return 0; // stub
    }

    public double getBidPrice() {
        updateHistory();
        return 0; // stub
    }

    private void updateHistory() {
        if (history != null || checkForUpdate()) {
            history.add(new PriceTime());
        }
    }

    private boolean checkForUpdate() {
        return true; // stub
    }

    public String getTicker() {
        return tickerSymbol;
    }

    public List<PriceTime> getHistory() {
        return history;
    }
}