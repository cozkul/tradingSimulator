package ui;

import javax.swing.*;
import java.awt.event.*;

public class NewSecurityDialog extends JDialog {
    private JPanel contentPane;
    private JButton buttonOK;
    private JButton buttonCancel;
    private JTextField priceField;
    private JTextField returnField;
    private JTextField volatilityField;
    private JTextField tickerField;

    private final TradingSimulatorGUI tradingSimulatorGUI;

    public NewSecurityDialog(TradingSimulatorGUI tradingSimulatorGUI) {
        this.tradingSimulatorGUI = tradingSimulatorGUI;

        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(buttonOK);

        buttonOK.addActionListener(e -> onOK());

        buttonCancel.addActionListener(e -> onCancel());

        // call onCancel() when cross is clicked
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                onCancel();
            }
        });

        // call onCancel() on ESCAPE
        contentPane.registerKeyboardAction(e ->
                onCancel(),
                KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0),
                JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT
        );
    }

    private void onOK() {
        dispose();
        tradingSimulatorGUI.createSecurity(
                tickerField.getText(),
                Double.parseDouble(priceField.getText()),
                Double.parseDouble(returnField.getText()),
                Double.parseDouble(volatilityField.getText())
        );
    }

    private void onCancel() {
        dispose();
    }
}
