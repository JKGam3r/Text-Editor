package main.gui.components;

import javax.swing.*;
import java.awt.*;

public class EditorMenu extends JMenuItem {
    public EditorMenu(String t) {
        super(t);
    }

    @Override
    protected void paintComponent(Graphics g) {
        g.setColor(Color.orange);
        g.fillRect(0, 0, getWidth(), getHeight());

        super.paintComponent(g);
    }

    @Override
    protected void paintChildren(Graphics g) {
        //super.paintComponent(g);
    }
}
