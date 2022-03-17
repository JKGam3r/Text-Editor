package main.gui.components.fileinfo;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.File;

/**
 * Handles picking files to open and save from the text editor.
 * Simplifies the method options by not extending the JFileChooser class,
 * making it so that only a few select methods are available to use.
 *
 * @author Justin Kocur
 */
public class FileSelector {
    /** A number (designated by JFileChooser) signifying the user has selected a file */
    public static final int APPROVE_OPTION = JFileChooser.APPROVE_OPTION;

    /* The JFileChooser instance responsible for carrying out all the actions
       necessary for the FileSelector instance to function properly for directory selection */
    private final JFileChooser directoryChooser;

    /* The JFileChooser instance responsible for carrying out all the actions
       necessary for the FileSelector instance to function properly for file selection */
    private final JFileChooser fileChooser;

    /**
     *  Constructs a new FileSelector instance
     *
     * @param path  the home of the file
     */
    public FileSelector(String path) {
        fileChooser = new JFileChooser(path);

        directoryChooser = new JFileChooser("c:");
        directoryChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

        // Disable "all files" option
        directoryChooser.setAcceptAllFileFilterUsed(false);
    }

    public File getSelectedDirectory() {
        return directoryChooser.getSelectedFile();
    }

    /**
     * Returns the selected file. This can be set either by the programmer
     * via setSelectedFile or by a user action, such as either typing the
     * filename into the UI or selecting the file from a list in the UI
     *
     * @return  the selected file
     */
    public File getSelectedFile() {
        return fileChooser.getSelectedFile();
    }

    public int showDirectoryOpenDialog(Component parent) {
        return directoryChooser.showOpenDialog(parent);
    }

    /**
     * Pops up an "Open File" file chooser dialog. Note that the text that appears
     * in the approve button is determined by the L&F
     *
     * @param parent    the parent component of the dialog, can be null
     * @return          the return state of the file chooser on popdown
     */
    public int showOpenDialog(Component parent) {
        return fileChooser.showOpenDialog(parent);
    }

    /**
     * Pops up a "Save File" file chooser dialog. Note that the text that appears
     * in the approve button is determined by the L&F
     *
     * @param parent    the parent component of the dialog, can be null
     * @return          the return state of the file chooser on popdown
     */
    public int showSaveDialog(Component parent) {
        return fileChooser.showSaveDialog(parent);
    }
}
