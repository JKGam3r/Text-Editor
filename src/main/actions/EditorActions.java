package main.actions;

import main.data.LineData;
import main.editor.Editor;
import main.gui.EditorContainer;
import main.gui.components.fileinfo.FileSelector;
import main.gui.components.PopUp;
import main.gui.components.fileinfo.FileTree;

import javax.swing.*;
import javax.swing.text.BadLocationException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.print.PrinterException;
import java.io.*;
import java.nio.file.Files;

/**
 * Contains the actions (e.g. create new tab, save as, copy, paste) that are
 * available to the user
 *
 * @author Justin Kocur
 */
public class EditorActions implements ActionListener {
    Thread thread;

    // Container for text editor used for additional functionality
    private final EditorContainer editorContainer;

    // The maximum number of tabs allowed open in the editor
    private final int MAX_TABS;

    /**
     * Constructs a new EditorActions instance
     *
     * @param MAX_TABS          the max number of tabs allowed open at once
     * @param editorContainer   contains GUI contents for the text editor
     */
    public EditorActions(final int MAX_TABS, EditorContainer editorContainer) {
        this.MAX_TABS = MAX_TABS;
        this.editorContainer = editorContainer;
    }

    /**
     *  Triggers the methods containing the actual actions to take place.  The order
     *  for the cases go by their position in the menu system
     *
     *  @param e     ActionEvent instance used to point to which action to utilize
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        // The tab in the text editor that the user is currently looking at
        int index = editorContainer.getTabbedPane().getSelectedIndex();

        switch(e.getActionCommand()) {
            case "New":
                newFunctionality();
                break;

            case "New Project":
                newProjectFunctionality();
                break;

            case "Open":
                openFunctionality(null);
                break;

            case "Open Project":
                openProjectFunctionality();
                break;

            case "Open Recent":
                openRecentFunctionality();
                break;

            case "Settings...":
                settingsFunctionality();
                break;

            case "Save":
                if(editorContainer.getFileData().getTabPath(index) != null)
                    saveFunctionality(index);
                else
                    saveAsFunctionality(index);
                break;

            case "Save All":
                saveAllFunctionality();
                break;

            case "Save As":
                saveAsFunctionality(index);
                break;

            case "Print":
                try {
                    editorContainer.getTextPane(index).print();
                } catch(PrinterException p) {
                    PopUp.displayErrorMessage(editorContainer.getMainFrame(), p.getMessage());
                }
                break;

            case "Close Tab":
                if(editorContainer.getFileData().numTabsOpen() > 0)
                    closeTabFunctionality(index);
                else
                    closeEditorFunctionality();
                break;

            case "Close Editor":
                closeEditorFunctionality();
                break;

            case "Cut":
                editorContainer.getTextPane(index).cut();
                editorContainer.checkTextForChanges();
                break;

            case "Copy":
                editorContainer.getTextPane(index).copy();
                break;

            case "Paste":
                editorContainer.getTextPane(index).paste();
                editorContainer.checkTextForChanges();
                break;

            case "Delete":
                deleteFunctionality(index);
                break;

            case "Begin/End Select":
                beginEndFunctionality();
                break;

            case "Select All":
                editorContainer.getTextPane(index).selectAll();
                break;

            case "Find":
                findFunctionality(index);
                break;

            case "Replace":
                replaceFunctionality(index);
                break;

            case "Duplicate Line":
                duplicateLineFunctionality(index);
                break;

            case "Join Lines":
                joinLinesFunctionality(index);
                break;

            case "Switch Case":
                switchCaseFunctionality(index);
                break;

            case "Uniform Case":
                uniformCaseFunctionality(index);
                break;

            case "Alternate Case":
                alternateCaseFunctionality(index);
                break;

            case "Run Program":
                runProgramFunctionality(index);
                break;

            case "Stop Program":
                stopProgramFunctionality(index);
                break;

            case "Edit Configurations...":
                break;
        }
    }

    /**
     *  Changes the case (lower vs. UPPER).  The text will alternate from lower-UPPER-lower
     *  or vice versa, depending on the state (lower vs. UPPER) of the first character
     *  to switch.  Only switches English alphabet characters.
     *
     * @param index     position in the tabbed pane
     */
    public void alternateCaseFunctionality(int index) {
        // Alternate case on selected text
        if(editorContainer.getTextPane(index).getSelectedText() != null) {
            String modText  = createAlternateCaseString(editorContainer.getTextPane(index).getSelectedText());
            int selStart    = editorContainer.getTextPane(index).getSelectionStart();
            int selEnd      = editorContainer.getTextPane(index).getSelectionEnd();

            try {
                editorContainer.getTextPane(index).getStyledDocument().remove(selStart, selEnd - selStart);
                editorContainer.getTextPane(index).getStyledDocument().insertString(selStart, modText, null);
            } catch (BadLocationException e) {
                e.printStackTrace();
            }

            // Re-select text
            editorContainer.getTextPane(index).setSelectionStart(selStart);
            editorContainer.getTextPane(index).setSelectionEnd(selEnd);
        }

        // Alternate case on word
        else {
            String text             = editorContainer.getTextPane(index).getText();
            int caretPos            = editorContainer.getTextPane(index).getCaretPosition();
            int start               = findCaretStart(text, caretPos);
            int end                 = findCaretEnd(text, caretPos);

            try {
                String modText = createAlternateCaseString(text.substring(start, end));

                editorContainer.getTextPane(index).getStyledDocument().remove(start, end - start);
                editorContainer.getTextPane(index).getStyledDocument().insertString(start, modText, null);
            } catch (BadLocationException e) {
                e.printStackTrace();
            }

            // Re-position caret
            editorContainer.getTextPane(index).setCaretPosition(caretPos);
            editorContainer.getTextPane(index).getCaret().setVisible(true);
        }
    }

    /**
     * Calls a designated function to select a portion of the text
     * between the bounds described by the user
     */
    public void beginEndFunctionality() {
        editorContainer.getSelectBoundsBox().showSelectionBox(true);
    }

    /**
     * Closes the whole editor
     */
    public void closeEditorFunctionality() {
        if(saveAllFunctionality())
            editorContainer.getMainFrame().dispose();
    }

    /**
     *  Closes the tab that is currently selected/ being looked at
     *
     * @param index     the tab index at which to close the tab
     */
    public void closeTabFunctionality(int index) {
        // No tabs currently open
        if(editorContainer.getFileData().numTabsOpen() <= 0)
            return;

        String ogText               = editorContainer.getFileData().getTabOgText(index);
        String currentText          = editorContainer.getTextPane(index).getText();
        boolean closeTab            = true;

        // Check if any changes were made to the file since last save
        if (!ogText.equals(currentText)) {
            int p = PopUp.displayConfirmMessage(editorContainer.getMainFrame(),
                    "Save before closing tab?");

            if (p == PopUp.OK_OPTION) {
                if (editorContainer.getFileData().getTabPath(index) == null)
                    closeTab = saveAsFunctionality(index);
                else
                    closeTab = saveFunctionality(index);
            } else if(p != PopUp.NO_OPTION) {
                closeTab = false;
            }
        }

        if(closeTab) {
            editorContainer.getSearchBox().removeSearchBoxAtIndex(index);
            editorContainer.getReplaceBox().removeReplaceBoxAtIndex(index);
            editorContainer.getFileData().removeTabData(index);
            editorContainer.getTabbedPane().remove(index);
            editorContainer.removeTextPane(index);
            editorContainer.checkTextForChanges();
        }
    }

    /**
     * Deletes a portion of the text designated by the user
     *
     * @param index     the index at which the text pane is located in the tabbed pane
     */
    public void deleteFunctionality(int index) {
        int start   = editorContainer.getTextPane(index).getSelectionStart();
        int length  = editorContainer.getTextPane(index).getSelectionEnd() - start;

        try {
            editorContainer.getTextPane(index).getDocument().remove(start, length);
        } catch (BadLocationException e) {
            e.printStackTrace();
        }
    }

    /**
     *  Uses the LineData class to duplicate user-highlighted text, or
     *  the line the cursor is currently on if there is no highlighted text
     *
     * @param index     the index at which to duplicate text
     */
    public void duplicateLineFunctionality(int index) {
        LineData.duplicateLine(editorContainer.getTextPane(index));
    }

    /**
     * Shows a pop-up for a find bar.  The functionality in this method toggles
     * the search bar on and off, such that it is visible and hidden upon
     * toggling it.
     */
    public void findFunctionality(int index) {
        editorContainer.getSearchBox().showSearchBox(index, true);
        editorContainer.getReplaceBox().showReplaceBox(index, false);
    }

    /**
     *  Joins the current line that the caret is positioned on with the
     *  line below it.  Doesn't do anything if the line the caret is on
     *  is the very last line.
     *
     * @param index     the index in the tabbed pane
     */
    public void joinLinesFunctionality(int index) {
        LineData.joinLines(editorContainer.getTextPane(index));
    }

    /**
     * Creates a new tab for the text editor
     */
    public void newFunctionality() {
        if(editorContainer.getFileData().numTabsOpen() < MAX_TABS) {
            // Index set to current number of tabs open since new tab not created yet
            int index = editorContainer.getFileData().numTabsOpen();

            editorContainer.getFileData().addTabData();
            editorContainer.constructTextPane(index);
            editorContainer.setFrameTitle(editorContainer.getFileData().getTabName(index), false);
            editorContainer.getTabbedPane().setSelectedIndex(index);
        }
    }

    public void newProjectFunctionality() {

    }

    /**
     * Creates a new tab in the editor with text from a previously saved file
     */
    public void openFunctionality(String path) {
        FileSelector selector = new FileSelector(editorContainer.getFileData().getLastOpenPath());
        int r;

        if(path == null || path.equals(""))
            r = selector.showOpenDialog(null);
        else
            r = FileSelector.APPROVE_OPTION;

        if(r == FileSelector.APPROVE_OPTION) {
            File file;
            if(path == null || path.equals(""))
                file = selector.getSelectedFile();
            else
                file = new File(path);

            try(BufferedReader reader = new BufferedReader(new FileReader(file))) {

                StringBuilder builder   = new StringBuilder();
                String currentLine      = reader.readLine();
                int index               = editorContainer.getTabbedPane().getSelectedIndex();

                // Ask user if he/ she wants the file open in a new tab
                int option = PopUp.displayConfirmMessage(editorContainer.getMainFrame(), "Open in new tab?");
                if(option == PopUp.OK_OPTION) {

                    // No tabs open or max tab limit reached
                    if(editorContainer.getFileData().numTabsOpen() <= 0 || editorContainer.getFileData().numTabsOpen() >= MAX_TABS) {
                        PopUp.displayStandardMessage(editorContainer.getMainFrame(), "Too many tabs open.");
                        return;
                    }

                    newFunctionality();
                    index = editorContainer.getTabbedPane().getSelectedIndex();
                }

                // User cancelled open functionality
                if(option != PopUp.OK_OPTION && option != PopUp.NO_OPTION)
                    return;

                // Read all text from given file
                builder.append(currentLine.replaceAll("\n", ""));
                while((currentLine = reader.readLine()) != null) {
                    builder.append('\n');
                    builder.append(currentLine.replaceAll("\n", ""));
                }

                editorContainer.getTextPane(index).setText(builder.toString());
                editorContainer.getFileData().setLastOpenPath(file.getAbsolutePath());

                // Set appropriate data for FileData instance
                editorContainer.getFileData().setTabName(index, file.getName());
                editorContainer.getFileData().setTabPath(index, file.getAbsolutePath());
                editorContainer.getFileData().setTabOgText(index, builder.toString());
                editorContainer.getFileData().setOpenedFromDir(index, true);

                editorContainer.getFillerBox().setShownText(file.getAbsolutePath());

                // Set appropriate titles
                editorContainer.setFrameTitle(editorContainer.getFileData().getTabName(index), false);
                editorContainer.getTabbedPane().setTitleAt(index, editorContainer.getFileData().getTabName(index));

            } catch(IOException i) {
                PopUp.displayErrorMessage(editorContainer.getMainFrame(), i.getMessage());
            }
        }
    }

    public void openProjectFunctionality() {
        FileSelector selector   = new FileSelector("c:");
        int r                   = selector.showDirectoryOpenDialog(null);

        if(r == FileSelector.APPROVE_OPTION) {
            File file = selector.getSelectedDirectory();

            editorContainer.getFileTree().setNewModel(file);

            // Set appropriate title
            editorContainer.setFrameTitle("Project", false);
        }
    }

    public void openRecentFunctionality() {

    }

    /**
     *  Displays the replace box while hiding the search box
     *
     * @param index     the index in the tabbed pane at which to
     *                  show/ hide the replace/ search boxes respectively
     */
    public void replaceFunctionality(int index) {
        editorContainer.getReplaceBox().showReplaceBox(index, true);
        editorContainer.getSearchBox().showSearchBox(index, false);
    }

    /**
     *  Runs a program with a main method
     *
     *  CREDIT: https://dzone.com/articles/running-a-java-class-as-a-subprocess
     *
     * @param index     the index in the tabbed pane
     */
    public void runProgramFunctionality(int index) {
        // Direction to the file
        String path = editorContainer.getFileData().getTabPath(index);

        // Path must be present to compile and run
        if(path != null) {
            try {
                // Compile
                Runtime.getRuntime().exec("javac " + path);

                // Change directory and run
                String fileName = editorContainer.getFileData().getTabName(index);
                new ProcessBuilder("cmd.exe", "/c", String.format("cd %s && java %s", new File(path).getParent(),
                        fileName.substring(0, fileName.length() - 5))).start();
                //new ProcessBuilder("cmd.exe", "/c", "cd C:\\Java && java TestClass").start();

            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            PopUp.displayErrorMessage(editorContainer.getMainFrame(), "Save text to file before running.");
        }
    }

    /**
     * Saves all tabs that are open in the text editor.  Opens a pop-up if not
     * all files are saved to a file.
     *
     * @return  true if all files successfully saved, false if 1+ files not "save as"-ed and
     *          user decides to forfeit save all action
     */
    public boolean saveAllFunctionality() {
        // Used for a pop-up if one or more tabs not "save as"-ed
        boolean unsavedFile = false;

        // Iterate over each tab
        for(int i = 0; i < editorContainer.getFileData().numTabsOpen(); i++) {
            // File not "save as"-ed, only pop up once - no path yet
            if(!unsavedFile && editorContainer.getFileData().getTabPath(i) == null) {
                // If no text in text pane, no need to save empty text pane to file
                if(!editorContainer.getTextPane(i).getText().equals("")) {
                    if (PopUp.displayConfirmMessage(editorContainer.getMainFrame(),
                            "One or more files not saved to path.  Continue anyways?") != PopUp.OK_OPTION)
                        return false;

                    unsavedFile = true;
                }
            }

            saveFunctionality(i);
        }

        return true;
    }

    /**
     * Saves text to a file in a designated location on the user's computer
     *
     * @param index     index in the tabbed pane, at which a text pane's text is to be saved
     * @return          true if successfully saved, false if not
     */
    public boolean saveAsFunctionality(int index) {
        FileSelector selector    = new FileSelector(editorContainer.getFileData().getLastSavePath());
        int r                   = selector.showSaveDialog(null);

        if(r == FileSelector.APPROVE_OPTION) {
            File file = selector.getSelectedFile();
            try(BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {

                String text = editorContainer.getTextPane(index).getText();

                writer.write(text);

                // Set appropriate data for FileData instance
                editorContainer.getFileData().setTabName(index, file.getName());
                editorContainer.getFileData().setTabPath(index, file.getAbsolutePath());
                editorContainer.getFileData().setTabOgText(index, text);

                // Set titles
                editorContainer.setFrameTitle(editorContainer.getFileData().getTabName(index), false);
                editorContainer.getTabbedPane().setTitleAt(index, editorContainer.getFileData().getTabName(index));

                editorContainer.getFillerBox().setShownText(file.getAbsolutePath());

                writer.flush();
                writer.close();

                return true;
            } catch(IOException i) {
                PopUp.displayErrorMessage(editorContainer.getMainFrame(), i.getMessage());
            }
        }

        return false;
    }

    /**
     * Saves a given file
     *
     * @param index  the index at which to save a file
     * @return       true if successfully saved, false if not
     */
    public boolean saveFunctionality(int index) {
        // No path set yet
        if(editorContainer.getFileData().getTabPath(index) == null)
            return false;

        try(BufferedWriter writer
                    = new BufferedWriter(new FileWriter(editorContainer
                .getFileData().getTabPath(index), false))) {

            String text = editorContainer.getTextPane(index).getText();

            writer.write(text);

            // Set new original text
            editorContainer.getFileData().setTabOgText(index, text);

            // Set titles
            editorContainer.setFrameTitle(editorContainer.getFileData().getTabName(index), false);
            editorContainer.getTabbedPane().setTitleAt(index, editorContainer.getFileData().getTabName(index));

            writer.flush();
            writer.close();

            return true;
        } catch(IOException i) {
            PopUp.displayErrorMessage(editorContainer.getMainFrame(), i.getMessage());
        }

        return false;
    }

    /**
     * Brings up the Settings menu for the user
     */
    public void settingsFunctionality() {
        editorContainer.getSettingsMenu().showSettingsMenu(true);
    }

    /**
     *  Stops the current program from running
     *
     * @param index     the index at which to stop the program
     */
    public void stopProgramFunctionality(int index) {

    }

    /**
     *  Changes the case (lower vs. UPPER).  Only switches English alphabet characters.
     *
     * @param index     position in the tabbed pane
     */
    public void switchCaseFunctionality(int index) {
        // Switch case on selected text
        if(editorContainer.getTextPane(index).getSelectedText() != null) {
            String modText  = createSwitchCaseString(editorContainer.getTextPane(index).getSelectedText());
            int selStart    = editorContainer.getTextPane(index).getSelectionStart();
            int selEnd      = editorContainer.getTextPane(index).getSelectionEnd();

            try {
                editorContainer.getTextPane(index).getStyledDocument().remove(selStart, selEnd - selStart);
                editorContainer.getTextPane(index).getStyledDocument().insertString(selStart, modText, null);
            } catch (BadLocationException e) {
                e.printStackTrace();
            }

            // Re-select text
            editorContainer.getTextPane(index).setSelectionStart(selStart);
            editorContainer.getTextPane(index).setSelectionEnd(selEnd);
        }

        // Switch case on word
        else {
            String text             = editorContainer.getTextPane(index).getText();
            int caretPos            = editorContainer.getTextPane(index).getCaretPosition();
            int start               = findCaretStart(text, caretPos);
            int end                 = findCaretEnd(text, caretPos);

            try {
                String modText = createSwitchCaseString(text.substring(start, end));

                editorContainer.getTextPane(index).getStyledDocument().remove(start, end - start);
                editorContainer.getTextPane(index).getStyledDocument().insertString(start, modText, null);
            } catch (BadLocationException e) {
                e.printStackTrace();
            }

            // Re-position caret
            editorContainer.getTextPane(index).setCaretPosition(caretPos);
            editorContainer.getTextPane(index).getCaret().setVisible(true);
        }
    }

    /**
     *  Changes the case (lower vs. UPPER) so that all characters are either lower
     *  or upper case.  Only switches English alphabet characters.
     *  The state (lower vs. UPPER) of the String phrase is based off of the first
     *  character in the to-be-altered String, such that if the first character is
     *  upper case, then the new String will all be lower case, otherwise (in all
     *  other cases) the new String will be lower case.
     *
     * @param index     position in the tabbed pane
     */
    public void uniformCaseFunctionality(int index) {
        // Uniform case on selected text
        if(editorContainer.getTextPane(index).getSelectedText() != null) {
            String text     = editorContainer.getTextPane(index).getSelectedText();
            int selStart    = editorContainer.getTextPane(index).getSelectionStart();
            int selEnd      = editorContainer.getTextPane(index).getSelectionEnd();

            try {
                // First character in selected text
                char firstChar = editorContainer.getTextPane(index).getText().charAt(selStart);

                editorContainer.getTextPane(index).getStyledDocument().remove(selStart, selEnd - selStart);

                if(firstChar >= 'A' && firstChar <= 'Z') {
                    editorContainer.getTextPane(index).getStyledDocument().insertString(selStart, text.toLowerCase(), null);
                } else {
                    editorContainer.getTextPane(index).getStyledDocument().insertString(selStart, text.toUpperCase(), null);
                }
            } catch (BadLocationException e) {
                e.printStackTrace();
            }

            // Re-select text
            editorContainer.getTextPane(index).setSelectionStart(selStart);
            editorContainer.getTextPane(index).setSelectionEnd(selEnd);
        }

        // Uniform case on word
        else {
            String text             = editorContainer.getTextPane(index).getText();
            int caretPos            = editorContainer.getTextPane(index).getCaretPosition();
            int start               = findCaretStart(text, caretPos);
            int end                 = findCaretEnd(text, caretPos);

            try {
                String subText = text.substring(start, end);

                // First character in selected text
                char firstChar = text.charAt(start);

                editorContainer.getTextPane(index).getStyledDocument().remove(start, end - start);

                if(firstChar >= 'A' && firstChar <= 'Z') {
                    editorContainer.getTextPane(index).getStyledDocument().insertString(start, subText.toLowerCase(), null);
                } else {
                    editorContainer.getTextPane(index).getStyledDocument().insertString(start, subText.toUpperCase(), null);
                }
            } catch (BadLocationException e) {
                e.printStackTrace();
            }

            // Re-position caret
            editorContainer.getTextPane(index).setCaretPosition(caretPos);
            editorContainer.getTextPane(index).getCaret().setVisible(true);
        }
    }

    /**
     *  Creates a new String that alternates between lower and upper case letters
     *  in the English alphabet
     *
     * @param text  the String to alternate between lower and upper case
     * @return      the new String with alternating lower and upper case
     */
    private String createAlternateCaseString(String text) {
        StringBuilder builder = new StringBuilder();

        // Current index at which a character should be capitalized
        int capIndex = text.charAt(0) >= 'A' && text.charAt(0) <= 'Z' ? 1 : 0;

        for(int i = 0; i < text.length(); i++) {
            if(i == capIndex) {
                if (text.charAt(i) >= 'a' && text.charAt(i) <= 'z')
                    builder.append((char) (text.charAt(i) - 32));
                else
                    builder.append(text.charAt(i));

                capIndex += 2;
            } else {
                if (text.charAt(i) >= 'A' && text.charAt(i) <= 'Z')
                    builder.append((char) (text.charAt(i) + 32));
                else
                    builder.append(text.charAt(i));
            }
        }

        return builder.toString();
    }

    /**
     *  Creates a new String that makes all lower case letters capital
     *  and vice versa for an input String
     *
     * @param text  the String to switch lower and upper case
     * @return      the new String with lower and upper case switched
     */
    private String createSwitchCaseString(String text) {
        StringBuilder builder = new StringBuilder();

        // Switch cases
        for(int i = 0; i < text.length(); i++) {
            if(text.charAt(i) >= 'a' && text.charAt(i) <= 'z')
                builder.append((char) (text.charAt(i) - 32));
            else if(text.charAt(i) >= 'A' && text.charAt(i) <= 'Z')
                builder.append((char) (text.charAt(i) + 32));
            else
                builder.append(text.charAt(i));
        }

        return builder.toString();
    }

    /**
     *  Finds the start position in a String based on where the
     *  caret is positioned.
     *  The start is any character that is any non-language
     *  character, non-hyphen, or non-underscore
     *
     * @param text          the String to search for the start index
     * @param caretPos      position of the caret in the text
     * @return              the start index in the text
     */
    private int findCaretStart(String text, int caretPos) {
        int start = 0; // Start of phrase

        // Search to the left for the first non-language character
        for(int i = caretPos - 1; i >= 0; i--) {
            if((text.charAt(i) < 'a' || text.charAt(i) > 'z')
                    && (text.charAt(i) < 'A' || text.charAt(i) > 'Z')
                    && (text.charAt(i) < 128 || text.charAt(i) > 165) // non-English
                    && text.charAt(i) != '-' && text.charAt(i) != '_') {
                start = i + 1;
                break;
            }
        }

        return start;
    }

    /**
     *  Finds the end position in a String based on where the
     *  caret is positioned.
     *  The end is any character that is any non-language
     *  character, non-hyphen, or non-underscore
     *
     * @param text          the String to search for the end index
     * @param caretPos      position of the caret in the text
     * @return              the end index in the text
     */
    private int findCaretEnd(String text, int caretPos) {
        int end = text.length(); // End of phrase

        // Search to the right for the first non-language character
        for(int i = caretPos; i < text.length(); i++) {
            if((text.charAt(i) < 'a' || text.charAt(i) > 'z')
                    && (text.charAt(i) < 'A' || text.charAt(i) > 'Z')
                    && (text.charAt(i) < 128 || text.charAt(i) > 165) // non-English
                    && text.charAt(i) != '-' && text.charAt(i) != '_') {
                end = i;
                break;
            }
        }

        return end;
    }
}
