package ui;

/*
 * Represents main class for the application.
 */
public class Main {
    /*
     * EFFECTS: Starts the application in GUI mode unless argument
     *          -cli is passed in, in which case starts the application in CLI mode.
     */
    public static void main(String[] args) {
        if (args.length >= 1 && args[0].equals("-cli")) {
            new TradingSimulatorCLI();
        } else {
            new TradingSimulatorGUI();
        }
    }
}