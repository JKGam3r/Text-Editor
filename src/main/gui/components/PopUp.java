package main.gui.components;

import main.editor.Editor;

import javax.swing.*;
import java.awt.*;

/**
 * Utilizes the JOptionPane class type to create customized pop ups.  Simplifies
 * the method options by not extending the JOptionPane class, making it so that only
 * a few select methods are available to use.
 *
 * @author Justin Kocur
 */
public class PopUp {
    /** Return value form class method if OK is chosen */
    public static final int OK_OPTION = JOptionPane.OK_OPTION;

    /** Return value from class method if NO is chosen */
    public static final int NO_OPTION = JOptionPane.NO_OPTION;

    /**
     *  Brings up a dialog with the options Yes, No and Cancel; with the title, Select an Option.
     *
     *
     * @param parentComponent   determines the Frame in which the dialog is displayed;
     *                          if null, or if the parentComponent has no Frame,
     *                          a default Frame is used
     * @param message           the Object to display
     * @return                  an integer indicating the option selected by the user
     */
    public static int displayConfirmMessage(Component parentComponent, String message) {
        return JOptionPane.showConfirmDialog(parentComponent, message, "Confirm", JOptionPane.INFORMATION_MESSAGE, JOptionPane.PLAIN_MESSAGE, null);
    }

    /**
     *  Brings up an information-message dialog titled "Message."  This should be used
     *  specifically for errors.
     *
     *
     * @param parentComponent   determines the Frame in which the dialog is displayed;
     *                          if null, or if the parentComponent has no Frame,
     *                          a default Frame is used
     * @param message           the Object to display
     */
    public static void displayErrorMessage(Component parentComponent, String message) {
        JOptionPane.showMessageDialog(parentComponent, "Error: " + message, "Error", JOptionPane.ERROR_MESSAGE, null);
        System.err.println(message);
    }

    /**
     *  Brings up an information-message dialog titled "Message"
     *
     *
     * @param parentComponent   determines the Frame in which the dialog is displayed;
     *                          if null, or if the parentComponent has no Frame,
     *                          a default Frame is used
     * @param message           the Object to display
     */
    public static void displayStandardMessage(Component parentComponent, String message) {
        JOptionPane.showMessageDialog(parentComponent, message, "Message", JOptionPane.INFORMATION_MESSAGE, null);
    }
}
