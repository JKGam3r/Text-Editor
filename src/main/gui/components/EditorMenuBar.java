package main.gui.components;

import javax.swing.*;
import java.awt.*;

public class EditorMenuBar extends JMenuBar {
    @Override
    protected void paintChildren(Graphics g) {
        g.setColor(getBackground());
        g.fillRect(0, 0, getWidth(), getHeight());

        super.paintChildren(g);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
    }
}
