package ui;

import model.Security;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.util.HashSet;
import java.util.Set;

/*
 * Represents GUI State
 */
public class GuiState {
    private int buySellState;                       // Stores buy or sell state. Buy: 0, Sell: 1
    private final Set<Security> viewableSecurities; // Stores securities selected to be viewed in chart.
    DefaultListModel<Security> listSecurities;      // Stores securities selected to be viewed in market list.
    DefaultTableModel tableModel;                   // Stores table model for market list.

    /*
     * EFFECTS: Creates a state for TradingSimulatorGUI. Default state is
     *          buy state, no securities viewed in chart, empty list in market,
     *          account with columns "Security" and "Position".
     */
    public GuiState() {
        buySellState = 0;
        viewableSecurities = new HashSet<>();
        listSecurities = new DefaultListModel<>();
        tableModel = new DefaultTableModel();
        tableModel.addColumn("Security");
        tableModel.addColumn("Position");
    }


    public int getBuySellState() {
        return buySellState;
    }

    public void setBuySellState(int buySellState) {
        this.buySellState = buySellState;
    }

    /*
     * REQUIRES: security not null
     * EFFECTS: Returns true only if security should be viewed in chart.
     */
    public boolean getViewSecurity(Security security) {
        return viewableSecurities.contains(security);
    }

    /*
     * REQUIRES: security not null
     * MODIFIES: this
     * EFFECTS: Sets the security to be viewable or not depending on view.
     */
    public void setViewSecurity(Security security, boolean view) {
        if (view) {
            viewableSecurities.add(security);
        } else {
            viewableSecurities.remove(security);
        }
    }

    public DefaultListModel<Security> getListSecurities() {
        return listSecurities;
    }

    public DefaultTableModel getTableModel() {
        return tableModel;
    }

    public Set<Security> getViewableSecurities() {
        return viewableSecurities;
    }

    /*
     * MODIFIES: this
     * EFFECTS: returns this to default state described in constructor.
     */
    public void reset() {
        buySellState = 0;
        viewableSecurities.clear();
        listSecurities.clear();
        tableModel.getDataVector().removeAllElements();
    }
}
