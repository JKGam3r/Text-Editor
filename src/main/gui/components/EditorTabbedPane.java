package main.gui.components;

import main.gui.EditorContainer;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 *  A class that is essentially a JTabbedPane from the Swing package.
 *  The main difference is the addition of an "x" button that removes
 *  the tab when the button is clicked
 *
 * @author Justin Kocur
 */
public class EditorTabbedPane extends JTabbedPane {
    // The color of the borders (lines) along the tabbed pane's boundaries
    private Color borderColor;

    // The main GUI for the text editor
    private EditorContainer editorContainer;

    // Controls the color of the tabs
    private Color tabColor;

    /* The color of the selected tab, represented by a line
       directly underneath the tab */
    private Color selTabColor;

    /**
     * Constructs a modified JTabbedPane
     */
    public EditorTabbedPane() {
        selTabColor = new Color(100, 150, 150);
        borderColor = new Color(0, 0, 0, 75);
        tabColor    = Color.white;

        setBackground(Color.white);
    }

    /**
     *  Returns the color of the borders (lines) along the tabbed pane's boundaries
     *
     * @return  the color of the borders (lines) along the tabbed pane's boundaries
     */
    public Color getBorderColor() {
        return borderColor;
    }

    /**
     *  Sets the color of the borders (lines) along the tabbed pane's boundaries
     *
     * @param borderColor  the color of the borders (lines) along the tabbed pane's boundaries
     */
    public void setBorderColor(Color borderColor) {
        this.borderColor = borderColor;
    }

    /**
     *  Returns the EditorContainer instance associated with this tabbed pane
     *
     * @return  the EditorContainer instance associated with this tabbed pane
     */
    public EditorContainer getEditorContainer() {
        return editorContainer;
    }

    /**
     *  Sets the EditorContainer instance associated with this tabbed pane
     *
     * @param editorContainer  the EditorContainer instance associated with this tabbed pane
     */
    public void setEditorContainer(EditorContainer editorContainer) {
        this.editorContainer = editorContainer;
    }

    /**
     *  Sets the color of the selected tab, represented by a line
     *  directly underneath the tab
     *
     * @return  the color of the selected tab, represented by a line
     * directly underneath the tab
     */
    public Color getSelTabColor() {
        return selTabColor;
    }

    /**
     *  Returns the color of the selected tab, represented by a line
     *  directly underneath the tab
     *
     * @param selTabColor  the color of the selected tab, represented by a line
     * directly underneath the tab
     */
    public void setSelTabColor(Color selTabColor) {
        this.selTabColor = selTabColor;
    }

    /**
     *  Acts the same as the JTabbedPane's add() method, only
     *  the addition of a close button is tacked onto the tab heading.
     *
     * @param title         the name of the tab
     * @param component     displayed when the tab is clicked
     * @return              the component associated with this tab
     */
    @Override
    public Component add(String title, Component component) {
        Component c     = super.add(title, component);
        JPanel panel    = new JPanel();

        panel.setOpaque(false);
        setTabComponentAt(getTabCount() - 1, getTitlePanel(component, title));
        return c;
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        g.setColor(getBackground());
        g.fillRect(0, 0, getWidth(), getHeight());

        int idx = getSelectedIndex();

        if(idx < 0)
            return;

        Rectangle bounds    = getBoundsAt(idx);
        Graphics2D g2d      = (Graphics2D) g;
        int strW            = 2;

        g2d.setStroke(new BasicStroke(strW));
        g2d.setColor(borderColor);
        g2d.drawLine(strW / 2, 1, getWidth() - strW / 2, 1);
        g2d.drawLine(strW / 2, bounds.y + bounds.height - strW,
                getWidth() - strW / 2, bounds.y + bounds.height - strW);

        strW = 4;
        g2d.setStroke(new BasicStroke(strW));
        g2d.setColor(selTabColor);
        g2d.drawLine(bounds.x + strW / 2 + 1, bounds.y + bounds.height - strW / 2,
                bounds.x + bounds.width - strW / 2 - 1, bounds.y + bounds.height - strW / 2);
    }

    @Override
    public void setForeground(Color color) {
        super.setForeground(color);

        for(int i = 0; i < getTabCount(); i++) {
            if (getTabComponentAt(i) instanceof JPanel) {
                if (((JPanel) getTabComponentAt(i)).getComponents()[0] instanceof JLabel)
                    (((JPanel) getTabComponentAt(i)).getComponents()[0]).setForeground(color);
                if (((JPanel) getTabComponentAt(i)).getComponents()[1] instanceof JButton)
                    (((JPanel) getTabComponentAt(i)).getComponents()[1]).setForeground(color);
            }
        }
    }

    /**
     *  Acts the same as the JTabbedPane's 'setTitleAt()' method, only the JLabel text
     *  associated with the tab is changed
     *
     * @param index     the index at which to change the title
     * @param title     the String name to change to
     */
    @Override
    public void setTitleAt(int index, String title) {
        super.setTitleAt(index, title);

        if(getTabComponentAt(index) instanceof JPanel) {
            if(((JPanel) getTabComponentAt(index)).getComponents()[0] instanceof JLabel) {
                ((JLabel) ((JPanel) getTabComponentAt(index)).getComponents()[0]).setText(title);
            }
        }
    }

    /**
     *  Constructs a new heading for the whole tab, consisting of the
     *  tab's title and a remove ("x") button.
     *
     * @param component     the component which is on the tab
     * @param title         the title of the tab
     * @return              a JPanel containing the modified heading for the new tab
     */
    private JPanel getTitlePanel(Component component, final String title) {
        // Declare/ initialize required components
        JPanel titlePanel   = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        JLabel titleLabel   = new JLabel(title);
        JButton closeButton = new JButton("x");

        titleLabel.setForeground(getForeground());
        closeButton.setForeground(getForeground());

        // Define properties of each component
        titlePanel.setOpaque(false);
        titlePanel.add(titleLabel);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 5));
        closeButton.setBorderPainted(false);
        closeButton.setFocusPainted(false);
        closeButton.setContentAreaFilled(false);
        closeButton.setOpaque(false);

        // Used to control events with the close ("x") button
        MouseAdapter mouseAdapter = new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if(getEditorContainer() != null) {
                    int index = indexOfComponent(component);
                    getEditorContainer().getEditorActions().closeTabFunctionality(index);
                }
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                closeButton.setForeground(Color.white);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                closeButton.setForeground(getForeground());
            }
        };

        closeButton.addMouseListener(mouseAdapter);
        titlePanel.add(closeButton);

        // Set the font
        for(Component c : titlePanel.getComponents()) {
            c.setFont(editorContainer.getStandardFont());
        }

        return titlePanel;
    }
}
