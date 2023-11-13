package ui;

import java.awt.*;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

public class GraphSample {
    private JFrame frame;

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new GraphSample()::createAndShowGui);
    }

    private void createAndShowGui() {
        frame = new JFrame(getClass().getSimpleName());

        GraphDrawer drawer = new GraphDrawer(new Dimension(400, 300), new int[] {0, 3, 4, 7, 5, 10, 3});

        frame.add(drawer);
        frame.pack();
        frame.setVisible(true);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }
}