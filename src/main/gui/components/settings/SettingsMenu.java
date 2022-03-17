package main.gui.components.settings;

import main.gui.EditorContainer;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

/**
 * Allows the user to alter various properties of the text editor
 *
 * @author Justin Kocur
 */
public class SettingsMenu {
    // Allows the user to see specific options for dealing with the appearance of the editor
    private final JButton appearanceTopicButton;

    /* True if 'appearanceTopicButton' is expanded to show existing options for user;
       false if hiding additional content */
    private boolean appearanceTopicButtonExpanded;

    // The main GUI for this text editor
    private final EditorContainer editorContainer;

    // Contains all components necessary for the menu to function
    private final JDialog settingsWindow;

    /**
     * Constructs a SettingsMenu instance
     *
     * @param editorContainer  the main GUI for this text editor
     */
    public SettingsMenu(EditorContainer editorContainer) {
        this.editorContainer = editorContainer;

        settingsWindow = new JDialog(editorContainer.getMainFrame(), "Settings");
        constructSettingsWindow();

        appearanceTopicButton = new JButton("+ Appearance");

        constructMenuSystem();
    }

    /**
     *  Displays and hides the settings menu upon request
     *
     * @param show  true if menu should be shown, false if hidden
     */
    public void showSettingsMenu(boolean show) {
        settingsWindow.setVisible(show);
        editorContainer.getMainFrame().setEnabled(!show);
    }

    /**
     *  Creates the menu system for the user to navigate around.
     *
     *  The menu system, unlike the one located in the 'EditorContainer'
     *  class, consists of JButtons instead of JMenu components.
     */
    private void constructMenuSystem() {
        JPanel menuPanel = new JPanel();

        setDimensions(appearanceTopicButton, 125, 25);

        ButtonListener buttonListener = new ButtonListener();
        appearanceTopicButton.addActionListener(buttonListener);

        menuPanel.add(appearanceTopicButton);

        settingsWindow.add(menuPanel, BorderLayout.WEST);
    }

    /**
     * Defines necessary properties for the main JDialog window
     * for the settings menu
     */
    private void constructSettingsWindow() {
        int width   = (int) (Toolkit.getDefaultToolkit().getScreenSize().getWidth() * 0.85);
        int height  = (int) (Toolkit.getDefaultToolkit().getScreenSize().getHeight() * 0.85);

        settingsWindow.setSize(width, height);
        settingsWindow.setLocationRelativeTo(null);
        settingsWindow.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
        settingsWindow.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                editorContainer.getMainFrame().setEnabled(true);
                settingsWindow.setDefaultCloseOperation(JDialog.HIDE_ON_CLOSE);
            }
        });
    }

    /**
     * Sets the width and height of the component
     *
     * @param c         the component to change dimensions on
     * @param width     the width of the component
     * @param height    the height of the component
     */
    private void setDimensions(Component c, int width, int height) {
        c.setPreferredSize(new Dimension(width, height));
        c.setSize(width, height);
    }

    /**
     * Controls what should happen upon the user pressing certain buttons
     * in the Settings Menu
     *
     * @author Justin Kocur
     */
    private class ButtonListener implements ActionListener {

        /**
         *  Calls the method corresponding to the button pressed
         *
         * @param e     information on action
         */
        @Override
        public void actionPerformed(ActionEvent e) {
            if(e.getSource() == SettingsMenu.this.appearanceTopicButton) {
                appearanceTopicFunctionality();
            }
        }

        private void appearanceTopicFunctionality() {
            SettingsMenu.this.appearanceTopicButtonExpanded = !SettingsMenu.this.appearanceTopicButtonExpanded;
            if(SettingsMenu.this.appearanceTopicButtonExpanded)
                SettingsMenu.this.appearanceTopicButton.setText("- Appearance");
            else
                SettingsMenu.this.appearanceTopicButton.setText("+ Appearance");
        }
    }
}
