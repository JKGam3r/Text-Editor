package main.actions;

import main.gui.EditorContainer;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

/**
 * Controls what happens when the user interacts with a key.  More specifically,
 * this class should tell the text editor what to do upon a key being typed,
 * pressed, or released.
 *
 * @author Justin Kocur
 */
public class EditorKeyActions implements KeyListener {

    /** The key responsible for closing a tab */
    public static final int CLOSE_TAB_KEY = KeyEvent.VK_W;

    /** The key responsible for copying highlighted text */
    public static final int COPY_TEXT_KEY = KeyEvent.VK_C;

    /** Used for binding keys to the CONTROL+ALT keys */
    public static final int CTRL_ALT_BINDING = KeyEvent.CTRL_DOWN_MASK | KeyEvent.ALT_DOWN_MASK;

    /** Used for binding keys to the CONTROL key */
    public static final int CTRL_BINDING = KeyEvent.CTRL_DOWN_MASK;

    /** Used for binding keys to the CONTROL+SHIFT keys */
    public static final int CTRL_SHIFT_BINDING = KeyEvent.CTRL_DOWN_MASK | KeyEvent.SHIFT_DOWN_MASK;

    /** The key responsible for cutting (copy + remove) text */
    public static final int CUT_TEXT_KEY = KeyEvent.VK_X;

    /** The key responsible for duplicating the current line the caret is located on */
    public static final int DUPLICATE_LINE_KEY = KeyEvent.VK_D;

    /** The key responsible for finding a user-typed phrase */
    public static final int FIND_PHRASE_KEY = KeyEvent.VK_F;

    /** The key responsible for joining the current line (holding caret) with the line below */
    public static final int JOIN_LINES_KEY = KeyEvent.VK_J;

    /** The key responsible for creating a new tab */
    public static final int NEW_TAB_KEY = KeyEvent.VK_T;

    /** The key responsible for opening a file */
    public static final int OPEN_FILE_KEY = KeyEvent.VK_O;

    /** The key responsible for pasting previously copied text */
    public static final int PASTE_TEXT_KEY = KeyEvent.VK_V;

    /** The key responsible for printing text on a text pane */
    public static final int PRINT_KEY = KeyEvent.VK_P;

    /** The key responsible for replacing a user-typed phrase with another user-typed phrase */
    public static final int REPLACE_PHRASE_KEY = KeyEvent.VK_R;

    /** The key responsible for redoing an action */
    public static final int REDO_ACTION_KEY = KeyEvent.VK_Z;

    /** The key responsible for saving a file */
    public static final int SAVE_FILE_KEY = KeyEvent.VK_S;

    /** The key responsible for selecting all text in a text pane */
    public static final int SELECT_ALL_KEY = KeyEvent.VK_A;

    /** The key responsible for undoing an action */
    public static final int UNDO_ACTION_KEY = KeyEvent.VK_Z;

    // The main GUI application for the text editor
    private final EditorContainer editorContainer;

    /**
     *  Constructs a new EditorKeyActions class instance
     *
     * @param editorContainer  the main GUI application for the text editor
     */
    public EditorKeyActions(EditorContainer editorContainer) {
        this.editorContainer = editorContainer;
    }

    @Override
    public void keyPressed(KeyEvent e) {
        int key = e.getKeyCode();

        if(key == KeyEvent.VK_ESCAPE) {
            int tabIndex = editorContainer.getTabbedPane().getSelectedIndex();
            editorContainer.getSearchBox().showSearchBox(tabIndex, false);
            editorContainer.getReplaceBox().showReplaceBox(tabIndex, false);
            editorContainer.resetTextPaneFocus();
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        editorContainer.checkTextForChanges();

        int tabIndex = editorContainer.getTabbedPane().getSelectedIndex();

        if(editorContainer.getSearchBox().isTextChanged(tabIndex)
                || isArrowKey(e.getKeyCode())) {
            if(editorContainer.getSearchBox().isVisible())
                editorContainer.getSearchBox().rehighlight(true);
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {

    }

    private boolean isArrowKey(int key) {
        return key == KeyEvent.VK_DOWN
                || key == KeyEvent.VK_KP_DOWN
                || key == KeyEvent.VK_LEFT
                || key == KeyEvent.VK_KP_LEFT
                || key == KeyEvent.VK_UP
                || key == KeyEvent.VK_KP_UP
                || key == KeyEvent.VK_RIGHT
                || key == KeyEvent.VK_KP_RIGHT;
    }
}
