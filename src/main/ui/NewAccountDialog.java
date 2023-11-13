package ui;

import javax.swing.*;
import java.awt.event.*;

/*
 * Represents a class that displays a New Account Dialog and calls tradingSimulatorGUI.makeNewAccount(...)
 * upon completion.
 */
public class NewAccountDialog extends JDialog {
    private JPanel contentPane;
    private JButton buttonOK;
    private JButton buttonCancel;

    private JTextField nameField;
    private JTextField balanceField;

    private final TradingSimulatorGUI tradingSimulatorGUI;

    /*
     * REQUIRES: tradingSimulatorGUI not null
     * EFFECTS: Creates and adds actionListeners for all related components in dialog.
     *          All autogenerated by intelliJ.
     */
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

    /*
     * REQUIRES: User input for balanceField to be double, nameField.getText().size() > 0
     * MODIFIES: this
     * EFFECTS: Closes the dialog and calls tradingSimulatorGUI.makeNewAccount(...) with user input.
     */
    private void onOK() {
        dispose();
        tradingSimulatorGUI.makeNewAccount(
                nameField.getText(),
                Double.parseDouble(balanceField.getText())
        );
    }

    /*
     * MODIFIES: this
     * EFFECTS: Closes the dialog.
     */
    private void onCancel() {
        dispose();
    }
}
