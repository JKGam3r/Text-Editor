package main.gui;

import main.actions.EditorActions;
import main.actions.EditorKeyActions;
import main.actions.undo.RedoAction;
import main.actions.undo.UndoAction;
import main.actions.undo.UndoHandler;
import main.data.ColorData;
import main.data.FileData;
import main.editor.Editor;
import main.gui.components.*;
import main.gui.components.boxcomponents.FillerBox;
import main.gui.components.boxcomponents.ReplaceBox;
import main.gui.components.boxcomponents.SearchBox;
import main.gui.components.fileinfo.FileTree;
import main.gui.components.textpanecomponents.TextLineNumber;
import main.gui.components.textpanecomponents.caretpos.*;
import main.gui.components.settings.SettingsMenu;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.text.*;
import javax.swing.undo.UndoManager;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.util.ListIterator;

/**
 * Essentially the "parent" for all other GUI components used for this text editor.
 * All GUI aspects (e.g. search box) are placed in separate classes and called here.
 *
 * @author Justin Kocur
 */
public class EditorContainer {
    // Responsible for changing text editor colors
    private final ColorData colorData;

    /* Displays a variety of commonly accessed commands; should be a
       mouse right click event to access (Windows) */
    private final ContextMenu contextMenu;

    // The actions that the user can perform on the text editor
    private final EditorActions editorActions;

    // The actions that should happen upon a key event
    private final EditorKeyActions editorKeyActions;

    // Contains all parts of the menu
    private final EditorMenuBar menuBar;

    // The data provided for each of the files open in the editor
    private final FileData fileData;

    // Provides the nodes (files and folders) that the user can access
    private final FileTree fileTree;

    private final FillerBox fillerBox;

    // Used for displaying overlapping components
    private final JLayeredPane layeredPane;

    /* The main JFrame that will hold all components, both interactive and static,
       for the text editor */
    private final JFrame mainFrame;

    // The maximum number of tabs this text editor will allow open at one time
    private final int MAX_TABS;

    private final RedoAction redoAction;

    // Displays a combination of components responsible for replacing a phrase in the text pane
    private final ReplaceBox replaceBox;

    // The search box associated with this text editor
    private final SearchBox searchBox;

    /* Allows the user to select a portion of the text in a text pane.
       Only one instance is needed for the whole text editor, as the select
       box works on the currently selected tab */
    private final SelectBoundsBox selectBoundsBox;

    // The SettingsMenu instance responsible for changing different features of the text editor
    private final SettingsMenu settingsMenu;

    // The standard font used in the text editor
    private final Font standardFont;

    // Contains all the tabs for the editor
    private final EditorTabbedPane tabbedPane;

    // Holds the actual text from each file
    private final EditorTextPane[] textPanes;

    // The font used for the text panes
    private final Font textPaneFont;

    private final UndoAction undoAction;

    private final UndoHandler undoHandler;

    private final UndoManager[] undoManager;

    /**
     * Sets up the text editor with its required components
     *
     * @param MAX_TABS              the maximum number of tabs allowed open in the editor at once
     * @param INITIAL_WIDTH         the initial width of the application
     * @param INITIAL_HEIGHT        the initial height of the application
     * @param fileData              the data provided for each of the files open in the editor
     */
    public EditorContainer(final int MAX_TABS, final int INITIAL_WIDTH, final int INITIAL_HEIGHT,
                           FileData fileData) {
        // Set look and feel before doing anything else
        try {
            UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
        }

        // No insets for tabbed panes
        UIManager.getDefaults().put("TabbedPane.contentBorderInsets", new Insets(0,0,0,0));
        UIManager.getDefaults().put("TabbedPane.tabsOverlapBorder", true);

        this.MAX_TABS = MAX_TABS;
        this.fileData = fileData;

        colorData = new ColorData(this);

        standardFont = new Font("dialog", Font.PLAIN, 12);
        textPaneFont = new Font("monospaced", Font.PLAIN, 14);

        // Create the main JFrame
        mainFrame = new JFrame();
        constructMainFrame(INITIAL_WIDTH, INITIAL_HEIGHT);

        layeredPane = new JLayeredPane();

        undoManager = new UndoManager[MAX_TABS];
        undoHandler = new UndoHandler(this, undoManager);
        undoAction  = new UndoAction(this, undoManager);
        redoAction  = new RedoAction(this, undoManager);

        editorActions       = new EditorActions(MAX_TABS, this);
        editorKeyActions    = new EditorKeyActions(this);

        menuBar = new EditorMenuBar();

        constructMenuSystem();

        textPanes = new EditorTextPane[MAX_TABS];

        // Construct the search box
        searchBox       = new SearchBox(this, MAX_TABS, null);
        replaceBox      = new ReplaceBox(this, MAX_TABS, null);
        fillerBox       = new FillerBox();
        selectBoundsBox = new SelectBoundsBox(this);
        contextMenu     = new ContextMenu(this);

        settingsMenu = new SettingsMenu(this);

        tabbedPane = new EditorTabbedPane();

        constructTabbedPane();

        editorActions.newFunctionality();

        Rectangle tabbedBounds = getTabbedPane().getBoundsAt(0);
        searchBox.setPosition((int) tabbedBounds.getX(), (int) (tabbedBounds.getY() + tabbedBounds.getHeight() - 1));
        replaceBox.setPosition((int) tabbedBounds.getX(), (int) (tabbedBounds.getY() + tabbedBounds.getHeight() - 1));
        fillerBox.setPosition((int) tabbedBounds.getX(), (int) (tabbedBounds.getY() + tabbedBounds.getHeight() - 1));

        // Add components
        layeredPane.add(tabbedPane, Integer.valueOf(1));
        layeredPane.add(searchBox, Integer.valueOf(2));
        layeredPane.add(replaceBox, Integer.valueOf(2));
        layeredPane.add(fillerBox, Integer.valueOf(2));

        fileTree = new FileTree(editorActions, new File(""), this);

        mainFrame.add(fileTree.getScrollableTree(), BorderLayout.WEST);
        mainFrame.add(layeredPane);

        //colorData.setStyle(ColorData.HALLOWEEN);

        mainFrame.setVisible(true);
    }

    /**
     *  Returns the EditorActions instance associated with this text editor
     *
     * @return  the EditorActions instance associated with this text editor
     */
    public EditorActions getEditorActions() {
        return editorActions;
    }

    /**
     *  Returns the FileData instance associated with this text editor
     *
     * @return  the FileData instance associated with this text editor
     */
    public FileData getFileData() {
        return fileData;
    }

    /**
     *  Returns the FileTree instance associated with this text editor
     *
     * @return  the FileTree instance associated with this text editor
     */
    public FileTree getFileTree() {
        return fileTree;
    }

    /**
     *  Returns the FillerBox instance associated with this text editor
     *
     * @return  the FillerBox instance associated with this text editor
     */
    public FillerBox getFillerBox() {
        return fillerBox;
    }

    /**
     *  Returns the JFrame associated with this text editor
     *
     * @return  the JFrame associated with this text editor
     */
    public JFrame getMainFrame() {
        return mainFrame;
    }

    /**
     *  Holds all menu options
     *
     * @return  the menu bar associated with this text editor
     */
    public EditorMenuBar getMenuBar() {
        return menuBar;
    }

    /**
     *  Returns the replace box associated with this text editor
     *
     * @return  the replace box associated with this text editor
     */
    public ReplaceBox getReplaceBox() {
        return replaceBox;
    }

    /**
     *  Returns the search box associated with this text editor
     *
     * @return  the search box associated with this text editor
     */
    public SearchBox getSearchBox() {
        return searchBox;
    }

    /**
     *  Returns the SelectBoundsBox instance associated with this text editor
     *
     * @return  the SelectBoundsBox instance associated with this text editor
     */
    public SelectBoundsBox getSelectBoundsBox() {
        return selectBoundsBox;
    }

    /**
     *  Returns the SettingsMenu instance associated with this text editor
     *
     * @return  the SettingsMenu instance associated with this text editor
     */
    public SettingsMenu getSettingsMenu() {
        return settingsMenu;
    }

    /**
     *  Returns the standard font for this text editor
     *
     * @return  returns the standard font for this text editor
     */
    public Font getStandardFont() {
        return standardFont;
    }

    /**
     *  Returns the tabbed pane associated with this text editor
     *
     * @return  the tabbed pane associated with this text editor
     */
    public EditorTabbedPane getTabbedPane() {
        return tabbedPane;
    }

    /**
     *  Returns a text pane from the text pane array
     *
     * @param index     the index at which to retrieve a text pane
     * @return          the designated text pane at the given index
     */
    public EditorTextPane getTextPane(int index) {
        if(index < 0 || index >= fileData.numTabsOpen())
            return null;

        return textPanes[index];
    }

    /**
     *  Sets the title for this JFrame
     *
     * @param name          the name to set for this text editor
     * @param changed       true if changed file (denoted with '*' after file name), false if no change
     */
    public void setFrameTitle(String name, boolean changed) {
        if(changed)
            mainFrame.setTitle(String.format("%s* - %s", name, Editor.EDITOR_NAME));
        else
            mainFrame.setTitle(String.format("%s - %s", name, Editor.EDITOR_NAME));
    }

    /**
     * Updates the String name of the JFrame container and currently selected tab
     * based on whether or not changes were made to the text.
     */
    public void checkTextForChanges() {
        int index = tabbedPane.getSelectedIndex();

        if(index < 0 || index >= MAX_TABS)
            return;

        if(textPanes[index] == null)
            return;

        String ogText       = fileData.getTabOgText(index);
        String currentText  = textPanes[index].getText();
        String name         = fileData.getTabName(index);

        // Compares the last saved text to the current state of the text on the text pane
        if(!ogText.equals(currentText)) {
            setFrameTitle(name, true);
            tabbedPane.setTitleAt(index, name + "*");
        } else {
            setFrameTitle(name, false);
            tabbedPane.setTitleAt(index, name);
        }
    }

    public void removeTextPane(int index) {
        if(index < 0 || index >= MAX_TABS)
            return;

        // Shift all content on right of removed to the left
        for(int i = index; i < MAX_TABS; i++) {
            if(i + 1 >= MAX_TABS) {
                textPanes[i] = null;
            } else {
                textPanes[i] = textPanes[i + 1];
            }
        }
    }

    /**
     * Constructs a new text pane with specified characteristics
     *
     * @param index     the index at which to add the text pane into the tabbed pane
     */
    public void constructTextPane(int index) {
        // The text pane to return
        EditorTextPane textPane = new EditorTextPane();

        // Create undo/ redo functionality
        constructUndoAction(index);

        // Set defined features for this text pane
        textPane.setMargin(new Insets(0, 5, 150, 5));
        textPane.setFont(textPaneFont);
        textPane.addKeyListener(editorKeyActions);
        textPane.addMouseListener(new MouseAdapter() {
            boolean rehighlightText = true;

            @Override
            public void mouseClicked(MouseEvent e) {
                if(SwingUtilities.isRightMouseButton(e)) {
                    contextMenu.show(textPane, e.getX(), e.getY());
                }
            }

            @Override
            public void mousePressed(MouseEvent e) {
                // Only highlight once if user clicks in text pane and search box is still visible
                if(searchBox.isVisible())
                    if(rehighlightText) {
                        searchBox.rehighlight(true);
                        rehighlightText = false;
                    }
                    else
                        rehighlightText = true;

                if(!searchBox.isFocusOwner())
                    rehighlightText = true;
            }
        });

        // CREDIT: https://itqna.net/questions/25520/change-size-tab-spacing-jtextpane
        textPane.setEditorKit(new StyledEditorKit() {
            public static final int TAB_SIZE = 10;

            @Override
            public ViewFactory getViewFactory() {
                return new MyViewFactory();
            }

            class MyViewFactory implements ViewFactory {

                @Override
                public View create(Element elem) {
                    String kind = elem.getName();
                    if (kind != null) {
                        if (kind.equals(AbstractDocument.ContentElementName)) {
                            return new LabelView(elem);
                        } else if (kind.equals(AbstractDocument.ParagraphElementName)) {
                            return new CustomTabParagraphView(elem);
                        } else if (kind.equals(AbstractDocument.SectionElementName)) {
                            return new BoxView(elem, View.Y_AXIS);
                        } else if (kind.equals(StyleConstants.ComponentElementName)) {
                            return new ComponentView(elem);
                        } else if (kind.equals(StyleConstants.IconElementName)) {
                            return new IconView(elem);
                        }
                    }

                    return new LabelView(elem);
                }
            }


            class CustomTabParagraphView extends ParagraphView {

                public CustomTabParagraphView(Element elem) {
                    super(elem);
                }

                @Override
                public float nextTabStop(float x, int tabOffset) {
                    TabSet tabs = getTabSet();
                    if(tabs == null) {
                        // a tab every 72 pixels.
                        return (float) (getTabBase() + (((int)x / TAB_SIZE + 1) * TAB_SIZE));
                    }

                    return super.nextTabStop(x, tabOffset);
                }

            }
        });

        textPane.getDocument().addUndoableEditListener(undoHandler);
        textPane.addDocumentFilter();
        textPane.getActionMap().put("select-word", new WordSelectionAction());
        textPane.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_LEFT,
                EditorKeyActions.CTRL_SHIFT_BINDING), new KeySelectWordAction(KeySelectWordAction.LEFT_ARROW));
        textPane.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_RIGHT,
                EditorKeyActions.CTRL_SHIFT_BINDING), new KeySelectWordAction(KeySelectWordAction.RIGHT_ARROW));
        textPane.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_LEFT,
                EditorKeyActions.CTRL_BINDING), new KeyMoveWordAction(KeyMoveWordAction.LEFT_ARROW));
        textPane.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_RIGHT,
                EditorKeyActions.CTRL_BINDING), new KeyMoveWordAction(KeyMoveWordAction.RIGHT_ARROW));

        // Create a scroller for the text pane
        JScrollPane scrollPane = new JScrollPane(textPane);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(fillerBox.getHeight() - 1, 0, 0, 0));

        TextLineNumber textLineNumber = new TextLineNumber(textPane, mainFrame);
        textPane.setNumberingSystem(textLineNumber);

        scrollPane.setRowHeaderView(textLineNumber);
        scrollPane.getHorizontalScrollBar().setUnitIncrement(16);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);

        tabbedPane.add(fileData.getTabName(index), scrollPane);
        textPanes[index] = textPane;
    }

    /**
     * Resets focus onto the selected tab's text pane
     */
    public void resetTextPaneFocus() {
        EditorTextPane pane = textPanes[tabbedPane.getSelectedIndex()];

        pane.requestFocus();
    }

    /**
     *  Sets the color of the frame's border
     *
     * @param color     the color to change the border to
     */
    public void setFrameBorderColor(Color color) {
        mainFrame.getRootPane().setBorder(BorderFactory
                .createMatteBorder(2, 2, 2, 2, color));
    }

    /**
     * Constructs the container ('mainFrame') for the GUI components
     *
     * @param width     width of the JFrame
     * @param height    height of the JFrame
     */
    private void constructMainFrame(int width, int height) {
        setFrameTitle("Untitled", false);
        mainFrame.getRootPane().setBorder(BorderFactory
                .createMatteBorder(2, 2, 2, 2, new Color(0, 0, 0, 75)));
        mainFrame.setSize(width, height);
        mainFrame.setPreferredSize(new Dimension(width, height));
        mainFrame.setLocationRelativeTo(null);
        mainFrame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        mainFrame.setFocusable(false);
        mainFrame.addKeyListener(editorKeyActions);
        mainFrame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                if(editorActions.saveAllFunctionality())
                    mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            }
        });

        mainFrame.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                int treeWidth = fileTree.getScrollableTree().getWidth();
                tabbedPane.setBounds(0, 0, mainFrame.getWidth() - treeWidth - 15, mainFrame.getHeight() - 55);

                fillerBox.setContainerDimensions(tabbedPane.getWidth(), fillerBox.getHeight());
            }
        });

        mainFrame.addWindowStateListener(new WindowStateListener() {
            @Override
            public void windowStateChanged(WindowEvent e) {
                // Maximized window
                if((e.getNewState() & Frame.MAXIMIZED_BOTH) == Frame.MAXIMIZED_BOTH) {
                    fillerBox.setContainerDimensions((int) Toolkit.getDefaultToolkit().getScreenSize().getWidth(),
                            fillerBox.getHeight());
                }
            }
        });
    }

    /**
     * Constructs the menu from which the user can use the mouse
     * to select actions to perform on the text editor
     */
    private void constructMenuSystem() {
        // Different categories housing the individual options
        JMenu fileMenu  = new JMenu("File");
        JMenu editMenu  = new JMenu("Edit");
        JMenu runMenu   = new JMenu("Run");
        JMenu helpMenu  = new JMenu("Help");

        // Sub categories
        JMenu findOptions           = new JMenu("Find");
        JMenu selectOptions         = new JMenu("Select");
        JMenu toggleCaseOptions     = new JMenu("Toggle Case");

        // The individual items the user can select
        JMenuItem newItem           = new JMenuItem("New");
        JMenuItem newProjectItem    = new JMenuItem("New Project");
        JMenuItem openItem          = new JMenuItem("Open");
        JMenuItem openProjectItem   = new JMenuItem("Open Project");
        JMenuItem openRecentItem    = new JMenuItem("Open Recent");
        JMenuItem settingsItem      = new JMenuItem("Settings...");
        JMenuItem saveItem          = new JMenuItem("Save");
        JMenuItem saveAllItem       = new JMenuItem("Save All");
        JMenuItem saveAsItem        = new JMenuItem("Save As");
        JMenuItem printItem         = new JMenuItem("Print");
        JMenuItem closeTabItem      = new JMenuItem("Close Tab");
        JMenuItem closeEditorItem   = new JMenuItem("Close Editor");

        JMenuItem undoItem          = new JMenuItem("Undo");
        JMenuItem redoItem          = new JMenuItem("Redo");
        JMenuItem cutItem           = new JMenuItem("Cut");
        JMenuItem copyItem          = new JMenuItem("Copy");
        JMenuItem pasteItem         = new JMenuItem("Paste");
        JMenuItem deleteItem        = new JMenuItem("Delete");
        JMenuItem setSelectItem     = new JMenuItem("Begin/End Select");
        JMenuItem selectAllItem     = new JMenuItem("Select All");
        JMenuItem findItem          = new JMenuItem("Find");
        JMenuItem replaceItem       = new JMenuItem("Replace");
        JMenuItem duplicateLineItem = new JMenuItem("Duplicate Line");
        JMenuItem joinLinesItem     = new JMenuItem("Join Lines");
        JMenuItem switchCaseItem    = new JMenuItem("Switch Case");
        JMenuItem uniformCaseItem   = new JMenuItem("Uniform Case");
        JMenuItem alternateCaseItem = new JMenuItem("Alternate Case");

        JMenuItem runProgramItem    = new JMenuItem("Run Program");
        JMenuItem stopProgramItem   = new JMenuItem("Stop Program");
        JMenuItem editConfigItem    = new JMenuItem("Edit Configurations...");

        newItem.setAction(shortcut(newItem.getText(), EditorKeyActions.NEW_TAB_KEY));
        newProjectItem.setAction(shortcut(newProjectItem.getText(), EditorKeyActions.NEW_TAB_KEY, EditorKeyActions.CTRL_SHIFT_BINDING));
        openItem.setAction(shortcut(openItem.getText(), EditorKeyActions.OPEN_FILE_KEY));
        openProjectItem.setAction(shortcut(openProjectItem.getText(), EditorKeyActions.OPEN_FILE_KEY, EditorKeyActions.CTRL_SHIFT_BINDING));
        settingsItem.setAction(shortcut(settingsItem.getText(), KeyEvent.VK_S, EditorKeyActions.CTRL_ALT_BINDING));
        saveItem.setAction(shortcut(saveItem.getText(), EditorKeyActions.SAVE_FILE_KEY));
        printItem.setAction(shortcut(printItem.getText(), EditorKeyActions.PRINT_KEY));
        closeTabItem.setAction(shortcut(closeTabItem.getText(), EditorKeyActions.CLOSE_TAB_KEY));
        closeEditorItem.setAction(shortcut(closeEditorItem.getText(), KeyEvent.VK_W, EditorKeyActions.CTRL_SHIFT_BINDING));

        undoItem.setAction(undoAction);
        redoItem.setAction(redoAction);
        cutItem.setAction(shortcut(cutItem.getText(), EditorKeyActions.CUT_TEXT_KEY));
        copyItem.setAction(shortcut(copyItem.getText(), EditorKeyActions.COPY_TEXT_KEY));
        pasteItem.setAction(shortcut(pasteItem.getText(), EditorKeyActions.PASTE_TEXT_KEY));
        deleteItem.setAction(shortcutSingle(deleteItem.getText(), KeyEvent.VK_DELETE));
        selectAllItem.setAction(shortcut(selectAllItem.getText(), EditorKeyActions.SELECT_ALL_KEY));
        findItem.setAction(shortcut(findItem.getText(), EditorKeyActions.FIND_PHRASE_KEY));
        replaceItem.setAction(shortcut(replaceItem.getText(), EditorKeyActions.REPLACE_PHRASE_KEY));
        duplicateLineItem.setAction(shortcut(duplicateLineItem.getText(), EditorKeyActions.DUPLICATE_LINE_KEY));
        joinLinesItem.setAction(shortcut(joinLinesItem.getText(), EditorKeyActions.JOIN_LINES_KEY));

        fileMenu.add(newItem);
        fileMenu.add(newProjectItem);
        fileMenu.addSeparator();
        fileMenu.add(openItem);
        fileMenu.add(openProjectItem);
        fileMenu.add(openRecentItem);
        fileMenu.addSeparator();
        fileMenu.add(settingsItem);
        fileMenu.addSeparator();
        fileMenu.add(saveItem);
        fileMenu.add(saveAllItem);
        fileMenu.add(saveAsItem);
        fileMenu.addSeparator();
        fileMenu.add(printItem);
        fileMenu.addSeparator();
        fileMenu.add(closeTabItem);
        fileMenu.add(closeEditorItem);

        editMenu.add(undoItem);
        editMenu.add(redoItem);
        editMenu.addSeparator();
        editMenu.add(cutItem);
        editMenu.add(copyItem);
        editMenu.add(pasteItem);
        editMenu.add(deleteItem);
        editMenu.addSeparator();
        selectOptions.add(setSelectItem);
        selectOptions.add(selectAllItem);
        editMenu.add(selectOptions);
        editMenu.addSeparator();
        findOptions.add(findItem);
        findOptions.add(replaceItem);
        editMenu.add(findOptions);
        editMenu.addSeparator();
        editMenu.add(duplicateLineItem);
        editMenu.add(joinLinesItem);
        toggleCaseOptions.add(switchCaseItem);
        toggleCaseOptions.add(uniformCaseItem);
        toggleCaseOptions.add(alternateCaseItem);
        editMenu.add(toggleCaseOptions);

        runMenu.add(runProgramItem);
        runMenu.add(stopProgramItem);
        runMenu.add(editConfigItem);

        setMenuFont(fileMenu, standardFont);
        setMenuFont(editMenu, standardFont);
        setMenuFont(runMenu, standardFont);
        setMenuFont(helpMenu, standardFont);
        setMenuFont(findOptions, standardFont);
        setMenuFont(selectOptions, standardFont);
        setMenuFont(toggleCaseOptions, standardFont);

        setMenuActionListener(fileMenu, editorActions);
        setMenuActionListener(editMenu, editorActions);
        setMenuActionListener(runMenu, editorActions);
        setMenuActionListener(helpMenu, editorActions);
        setMenuActionListener(findOptions, editorActions);
        setMenuActionListener(selectOptions, editorActions);
        setMenuActionListener(toggleCaseOptions, editorActions);

        menuBar.add(fileMenu);
        menuBar.add(editMenu);
        menuBar.add(runMenu);
        menuBar.add(helpMenu);

        mainFrame.setJMenuBar(menuBar);
    }

    /**
     * Creates defined properties for the tabbed pane containing all text panes
     */
    private void constructTabbedPane() {
        tabbedPane.setEditorContainer(this);
        tabbedPane.setFocusable(false);
        tabbedPane.addChangeListener(new ChangeListener() {
            // The tab that was previously selected/ being looked at
            int prevTab = 0;

            @Override
            public void stateChanged(ChangeEvent e) {
                if(tabbedPane.getSelectedIndex() >= 0 && tabbedPane.getSelectedIndex() < MAX_TABS) {
                    int tabIndex = tabbedPane.getSelectedIndex();

                    searchBox.setCurrentSearch(prevTab);
                    replaceBox.setCurrentReplace(prevTab);
                    prevTab = tabIndex;

                    searchBox.setTab(tabIndex);
                    searchBox.setTextPane(textPanes[tabIndex]);
                    replaceBox.setTab(tabIndex);
                    replaceBox.setTextPane(textPanes[tabIndex]);

                    fillerBox.setShownText(fileData.getTabPath(tabIndex));

                    if(textPanes[tabIndex] != null)
                        textPanes[tabIndex].requestFocus();

                    checkTextForChanges();
                }
            }
        });
    }

    /**
     * Defines designated properties to get the undo/ redo functions working
     *
     * @param tabIndex  the index at which to create a new undo/ redo action
     */
    private void constructUndoAction(int tabIndex) {
        undoManager[tabIndex] = new UndoManager();

        undoHandler.setUndoAction(undoAction);
        undoHandler.setRedoAction(redoAction);
        undoAction.setRedoAction(redoAction);
        redoAction.setUndoAction(undoAction);
        undoAction.putValue(Action.ACCELERATOR_KEY,
                KeyStroke.getKeyStroke(EditorKeyActions.UNDO_ACTION_KEY,
                        EditorKeyActions.CTRL_BINDING));
        redoAction.putValue(Action.ACCELERATOR_KEY,
                KeyStroke.getKeyStroke(EditorKeyActions.REDO_ACTION_KEY,
                        EditorKeyActions.CTRL_SHIFT_BINDING));
    }

    /**
     * Adds an EditorActions instance to each component in the provided JMenu instance
     *
     * @param menu              the menu to add the EditorActions instance to its components
     * @param editorActions     added to each JMenuItem in the JMenu instance; provides user interaction
     */
    private void setMenuActionListener(JMenu menu, EditorActions editorActions) {
        for(Component c : menu.getMenuComponents()) {
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
    private void setMenuFont(JMenu menu, Font font) {
        // Change the category font
        menu.setFont(font);

        // Change the font of each component
        for(Component c : menu.getMenuComponents()) {
            c.setFont(font);
        }
    }

    /**
     *  Creates a shortcut key that will link to a provided action
     *
     * @param command   the name of the action
     * @param key       the key used to execute the action
     * @return          the action associated
     */
    private Action shortcut(String command, int key) {
        AbstractAction action = new AbstractAction(command) {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.printf("'%s' functionality performed.%n", e.getActionCommand());
            }
        };

        action.putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(key, KeyEvent.CTRL_DOWN_MASK));

        return action;
    }

    /**
     *  Creates a shortcut key that will link to a provided action
     *
     * @param command   the name of the action
     * @param key       the key used to execute the action
     * @param mask      the masking key(s) that must be pressed in addition to the 'key'
     * @return          the action associated
     */
    private Action shortcut(String command, int key, int mask) {
        AbstractAction action = new AbstractAction(command) {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.printf("'%s' functionality performed.%n", e.getActionCommand());
            }
        };

        action.putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(key, mask));

        return action;
    }

    /**
     *  Creates a shortcut key that will link to a provided action, only using
     *  the key provided by 'key'
     *
     * @param command   the name of the action
     * @param key       the key used to execute the action
     * @return          the action associated
     */
    private Action shortcutSingle(String command, int key) {
        AbstractAction action = new AbstractAction(command) {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.printf("'%s' functionality performed.%n", e.getActionCommand());
            }
        };

        action.putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke((char) key));

        return action;
    }
}