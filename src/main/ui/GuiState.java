package ui;

import model.Security;

import java.util.HashSet;
import java.util.Set;

public class GuiState {
    private int buySellState;
    private final Set<Security> viewableSecurities;

    public GuiState() {
        buySellState = 0;
        viewableSecurities = new HashSet<>();
    }

    public int getBuySellState() {
        return buySellState;
    }

    public void setBuySellState(int buySellState) {
        this.buySellState = buySellState;
    }

    public boolean getViewFund(Security security) {
        return viewableSecurities.contains(security);
    }

    public void setViewFund(Security security) {
        if (viewableSecurities.contains(security)) {
            viewableSecurities.remove(security);
        } else {
            viewableSecurities.add(security);
        }
    }
}
