package ui;

public class Main {
    public static void main(String[] args) {
        if (args.length >= 1 && args[0].equals("-cli")) {
            new TradingSimulatorCLI();
        } else {
            new TradingSimulatorGUI();
        }
    }
}