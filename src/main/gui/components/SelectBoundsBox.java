package main.gui.components;

import main.gui.EditorContainer;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

/**
 * Used to set the beginning and end indexes to highlight in a text pane.
 * More specifically, this class represents a GUI displayed to the user,
 * allowing said user to highlight a portion of the text in the text pane.
 */
public class SelectBoundsBox {
    private final EditorContainer editorContainer;

    private final JTextField endIdxField;

    /* "Submit button used to confirm user action and
       select text between given indexes */
    private final JButton selectButton;

    private final JTextField startIdxField;

    private final JDialog selectionWindow;

    public SelectBoundsBox(EditorContainer editorContainer) {
        this.editorContainer = editorContainer;

        selectionWindow = new JDialog(editorContainer.getMainFrame(), "Select");
        constructWindow();

        selectButton    = new JButton("Select Text");
        startIdxField   = new JTextField();
        endIdxField     = new JTextField();

        constructComponents();
    }

    /**
     *  Displays and hides the selection box upon request
     *
     * @param show  true if box should be shown, false if hidden
     */
    public void showSelectionBox(boolean show) {
        selectionWindow.setVisible(show);
        editorContainer.getMainFrame().setEnabled(!show);
    }

    /**
     * Defines designated properties for each of the components
     * belonging to the selectionWindow
     */
    private void constructComponents() {
        startIdxField.setPreferredSize(new Dimension(150, 25));
        endIdxField.setPreferredSize(new Dimension(150, 25));
        selectButton.setPreferredSize(new Dimension(150, 25));

        selectButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    int tabIdx      = editorContainer.getTabbedPane().getSelectedIndex();
                    int startIdx    = Integer.parseInt(startIdxField.getText());
                    int endIdx      = Integer.parseInt(endIdxField.getText());
                    int length      = editorContainer.getTextPane(tabIdx).getText().length();

                    // Invalid index range
                    if(startIdx >= endIdx) {
                        PopUp.displayErrorMessage(selectionWindow, "Invalid index range.");
                        return;
                    }

                    // 'startIdx' out of bounds
                    if(startIdx < 0 || startIdx >= length) {
                        PopUp.displayErrorMessage(selectionWindow, "Index out of bounds.");
                        return;
                    }

                    // 'endIdx' out of bounds
                    if(endIdx >= length) {
                        PopUp.displayErrorMessage(selectionWindow, "Index out of bounds.");
                        return;
                    }

                    editorContainer.getTextPane(tabIdx).select(startIdx, endIdx);
                } catch(NumberFormatException ex) {
                    PopUp.displayErrorMessage(selectionWindow, ex.getMessage());
                }
            }
        });

        JPanel panel = new JPanel();
        panel.add(startIdxField);
        panel.add(endIdxField);
        panel.add(selectButton);
        selectionWindow.add(panel);
    }

    private void constructWindow() {
        int width   = 400;
        int height  = 100;

        selectionWindow.setSize(width, height);
        selectionWindow.setLocationRelativeTo(null);
        selectionWindow.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
        selectionWindow.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                editorContainer.getMainFrame().setEnabled(true);
                selectionWindow.setDefaultCloseOperation(JDialog.HIDE_ON_CLOSE);
            }
        });
    }
}
