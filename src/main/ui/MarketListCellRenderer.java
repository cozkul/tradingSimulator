package ui;

import model.Security;

import javax.swing.*;
import java.awt.*;

/*
 * Represents a class that can render Security objects in JList
 */
class MarketListCellRenderer extends JLabel implements ListCellRenderer<Security> {
    /*
     * EFFECTS: Sets parent JLabel to be opaque
     */
    public MarketListCellRenderer() {
        setOpaque(true);
    }

    /*
     * REQUIRES: list, security not null.
     * MODIFIES: this
     * EFFECTS: Renders value.getTicker() as the text displayed in this JLabel.
     *          If this is selected, sets the background and foreground to default selected colors.
     *          If this is not selected, sets the background and foreground to default colors.
     *          Sets the font to default font, sets this to enabled if list is enabled.
     */
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
        return this;
    }
}
