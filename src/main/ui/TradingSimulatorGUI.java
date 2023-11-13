package ui;

import model.Account;
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


public class TradingSimulatorGUI {
    private GuiState state;
    private Account account;

    private static final String JSON_STORE = "./data/user.json";
    private JsonWriter jsonWriter;
    private JsonReader jsonReader;

    private static final int WIDTH = 800;  // Represents frame width
    private static final int HEIGHT = 600; // Represents frame height
    JFrame frame;

    private JList<Security> market;
    private JCheckBox viewSecurityInChartCheckBox;
    private JRadioButton buyAtAskPriceRadioButton;
    private JRadioButton sellAtBidPriceRadioButton;
    private JTextField quantityField;
    private JButton executeButton;
    private JTable accountTable;
    private JPanel panelMain;
    private JLabel quoteField;
    private JLabel nameLabel;
    private JLabel cashLabel;
    private JScrollPane accountScrollPane;
    private JPanel chartPanel;

    public TradingSimulatorGUI() {
        // createUIComponents() is called here.
        initializeJson();
        initializeSimulator();
        initializeGUI();
        scheduleUpdate(); // disable while debugging
    }

    private void scheduleUpdate() {
        Runnable update = new Runnable() {
            public void run() {
                updateAll();
            }
        };
        ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
        executor.scheduleAtFixedRate(update, 0, 5, TimeUnit.SECONDS);
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
        viewSecurityInChartCheckBox.addActionListener(this::viewCheckHandler);
        viewSecurityInChartCheckBox.addActionListener(this::viewCheckHandler);
        frame.setResizable(false);
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

    private void createUIComponents() {
        state = new GuiState();
        market = new JList<>(state.getListSecurities());
        market.setCellRenderer(new MarketListCellRenderer());
        MouseListener mouseListener = new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                marketClickHandler(e);
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
            super("Create New Security");
        }

        @Override
        public void actionPerformed(ActionEvent evt) {
            createSecurityHandler(evt);
        }
    }

    private void saveHandler(ActionEvent evt) {
        try {
            jsonWriter.open();
            jsonWriter.write(account);
            jsonWriter.close();
            JOptionPane.showMessageDialog(frame, "Saved " + account.getName() + " to " + JSON_STORE);
        } catch (FileNotFoundException e) {
            JOptionPane.showMessageDialog(frame, "Unable to write to file: " + JSON_STORE);
        }
    }

    private void newAccountHandler(ActionEvent evt) {
        NewAccountDialog dialog = new NewAccountDialog(this);
        dialog.setTitle("Please Enter Account Information");
        dialog.setLocationRelativeTo(frame);
        dialog.pack();
        dialog.setResizable(false);
        dialog.setVisible(true);
    }

    public void makeNewAccount(String name, double initialAmount) {
        state.reset();
        Security security = new Security("SP500", 400, 0.07, .20);
        account = new Account(name, initialAmount, security);
        updateAll();
    }

    private boolean firstLoad = true;

    private void loadHandler(ActionEvent evt) {
        try {
            state.reset();
            account = jsonReader.read();
            updateAll();
            if (firstLoad) {
                firstLoad = false;
            } else {
                JOptionPane.showMessageDialog(frame, "Loaded " + account.getName() + " from " + JSON_STORE);
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(frame, "Unable to read from file: " + JSON_STORE);
        }
    }

    private void viewCheckHandler(ActionEvent evt) {
        Security selected = market.getSelectedValue();
        state.setViewSecurity(selected, viewSecurityInChartCheckBox.isSelected());
        updateChart();
    }

    private void createSecurityHandler(ActionEvent evt) {
        NewSecurityDialog dialog = new NewSecurityDialog(this);
        dialog.setTitle("Please Enter Security Information");
        dialog.setLocationRelativeTo(frame);
        dialog.pack();
        dialog.setResizable(false);
        dialog.setVisible(true);
    }

    public void createSecurity(String ticker, double price, double annualReturn, double std) {
        Security security = new Security(ticker, price, annualReturn, std);
        account.addFund(security);
        updateMarket();
        updateAccountTable();
    }

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
        updateAll();
    }

    private void buyRadioHandler(ActionEvent evt) {
        state.setBuySellState(0);
        updateRadio();
    }

    private void sellRadioHandler(ActionEvent evt) {
        state.setBuySellState(1);
        updateRadio();
    }

    private void marketClickHandler(MouseEvent evt) {
        updateCheckBox();
        updateQuote();
    }

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

    private void updateRadio() {
        buyAtAskPriceRadioButton.setSelected(state.getBuySellState() == 0);
        sellAtBidPriceRadioButton.setSelected(state.getBuySellState() == 1);
    }

    private void updateCheckBox() {
        Security selected = market.getSelectedValue();
        viewSecurityInChartCheckBox.setSelected(state.getViewSecurity(selected));
    }

    private void updateQuote() {
        Security selected = market.getSelectedValue();
        String bidPrice = String.format("%.2f", selected.getBidPrice());
        String askPrice = String.format("%.2f", selected.getAskPrice());
        String quote = "Current bid price: $" + bidPrice + " , ask price: $" + askPrice;
        quoteField.setText(quote);
    }

    private void updateAccountTable() {
        DefaultTableModel tableModel = state.getTableModel();
        List<Security> securities = account.getSecurities();
        tableModel.getDataVector().removeAllElements();
        for (Security s : securities) {
            tableModel.addRow(new Object[]{s.getTicker(), s.getSecurityPosition()});
        }
    }

    private void updateAccountNameCash() {
        String balance = String.format("%.2f", account.getBalance());
        nameLabel.setText("Account Holder: " + account.getName());
        cashLabel.setText("Buying Power: $" + balance);
    }

    private void updateChart() {
        chartPanel.repaint();
    }

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
