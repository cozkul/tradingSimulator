package ui;

import model.Account;
import persistence.JsonReader;
import persistence.JsonWriter;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.IOException;


public class TradingSimulatorGUI {
    private final GuiState state;
    private Account account;

    private static final String JSON_STORE = "./data/user.json";
    private JsonWriter jsonWriter;
    private JsonReader jsonReader;

    private static final int WIDTH = 800;  // Represents frame width
    private static final int HEIGHT = 600; // Represents frame height
    JFrame frame;

    private JList market;
    private JCheckBox viewStockInChartCheckBox;
    private JRadioButton buyAtAskPriceRadioButton;
    private JRadioButton sellAtBidPriceRadioButton;
    private JTextField quantityField;
    private JButton executeButton;
    private JTable accountTable;
    private JScrollPane chart;
    private JPanel panelMain;

    public TradingSimulatorGUI() {
        state = new GuiState();
        initializeJson();
        initializeGUI();
        initializeSimulator();
    }

    private void initializeGUI() {
        frame = new JFrame();
        frame.setTitle("Trading Simulator");
        frame.setSize(WIDTH, HEIGHT);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setContentPane(panelMain);
        centreOnScreen(frame);
        addMenu();
        executeButton.addActionListener(this::executeTradeHandler);
        buyAtAskPriceRadioButton.addActionListener(this::buyRadioHandler);
        sellAtBidPriceRadioButton.addActionListener(this::sellRadioHandler);
        frame.setVisible(true);
    }

    private void initializeSimulator() {
        loadHandler(new ActionEvent(this,
                ActionEvent.ACTION_PERFORMED, ""));
        if (account == null) {
            loadHandler(new ActionEvent(this,
                    ActionEvent.ACTION_PERFORMED, ""));
        }
    }

    private void initializeJson() {
        jsonWriter = new JsonWriter(JSON_STORE);
        jsonReader = new JsonReader(JSON_STORE);
    }

    /**
     * Helper to centre main application window on desktop
     */
    private void centreOnScreen(JFrame frame) {
        int width = Toolkit.getDefaultToolkit().getScreenSize().width;
        int height = Toolkit.getDefaultToolkit().getScreenSize().height;
        frame.setLocation((width - frame.getWidth()) / 2, (height - frame.getHeight()) / 2);
    }

    /**
     * Adds menu bar.
     */
    private void addMenu() {
        JMenuBar menuBar = new JMenuBar();

        JMenu fileMenu = new JMenu("File");
        fileMenu.setMnemonic('F');
        addMenuItem(fileMenu, new NewAccountAction(),
                KeyStroke.getKeyStroke("control N"));
        addMenuItem(fileMenu, new SaveAccountAction(),
                KeyStroke.getKeyStroke("control S"));
        addMenuItem(fileMenu, new LoadAccountAction(),
                KeyStroke.getKeyStroke("control O"));
        menuBar.add(fileMenu);

        JMenu simulationMenu = new JMenu("Simulation");
        simulationMenu.setMnemonic('S');
        addMenuItem(simulationMenu, new CreateSecurity(),
                KeyStroke.getKeyStroke("control X"));
        menuBar.add(simulationMenu);

        frame.setJMenuBar(menuBar);
    }

    /**
     * Adds an item with given handler to the given menu
     * @param theMenu  menu to which new item is added
     * @param action   handler for new menu item
     * @param accelerator    keystroke accelerator for this menu item
     */
    private void addMenuItem(JMenu theMenu, AbstractAction action, KeyStroke accelerator) {
        JMenuItem menuItem = new JMenuItem(action);
        menuItem.setMnemonic(menuItem.getText().charAt(0));
        menuItem.setAccelerator(accelerator);
        theMenu.add(menuItem);
    }

    private class NewAccountAction extends AbstractAction {
        NewAccountAction() {
            super("New Account");
        }

        @Override
        public void actionPerformed(ActionEvent evt) {
            newAccountHandler(evt);
        }
    }

    private class SaveAccountAction extends AbstractAction {
        SaveAccountAction() {
            super("Save File");
        }

        @Override
        public void actionPerformed(ActionEvent evt) {
            saveHandler(evt);
        }
    }

    private class LoadAccountAction extends AbstractAction {
        LoadAccountAction() {
            super("Load File");
        }

        @Override
        public void actionPerformed(ActionEvent evt) {
            loadHandler(evt);
        }
    }

    private class CreateSecurity extends AbstractAction {
        CreateSecurity() {
            super("Create new Security");
        }

        @Override
        public void actionPerformed(ActionEvent evt) {
            createSecurityHandler(evt);
        }
    }

    private void saveHandler(ActionEvent evt) {
        return;
    }

    private void newAccountHandler(ActionEvent evt) {
        return;
    }

    private void loadHandler(ActionEvent evt) {
        try {
            account = jsonReader.read();
        } catch (IOException e) {
            JOptionPane.showMessageDialog(frame, "Unable to read from file: " + JSON_STORE);
            // STUB
        }
    }

    private void createSecurityHandler(ActionEvent evt) {
        return;
    }

    private void executeTradeHandler(ActionEvent evt) {
        quantityField.setText("mooo");
        JOptionPane.showMessageDialog(frame, "mello?");
    }

    private void buyRadioHandler(ActionEvent evt) {
        state.setBuySellState(0);
        updateRadio();
    }

    private void sellRadioHandler(ActionEvent evt) {
        state.setBuySellState(1);
        updateRadio();
    }

    private void updateRadio() {
        buyAtAskPriceRadioButton.setSelected(state.getBuySellState() == 0);
        sellAtBidPriceRadioButton.setSelected(state.getBuySellState() == 1);
    }

    private void updateCheckBox() {

    }
}
