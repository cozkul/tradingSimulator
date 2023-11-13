package ui;

import model.Security;

import javax.swing.*;
import java.awt.*;

class MarketListCellRenderer extends JLabel implements ListCellRenderer<Security> {

    public MarketListCellRenderer() {
        setOpaque(true);
    }

    @Override
    public Component getListCellRendererComponent(JList<? extends Security> list, Security value, int index,
                                                  boolean isSelected, boolean cellHasFocus) {
        setText(value.getTicker());
        if (isSelected) {
            setBackground(list.getSelectionBackground());
            setForeground(list.getSelectionForeground());
        } else {
            setBackground(list.getBackground());
            setForeground(list.getForeground());
        }
        setFont(list.getFont());
        setEnabled(list.isEnabled());
        if (isSelected && cellHasFocus) {
            setBorder(list.getBorder());
        } else {
            setBorder(null);
        }
        return this;
    }
}
