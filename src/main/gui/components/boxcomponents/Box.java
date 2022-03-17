package main.gui.components.boxcomponents;

import javax.swing.*;
import java.awt.*;

/**
 * The general format/ template that a box (e.g. search box) should take
 *
 * @author Justin Kocur
 */
public class Box extends JPanel {
    private Color backgroundCompColor;

    // The color of the line separating the box from the text pane
    private Color borderColor;

    private Color textColor;

    public Box() {
        this(new FlowLayout(FlowLayout.CENTER));
    }

    public Box(FlowLayout flowLayout) {
        // All components aligned to the left
        setLayout(flowLayout);

        // JPanel should span the entire width of the screen; assume JFrame at max size
        int width = (int) Toolkit.getDefaultToolkit().getScreenSize().getWidth();
        setDimensions(this, width, 32);

        borderColor = new Color(0, 0, 0, 75);

        setBackground(Color.white);
    }

    /**
     *  Returns the color of the line separating the box from the text pane
     *
     * @return  the color of the line separating the box from the text pane
     */
    public Color getBorderColor() {
        return borderColor;
    }

    /**
     *  Sets the color of the line separating the box from the text pane
     *
     * @param borderColor  the color of the line separating the box from the text pane
     */
    public void setBorderColor(Color borderColor) {
        this.borderColor = borderColor;
    }

    /**
     *  Changes the way the JPanel is painted
     *
     * @param g     drawing component
     */
    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        g.setColor(getBackground());
        g.fillRect(0, 0, getWidth(), getHeight());

        Graphics2D g2d  = (Graphics2D) g;
        int strW        = 2;
        g2d.setColor(borderColor);
        g2d.setStroke(new BasicStroke(strW));
        g2d.drawLine(0, getHeight() - strW / 2, getWidth(), getHeight() - strW / 2);
    }

    /**
     *  Sets the color of all background components in the box
     *
     * @param color     the new color
     */
    public void setBackgroundColor(Color color) {
        backgroundCompColor = color;

        for(Component c : getComponents()) {
            if(c instanceof JScrollPane) {
                JScrollPane p = ((JScrollPane) c);

                // Should be scroll pane's text pane
                if(p.getViewport() != null && p.getViewport().getComponents() != null
                        && p.getViewport().getComponents()[0] != null) {
                    p.getViewport().getComponents()[0].setBackground(color);
                }
            } else
                c.setBackground(color);
        }
    }

    /**
     * Sets the width and height of the component
     *
     * @param c         the component to change dimensions on
     * @param width     the width of the component
     * @param height    the height of the component
     */
    protected void setDimensions(Component c, int width, int height) {
        c.setPreferredSize(new Dimension(width, height));
        c.setSize(width, height);
    }

    /**
     *  Sets the position of the whole search box on the text editor
     *
     * @param x     the x-position, left to right
     * @param y     the y-position, bottom to top
     */
    public void setPosition(int x, int y) {
        // Keep original dimensions
        int width   = this.getWidth();
        int height  = this.getHeight();

        this.setBounds(x, y, width, height);
    }

    /**
     *  Sets the color of all text in the box
     *
     * @param color     the new color
     */
    public void setTextColor(Color color) {
        textColor = color;

        for(Component c : getComponents()) {
            if(c instanceof JScrollPane) {
                JScrollPane p = ((JScrollPane) c);

                // Should be scroll pane's text pane
                if(p.getViewport() != null && p.getViewport().getComponents() != null
                        && p.getViewport().getComponents()[0] != null) {
                    p.getViewport().getComponents()[0].setForeground(color);
                }
            } else
                c.setForeground(color);
        }
    }
}
