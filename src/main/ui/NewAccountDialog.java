package ui;

import javax.swing.*;
import java.awt.event.*;

public class NewAccountDialog extends JDialog {
    private JPanel contentPane;
    private JButton buttonOK;
    private JButton buttonCancel;

    private JTextField nameField;
    private JTextField balanceField;

    private final TradingSimulatorGUI tradingSimulatorGUI;

    public NewAccountDialog(TradingSimulatorGUI tradingSimulatorGUI) {
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
        tradingSimulatorGUI.makeNewAccount(
                nameField.getText(),
                Double.parseDouble(balanceField.getText())
        );
    }

    private void onCancel() {
        dispose();
    }
}
