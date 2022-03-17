package main.actions.undo;

import main.gui.EditorContainer;

import javax.swing.*;
import javax.swing.undo.CannotUndoException;
import javax.swing.undo.UndoManager;
import java.awt.event.ActionEvent;

/** CREDIT: https://alvinalexander.com/java/java-undo-redo/
 *
 * A class for undoing an action that the user has done.
 */
public class UndoAction extends AbstractAction
{
    private final EditorContainer editorContainer;

    private RedoAction redoAction;

    private final UndoManager[] undoManager;

    public UndoAction(EditorContainer editorContainer, UndoManager[] undoManager)
    {
        super("Undo");
        setEnabled(false);

        this.editorContainer = editorContainer;
        this.undoManager = undoManager;
    }

    public void actionPerformed(ActionEvent e)
    {
        if(redoAction == null)
            return;

        int tabIndex = editorContainer.getTabbedPane().getSelectedIndex();

        try
        {
            undoManager[tabIndex].undo();
            editorContainer.checkTextForChanges();
        }
        catch (CannotUndoException ex)
        {
            // TODO deal with this
            //ex.printStackTrace();
        }
        update();
        redoAction.update();
    }

    public void setRedoAction(RedoAction redoAction) {
        this.redoAction = redoAction;
    }

    protected void update()
    {
        int tabIndex = editorContainer.getTabbedPane().getSelectedIndex();

        if (undoManager[tabIndex].canUndo())
        {
            setEnabled(true);
            putValue(Action.NAME, undoManager[tabIndex].getUndoPresentationName());
        }
        else
        {
            setEnabled(false);
            putValue(Action.NAME, "Undo");
        }
    }
}
