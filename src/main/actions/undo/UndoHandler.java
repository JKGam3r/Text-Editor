package main.actions.undo;

import main.gui.EditorContainer;

import javax.swing.event.UndoableEditEvent;
import javax.swing.event.UndoableEditListener;
import javax.swing.undo.UndoManager;

/** CREDIT: https://alvinalexander.com/java/java-undo-redo/
 *
 * Handles the undo/ redo actions
 */
public class UndoHandler implements UndoableEditListener
{
    private final EditorContainer editorContainer;

    private RedoAction redoAction;

    private UndoAction undoAction;

    private final UndoManager[] undoManager;

    public UndoHandler(EditorContainer editorContainer, UndoManager[] undoManager) {
        this.editorContainer = editorContainer;
        this.undoManager = undoManager;
    }

    public void setRedoAction(RedoAction redoAction) {
        this.redoAction = redoAction;
    }

    public void setUndoAction(UndoAction undoAction) {
        this.undoAction = undoAction;
    }

    /**
     * Messaged when the Document has created an edit, the edit is added to
     * <code>undoManager</code>, an instance of UndoManager.
     */
    public void undoableEditHappened(UndoableEditEvent e)
    {
        if(undoAction == null || redoAction == null)
            return;

        int tabIndex = editorContainer.getTabbedPane().getSelectedIndex();

        undoManager[tabIndex].addEdit(e.getEdit());
        undoAction.update();
        redoAction.update();
    }
}
