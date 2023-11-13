package ui;

import model.Security;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.util.HashSet;
import java.util.Set;

public class GuiState {
    private int buySellState;
    private final Set<Security> viewableSecurities;
    DefaultListModel<Security> listSecurities;
    DefaultTableModel tableModel;

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

    public boolean getViewSecurity(Security security) {
        return viewableSecurities.contains(security);
    }

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

    public void reset() {
        buySellState = 0;
        viewableSecurities.clear();
        listSecurities.clear();
    }
}
