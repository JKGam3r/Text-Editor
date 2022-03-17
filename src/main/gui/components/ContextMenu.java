package main.gui.components;

import main.actions.EditorActions;
import main.gui.EditorContainer;

import javax.swing.*;
import java.awt.*;

/**
 * Houses many popular commands that should be accessed with a right click of the mouse
 *
 * @author Justin Kocur
 */
public class ContextMenu extends JPopupMenu {
    // The main GUI for the application
    private final EditorContainer editorContainer;

    /**
     * Constructs a new ContextMenu
     *
     * @param editorContainer   the main GUI for the application
     */
    public ContextMenu(EditorContainer editorContainer) {
        this.editorContainer = editorContainer;

        constructMenuItems();
    }

    @Override
    protected void paintComponent(Graphics g) {
        g.setColor(Color.pink);
        g.fillRect(0, 0, getWidth(), getHeight());
        //super.paintComponent(g);
    }

    /**
     * Creates the various options available to the user to interact with
     */
    private void constructMenuItems() {
        JMenuItem copyItem = new JMenuItem("Copy");
        JMenuItem pasteItem = new JMenuItem("Paste");
        JMenuItem deleteItem = new JMenuItem("Delete");
        JMenuItem selectAllItem = new JMenuItem("Select All");
        JMenuItem findItem = new JMenuItem("Find");
        JMenuItem replaceItem = new JMenuItem("Replace");

        add(copyItem);
        add(pasteItem);
        add(deleteItem);
        addSeparator();
        add(selectAllItem);
        addSeparator();
        add(findItem);
        add(replaceItem);

        setMenuFont(this, editorContainer.getStandardFont());
        setMenuActionListener(this, editorContainer.getEditorActions());
    }

    /**
     * Adds an EditorActions instance to each component in the provided JMenu instance
     *
     * @param menu              the menu to add the EditorActions instance to its components
     * @param editorActions     added to each JMenuItem in the JMenu instance; provides user interaction
     */
    private void setMenuActionListener(JPopupMenu menu, EditorActions editorActions) {
        for(Component c : menu.getComponents()) {
            if(c instanceof JMenuItem)
                ((JMenuItem) c).addActionListener(editorActions);
        }
    }

    /**
     * Changes the font of all components in a menu
     *
     * @param menu      the menu system for accessing the components
     * @param font      the new font to set
     */
    private void setMenuFont(JPopupMenu menu, Font font) {
        // Change the font of each component
        for(Component c : menu.getComponents()) {
            c.setFont(font);
        }
    }
}
