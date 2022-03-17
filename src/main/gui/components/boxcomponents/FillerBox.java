package main.gui.components.boxcomponents;

import javax.swing.*;
import java.awt.*;
import java.util.Objects;

/**
 * Contains general information regarding the text editor
 *
 * @author Justin Kocur
 */
public class FillerBox extends Box {
    // Displays text to the path to the opened file
    private final JLabel dirLabel;

    // Contains the general text for the current directory
    private final JLabel genLabel;

    /**
     *  Creates the FillerBox instance
     */
    public FillerBox() {
        super();

        genLabel = new JLabel("File Path: ");
        dirLabel = new JLabel();

        add(genLabel);
        add(dirLabel);

        genLabel.setFont(new Font("arial", Font.PLAIN, 12));
        dirLabel.setFont(new Font("arial", Font.ITALIC, 12));
    }

    /**
     * Sets the width and height of the component
     *
     * @param width     the width of the component
     * @param height    the height of the component
     */
    public void setContainerDimensions(int width, int height) {
        setDimensions(this, width, height);
    }

    public void setShownText(String text) {
        dirLabel.setText(Objects.requireNonNullElse(text, "Not Set "));
    }

    /**
     *  Sets the color of all text in the box
     *
     * @param color     the new color
     */
    /*public void setTextColor(Color color) {
        genLabel.setForeground(color);
        dirLabel.setForeground(color);
    }*/
}
