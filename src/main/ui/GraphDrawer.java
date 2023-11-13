package ui;

import model.Security;

import javax.swing.*;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Color;
import java.util.List;

/*
 * Represents JPanel that can draw a plot of viewableSecurities
 * in guiState.
 */
class GraphDrawer extends JPanel {
    private static final int X_GRID = 365;     // Width of X used for scaling
    private static final int Y_GRID = 10;      // Width of Y used for scaling
    private static final int X_GRID_LINE = 30; // Grid line per X
    private static final int Y_GRID_LINE = 1;  // Grid line per Y

    private final GuiState guiState; // Represents GUI State
    private double maxPrice;         // Maximum price in guiState.getViewableSecurities()

    private final int initialX; // On screen X initial coordinate
    private final int initialY; // On screen Y initial coordinate
    private final int finalX;   // On screen X final coordinate
    private final int finalY;   // On screen Y final coordinate
    private final float unitX;  // Unit X length used for scaling
    private final float unitY;  // Unit Y length used for scaling

    /*
     * REQUIRES: Dimension size not null, size.getWidth() > 50, size.getHeight() > 20
     *           guiState not null.
     * MODIFIES:
     * EFFECTS: Creates GraphDrawer object and initializes internal parameters
     *          for scaling.
     */
    public GraphDrawer(Dimension size, GuiState guiState) {
        this.guiState = guiState;
        updateMaxPrice();
        finalX = 10;
        initialY = 20;
        initialX = (int) (size.getWidth() - 50);
        finalY = (int) (size.getHeight() - 20);
        unitX = ((float) finalX - initialX) / X_GRID;
        unitY = ((float) finalY - initialY) / Y_GRID;
    }

    /*
     * REQUIRES: Graphics g not null
     * MODIFIES: this, Graphics g
     * EFFECTS: Updates maximum price in viewable graphs.
     *          Draws grid and axis by calling drawGridAxis(Graphics2D g2d).
     *          Draws each viewable security in guiState. Colors are unique to each security.
     */
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        updateMaxPrice();
        drawGridAxis(g2d);

        for (Security security : guiState.getViewableSecurities()) {
            g2d.setColor(generateColor(security.hashCode()));
            List<Double> history = security.getHistory();
            float prevX = initialX;
            float prevY = scaleY(history.get(history.size() - 1));
            int realWidth = (X_GRID / X_GRID_LINE) * X_GRID_LINE;
            int lowerBound = Math.max(history.size() - realWidth, 0);
            for (int i = history.size() - 1; --i >= lowerBound;) {
                g2d.drawLine(
                        (int) prevX,
                        (int) prevY,
                        (int) (prevX += unitX),
                        (int) (prevY = scaleY(history.get(i)))
                );
            }
        }
    }

    /*
     * REQUIRES: Graphics2D g2d not null
     * MODIFIES: Graphics2D g2d
     * EFFECTS: Draws grid in gray and axis in black.
     */
    private void drawGridAxis(Graphics2D g2d) {
        g2d.setColor(Color.GRAY);
        // Grid
        for (int i = initialX; i >= finalX; i += unitX * X_GRID_LINE) {
            g2d.drawLine(i, initialY, i, finalY);
        }
        for (int i = initialY; i <= finalY; i += unitY * Y_GRID_LINE) {
            g2d.drawLine(initialX, i, finalX, i);
        }
        // Axis
        g2d.setColor(Color.BLACK);
        g2d.drawLine(initialX, initialY, initialX, finalY);
        g2d.drawLine(initialX, finalY, finalX, finalY);
    }

    /*
     * EFFECTS: Generates a color unique to the hash provided.
     */
    private Color generateColor(int hash) {
        return Color.getHSBColor((float) hash / (float) Integer.MAX_VALUE, 0.85f, 1.0f);
    }

    /*
     * EFFECTS: Returns a scaled version of y.
     */
    private int scaleY(double y) {
        return (int) (finalY - (y / maxPrice * Y_GRID * unitY));
    }

    /*
     * MODIFIES: this
     * EFFECTS: Sets maxPrice to maximum price in guiState.getViewableSecurities()
     *          If there are no securities in guiState.getViewableSecurities(),
     *          maxPrice is set to 0.
     */
    private void updateMaxPrice() {
        maxPrice = 0.0;
        for (Security security : guiState.getViewableSecurities()) {
            for (Double price : security.getHistory()) {
                maxPrice = (price > maxPrice) ? price : maxPrice;
            }
        }
    }
}