package ui;

import model.Security;

import javax.swing.*;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Color;
import java.util.List;

class GraphDrawer extends JPanel {
    private GuiState guiState;

    private final int initialX;
    private final int initialY;
    private final int finalX;
    private final int finalY;
    private final int unitX;
    private final int unitY;

    public GraphDrawer(Dimension size, GuiState guiState) {
        this.guiState = guiState;
        finalX = 10;
        initialY = 20;
        initialX = (int) (size.getWidth() - 50);
        finalY = (int) (size.getHeight() - 20);
        unitX = (finalX - initialX) / 10;
        unitY = (finalY - initialY) / 10;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;

        drawGridAxis(g2d);

        Color[] colors = generateColors(guiState.getViewableSecurities().size());
        double maxPrice = getMaxPrice();
        int i = 0;
        for (Security security : guiState.getViewableSecurities()) {
            g2d.setColor(colors[i]);
            List<Double> history = security.getHistory();
            int prevX = initialX;
            int prevY = (int) (finalY - (history.get(history.size() - 1) / maxPrice * 10 * unitY));
            int lowerBound = Math.max(history.size() - 11, 0);
            for (int j = history.size() - 1; --j >= lowerBound;) {
                double y = history.get(j) / maxPrice * 10;
                g2d.drawLine(prevX, prevY, prevX += unitX, prevY = (int) (finalY - (y * unitY)));
            }
            ++i;
        }
    }

    private void drawGridAxis(Graphics2D g2d) {
        g2d.setColor(Color.GRAY);
        // Grid
        for (int i = initialX; i >= finalX; i += unitX) {
            g2d.drawLine(i, initialY, i, finalY);
        }
        for (int i = initialY; i <= finalY; i += unitY) {
            g2d.drawLine(initialX, i, finalX, i);
        }
        // Axis
        g2d.setColor(Color.BLACK);
        g2d.drawLine(initialX, initialY, initialX, finalY);
        g2d.drawLine(initialX, finalY, finalX, finalY);
    }

    private Color[] generateColors(int n) {
        Color[] cols = new Color[n];
        for (int i = 0; i < n; ++i) {
            cols[i] = Color.getHSBColor((float) i / (float) n, 0.85f, 1.0f);
        }
        return cols;
    }

    private double getMaxPrice() {
        double ans = 0.0;
        for (Security security : guiState.getViewableSecurities()) {
            for (Double price : security.getHistory()) {
                ans = (price > ans) ? price : ans;
            }
        }
        return ans;
    }
}