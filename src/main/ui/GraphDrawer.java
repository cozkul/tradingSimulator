package ui;

import javax.swing.*;
import java.awt.*;

class GraphDrawer extends JPanel {
    private int[] coordsY;
    private final int initialX;
    private final int initialY;
    private final int finalX;
    private final int finalY;
    private final int unitX;
    private final int unitY;

    public GraphDrawer(Dimension size, int[] coordsY) {
        this.coordsY = coordsY;
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

        int prevX = initialX;
        int prevY = finalY;
        //We draw each of our coords in red color
        g2d.setColor(Color.RED);
        for (int y : coordsY) {
            g2d.drawLine(prevX, prevY, prevX += unitX, prevY = finalY - (y * unitY));
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
}