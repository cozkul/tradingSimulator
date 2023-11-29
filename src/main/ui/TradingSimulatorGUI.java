package ui;

import model.Account;
import model.Event;
import model.EventLog;
import model.Security;
import model.exception.InsufficientBalanceException;
import model.exception.InsufficientFundsException;
import persistence.JsonReader;
import persistence.JsonWriter;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/*
 * Represents main GUI of the Trading simulator application.
 */
public class TradingSimulatorGUI {
    private GuiState state;  // GuiState stores the important state parameters.
    private Account account; // Stores all account related information.

    private static final String JSON_STORE = "./data/user.json"; // Directory of user save file
    private JsonWriter jsonWriter;                               // jsonWriter object used for saving
    private JsonReader jsonReader;                               // jsonWriter object used for loading

    private static final int WIDTH = 800;  // Represents frame width
    private static final int HEIGHT = 600; // Represents frame height
    JFrame frame;                          // Main frame for the application

    private JList<Security> market;                // JList for all the tickers in the market
    private JCheckBox viewSecurityInChartCheckBox; // JCheckBox for optionally displaying ticker in chart
    private JRadioButton buyAtAskPriceRadioButton; // JRadioButton for buy order
    private JRadioButton sellAtBidPriceRadioButton;// JRadioButton for sell order
    private JTextField quantityField;              // JTextField for quantity of buy or sell order
    private JButton executeButton;                 // JButton for executing buy or sell order
    private JTable accountTable;                   // JTable displays account information
    private JPanel panelMain;                      // JPanel main panel in frame
    private JLabel quoteLabel;                     // JLabel for displaying quote
    private JLabel nameLabel;                      // JLabel for displaying account holder name
    private JLabel cashLabel;                      // JLabel for displaying cash in account
    private JScrollPane accountScrollPane;         // JScrollPane for accountTable
    private JPanel chartPanel;                     // JPanel for displaying the chart

    /*
     * EFFECTS: Initializes all fields of the instance:
     *            Creates jsonReader and jsonWriter.
     *            Initializes the simulator by loading last save.
     *            Initializes GUI with relevant references and settings.
     *            Creates a scheduled updater for updating chart and prices.
     */
    public TradingSimulatorGUI() {
        // createUIComponents() is called here.
        initializeJson();
        initializeSimulator();
        initializeGUI();
        scheduleUpdate(); // disable while debugging
    }

    /*
     * REQUIRES: account, quoteLabel, chart not null
     * MODIFIES: this
     * EFFECTS: Creates a scheduled updater for updating chart and prices.
     */
    private void scheduleUpdate() {
        Runnable update = () -> {
            updateQuote();
            updateChart();
        };
        ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
        executor.scheduleAtFixedRate(update, 0, Security.UPDATE_INTERVAL, TimeUnit.SECONDS);
    }

    /*
     * REQUIRES: executeButton, buyAtAskPriceRadioButton, sellAtBidPriceRadioButton,
     *           viewSecurityInChartCheckBox not null
     * MODIFIES: this
     * EFFECTS: Creates frame, centers frame on screen, adds menu options to frame,
     *          adds action listeners to from components, disables frame resizing, makes
     *          frame visible.
     */
    private void initializeGUI() {
        frame = new MainFrame();
        frame.setTitle("Trading Simulator");
        frame.setSize(WIDTH, HEIGHT);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setContentPane(panelMain);
        frame.addWindowListener((MainFrame)frame);
        centreOnScreen(frame);
        addMenu();
        executeButton.addActionListener(this::executeTradeHandler);
        buyAtAskPriceRadioButton.addActionListener(this::buyRadioHandler);
        sellAtBidPriceRadioButton.addActionListener(this::sellRadioHandler);
        viewSecurityInChartCheckBox.addActionListener(this::viewCheckHandler);
        frame.setResizable(false);
        frame.setVisible(true);
    }

    private static class MainFrame extends JFrame implements WindowListener {

        public MainFrame() {
            super();
        }

        @Override
        public void windowOpened(WindowEvent e) {

        }

        @Override
        public void windowClosing(WindowEvent e) {
            EventLog eventLog = EventLog.getInstance();
            for (Event event : eventLog) {
                System.out.println(event);
            }
        }

        @Override
        public void windowClosed(WindowEvent e) {

        }

        @Override
        public void windowIconified(WindowEvent e) {

        }

        @Override
        public void windowDeiconified(WindowEvent e) {

        }

        @Override
        public void windowActivated(WindowEvent e) {

        }

        @Override
        public void windowDeactivated(WindowEvent e) {

        }
    }

    /*
     * REQUIRES: ./data/user.json to exist and be valid
     * MODIFIES: this
     * EFFECTS: Initialize simulator using existing save file
     */
    private void initializeSimulator() {
        if (account == null) {
            loadHandler();
        }
    }

    /*
     * MODIFIES: this
     * EFFECTS: Initializes jsonWriter, jsonReader using value in JSON_STORE
     */
    private void initializeJson() {
        jsonWriter = new JsonWriter(JSON_STORE);
        jsonReader = new JsonReader(JSON_STORE);
    }

    /*
     * REQUIRES: frame not null
     * MODIFIES: this
     * EFFECTS: Centers frame on screen
     */
    private void centreOnScreen(JFrame frame) {
        int width = Toolkit.getDefaultToolkit().getScreenSize().width;
        int height = Toolkit.getDefaultToolkit().getScreenSize().height;
        frame.setLocation((width - frame.getWidth()) / 2, (height - frame.getHeight()) / 2);
    }

    /*
     * REQUIRES: frame not null
     * MODIFIES: this
     * EFFECTS: Adds menu bar to frame.
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
        addMenuItem(simulationMenu, new CreateSecurityAction(),
                KeyStroke.getKeyStroke("control X"));
        menuBar.add(simulationMenu);

        frame.setJMenuBar(menuBar);
    }

    /*
     * REQUIRES: theMenu, action, accelerator not null
     * MODIFIES: theMenu
     * EFFECTS: Creates a menuItem and adds to theMenu
     */
    private void addMenuItem(JMenu theMenu, AbstractAction action, KeyStroke accelerator) {
        JMenuItem menuItem = new JMenuItem(action);
        menuItem.setMnemonic(menuItem.getText().charAt(0));
        menuItem.setAccelerator(accelerator);
        theMenu.add(menuItem);
    }

    /*
     * MODIFIES: this
     * EFFECTS: Custom create for form auto called. Initializes state,
     *          sets up market, accountScrollPane, accountTable, chartPanel.
     */
    private void createUIComponents() {
        state = new GuiState();
        market = new JList<>(state.getListSecurities());
        market.setCellRenderer(new MarketListCellRenderer());
        MouseListener mouseListener = new MouseAdapter() {
            public void mouseClicked(MouseEvent evt) {
                marketClickHandler();
            }
        };
        market.addMouseListener(mouseListener);
        accountScrollPane = new JScrollPane();
        accountTable = new JTable(state.getTableModel());
        accountTable.setPreferredScrollableViewportSize(accountScrollPane.getSize());

        accountTable.setPreferredScrollableViewportSize(accountTable.getPreferredSize());
        accountTable.setFillsViewportHeight(true);

        chartPanel = new GraphDrawer(new Dimension(600, 400), state);
    }

    /*
     * Represents a new account action called by the menu in frame.
     */
    private class NewAccountAction extends AbstractAction {
        /*
         * EFFECTS: Initialize Abstract Action with name "New Account".
         */
        NewAccountAction() {
            super("New Account");
        }

        /*
         * EFFECTS: Calls newAccountHandler(evt) upon action performed.
         */
        @Override
        public void actionPerformed(ActionEvent evt) {
            newAccountHandler();
        }
    }

    /*
     * Represents a save account action called by the menu in frame.
     */
    private class SaveAccountAction extends AbstractAction {
        /*
         * EFFECTS: Initialize Abstract Action with name "Save File".
         */
        SaveAccountAction() {
            super("Save File");
        }

        /*
         * EFFECTS: Calls saveHandler(evt) upon action performed.
         */
        @Override
        public void actionPerformed(ActionEvent evt) {
            saveHandler();
        }
    }

    /*
     * Represents a load account action called by the menu in frame.
     */
    private class LoadAccountAction extends AbstractAction {
        /*
         * EFFECTS: Initialize Abstract Action with name "Load File".
         */
        LoadAccountAction() {
            super("Load File");
        }

        /*
         * EFFECTS: Calls loadHandler(evt) upon action performed.
         */
        @Override
        public void actionPerformed(ActionEvent evt) {
            loadHandler();
        }
    }

    /*
     * Represents a new security action called by the menu in frame.
     */
    private class CreateSecurityAction extends AbstractAction {
        /*
         * EFFECTS: Initialize Abstract Action with name "Create New Security".
         */
        CreateSecurityAction() {
            super("Create New Security");
        }

        /*
         * EFFECTS: Calls createSecurityHandler(evt) upon action performed.
         */
        @Override
        public void actionPerformed(ActionEvent evt) {
            createSecurityHandler();
        }
    }

    /*
     * REQUIRES: account, frame, jsonWriter not null
     * MODIFIES: this
     * EFFECTS: Called upon save action from menu. Saves the account to JSON_STORE.
     *          Creates a message dialog about save status.
     */
    private void saveHandler() {
        try {
            jsonWriter.open();
            jsonWriter.write(account);
            jsonWriter.close();
            JOptionPane.showMessageDialog(frame, "Saved " + account.getName() + " to " + JSON_STORE);
        } catch (FileNotFoundException e) {
            JOptionPane.showMessageDialog(frame, "Unable to write to file: " + JSON_STORE);
        }
    }

    /*
     * REQUIRES: frame not null
     * EFFECTS: Called upon new account action from menu.
     *          Creates a NewAccountDialog and displays it to user.
     */
    private void newAccountHandler() {
        NewAccountDialog dialog = new NewAccountDialog(this);
        dialog.setTitle("Please Enter Account Information");
        dialog.setLocationRelativeTo(frame);
        dialog.pack();
        dialog.setResizable(false);
        dialog.setVisible(true);
    }

    /*
     * REQUIRES: initialAmount > 500; chartPanel, nameLabel, account, cashLabel, state, quoteLabel,
     *           viewSecurityInChartCheckBox, buyAtAskPriceRadioButton, sellAtBidPriceRadioButton,
     *           not null
     * MODIFIES: this
     * EFFECTS: Resets state, creates a new account with name and initialAmount,
     *          sets the current this.account to created account, updates all visual components.
     */
    public void makeNewAccount(String name, double initialAmount) {
        state.reset();
        Security security = new Security("SP500", 400, 0.07, .20);
        account = new Account(name, initialAmount, security);
        updateAll();
    }

    private boolean initialLoad = true; // A boolean that allows no status display for the first load of the app
                                        // Used by loadHandler.

    /*
     * REQUIRES: ./data/user.json to exist and be valid
     *           chartPanel, nameLabel, account, cashLabel, state, quoteLabel, frame,
     *           viewSecurityInChartCheckBox, buyAtAskPriceRadioButton, sellAtBidPriceRadioButton,
     *           not null
     * MODIFIES: this
     * EFFECTS: Resets gui state, replaces account with new account from jsonReader,
     *          updates all visual components, displays a notification for user if it not the first successful load.
     *          Displays error if the load is not successful even if it is not the first load.
     */
    private void loadHandler() {
        try {
            state.reset();
            account = jsonReader.read();
            updateAll();
            if (initialLoad) {
                initialLoad = false;
            } else {
                JOptionPane.showMessageDialog(frame, "Loaded " + account.getName() + " from " + JSON_STORE);
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(frame, "Unable to read from file: " + JSON_STORE);
        }
    }

    /*
     * REQUIRES: market, state, viewSecurityInChartCheckBox, chartPanel not null.
     * MODIFIES: this
     * EFFECTS: Changes the state using setViewSecurity if viewSecurityInChartCheckBox is selected.
     *          Updates chart.
     */
    private void viewCheckHandler(ActionEvent evt) {
        Security selected = market.getSelectedValue();
        state.setViewSecurity(selected, viewSecurityInChartCheckBox.isSelected());
        updateChart();
    }

    /*
     * REQUIRES: frame not null
     * MODIFIES: this
     * EFFECTS: Creates a NewSecurityDialog and displays it to user.
     */
    private void createSecurityHandler() {
        NewSecurityDialog dialog = new NewSecurityDialog(this);
        dialog.setTitle("Please Enter Security Information");
        dialog.setLocationRelativeTo(frame);
        dialog.pack();
        dialog.setResizable(false);
        dialog.setVisible(true);
    }

    /*
     * REQUIRES: account, ticker, market, state not null.
     * MODIFIES: this
     * EFFECTS: Create a security and add to account.
     */
    public void createSecurity(String ticker, double price, double annualReturn, double std) {
        Security security = new Security(ticker, price, annualReturn, std);
        account.addFund(security);
        updateMarket();
        updateAccountTable();
    }

    /*
     * REQUIRES: state, account, market, quantityField, frame, nameLabel, account, cashLabel not null.
     *           Valid input in quantityField from user.
     * MODIFIES: this
     * EFFECTS: Checks the state to decide execute buy order or sell order.
     *          Uses the value in quantityField or order amount.
     *          Shows a dialog whether the order was successful.
     *          Updates account table and account cash displayed.
     */
    private void executeTradeHandler(ActionEvent evt) {
        if (state.getBuySellState() == 0) { // time to buy
            try {
                account.buyFundAtAskPrice(
                        Integer.parseInt(quantityField.getText()),
                        market.getSelectedValue());
                JOptionPane.showMessageDialog(frame, "Order successfully executed.");
            } catch (InsufficientBalanceException e) {
                JOptionPane.showMessageDialog(frame, "You do not have enough cash.");
            }
        } else if (state.getBuySellState() == 1) { // time to sell
            try {
                account.sellFundAtBidPrice(
                        Integer.parseInt(quantityField.getText()),
                        market.getSelectedValue());
                JOptionPane.showMessageDialog(frame, "Order successfully executed.");
            } catch (InsufficientFundsException e) {
                JOptionPane.showMessageDialog(frame, "You do not have enough of this position to sell.");
            }
        }
        updateAccountTable();
        updateAccountNameCash();
    }

    /*
     * REQUIRES: buyAtAskPriceRadioButton, sellAtBidPriceRadioButton, state not null.
     * MODIFIES: this
     * EFFECTS: Changes the state to buy state and updates all radioButtons.
     */
    private void buyRadioHandler(ActionEvent evt) {
        state.setBuySellState(0);
        updateRadio();
    }

    /*
     * REQUIRES: buyAtAskPriceRadioButton, sellAtBidPriceRadioButton, state not null.
     * MODIFIES: this
     * EFFECTS: Changes the state to sell state and updates all radioButtons.
     */
    private void sellRadioHandler(ActionEvent evt) {
        state.setBuySellState(1);
        updateRadio();
    }

    /*
     * REQUIRES: market, viewSecurityInChartCheckBox, quoteLabel not null
     * MODIFIES: this
     * EFFECTS: Updates the quote and checkbox for the selected security.
     */
    private void marketClickHandler() {
        updateCheckBox();
        updateQuote();
    }

    /*
     * REQUIRES: market, state, account not null
     * MODIFIES: this
     * EFFECTS: Updates the market list with the listed securities in state.
     *          First save selected from market, then remove all from market,
     *          add everything back from states listed securities, set the selected to saved security.
     */
    private void updateMarket() {
        DefaultListModel<Security> listSecurities = state.getListSecurities();
        Security selected = market.getSelectedValue();
        listSecurities.clear();
        state.getListSecurities().addAll(account.getSecurities());
        if (selected != null) {
            market.setSelectedValue(selected, true);
        } else {
            market.setSelectedIndex(0);
        }
    }

    /*
     * REQUIRES: buyAtAskPriceRadioButton, sellAtBidPriceRadioButton, state not null
     * MODIFIES: this
     * EFFECTS: Sets the radio boxes selected depending on the state.
     */
    private void updateRadio() {
        buyAtAskPriceRadioButton.setSelected(state.getBuySellState() == 0);
        sellAtBidPriceRadioButton.setSelected(state.getBuySellState() == 1);
    }

    /*
     * REQUIRES: market, viewSecurityInChartCheckBox not null
     * MODIFIES: this
     * EFFECTS: Reads the selected ticker from market and updates the view checkbox accordingly.
     */
    private void updateCheckBox() {
        Security selected = market.getSelectedValue();
        viewSecurityInChartCheckBox.setSelected(state.getViewSecurity(selected));
    }

    /*
     * REQUIRES: market, quoteLabel not null
     * MODIFIES: this
     * EFFECTS: Reads the selected ticker from market and updates the quote accordingly.
     */
    private void updateQuote() {
        Security selected = market.getSelectedValue();
        String bidPrice = String.format("%.2f", selected.getBidPrice());
        String askPrice = String.format("%.2f", selected.getAskPrice());
        String quote = "Current bid price: $" + bidPrice + " , ask price: $" + askPrice;
        quoteLabel.setText(quote);
    }

    /*
     * REQUIRES: state, account not null
     * MODIFIES: this
     * EFFECTS: Update account table with positions in the account.
     */
    private void updateAccountTable() {
        DefaultTableModel tableModel = state.getTableModel();
        List<Security> securities = account.getSecurities();
        tableModel.getDataVector().removeAllElements();
        for (Security s : securities) {
            tableModel.addRow(new Object[]{s.getTicker(), s.getSecurityPosition()});
        }
    }

    /*
     * REQUIRES: nameLabel, account, cashLabel not null
     * MODIFIES: this
     * EFFECTS: Sets nameLabel with name in account.
     *          Sets cashLabel with balance in account.
     */
    private void updateAccountNameCash() {
        String balance = String.format("%.2f", account.getBalance());
        nameLabel.setText("Account Holder: " + account.getName());
        cashLabel.setText("Buying Power: $" + balance);
    }

    /*
     * REQUIRES: chartPanel not null
     * MODIFIES: this
     * EFFECTS: repaints chartPanel
     */
    private void updateChart() {
        chartPanel.repaint();
    }

    /*
     * REQUIRES: chartPanel, nameLabel, account, cashLabel, state, quoteLabel,
     *           viewSecurityInChartCheckBox, buyAtAskPriceRadioButton, sellAtBidPriceRadioButton,
     *           not null
     * MODIFIES: this
     * EFFECTS: Updates all visual components of importance.
     */
    private void updateAll() {
        updateCheckBox();
        updateRadio();
        updateMarket();
        updateQuote();
        updateAccountNameCash();
        updateAccountTable();
        updateChart();
    }
}
