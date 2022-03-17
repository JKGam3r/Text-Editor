package main.actions.undo;

import main.gui.EditorContainer;

import javax.swing.*;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.UndoManager;
import java.awt.event.ActionEvent;

/** CREDIT: https://alvinalexander.com/java/java-undo-redo/
 *
 * A class for "undoing an undo" or simply put, redoing an action made by the user
 */
public class RedoAction extends AbstractAction
{
    private final EditorContainer editorContainer;

    private UndoAction undoAction;

    private final UndoManager[] undoManager;

    public RedoAction(EditorContainer editorContainer, UndoManager[] undoManager)
    {
        super("Redo");
        setEnabled(false);

        this.editorContainer = editorContainer;
        this.undoManager = undoManager;
    }

    public void actionPerformed(ActionEvent e)
    {
        if(undoAction == null)
            return;

        int tabIndex = editorContainer.getTabbedPane().getSelectedIndex();

        try
        {
            undoManager[tabIndex].redo();
            editorContainer.checkTextForChanges();
        }
        catch (CannotRedoException ex)
        {
            // TODO deal with this
            ex.printStackTrace();
        }
        update();
        undoAction.update();
    }

    public void setUndoAction(UndoAction undoAction) {
        this.undoAction = undoAction;
    }

    protected void update()
    {
        int tabIndex = editorContainer.getTabbedPane().getSelectedIndex();

        if (undoManager[tabIndex].canRedo())
        {
            setEnabled(true);
            putValue(Action.NAME, undoManager[tabIndex].getRedoPresentationName());
        }
        else
        {
            setEnabled(false);
            putValue(Action.NAME, "Redo");
        }
    }
}
