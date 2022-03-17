package main.gui.components.boxcomponents;

import main.gui.EditorContainer;
import main.gui.components.PopUp;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.Arrays;
import java.util.regex.Pattern;

/**
 * Consists of a set of components that work cohesively to replace
 * a phrase in the text pane.  The user types a phrase into a text box,
 * which will be searched for in the text of the text pane.  Another
 * text box will be used for the user to type in the replacement for
 * the phrase typed in the other text box.
 *
 * @author Justin Kocur
 */
public class ReplaceBox extends Box {
    /* Case sensitivity button; toggle on so that cases matter (e.g. 'A' != 'a')
       or toggle off so that cases do not matter (e.g. 'A' == 'a') */
    private final JButton caseSenButton;

    // The main GUI application for the text editor
    private final EditorContainer editorContainer;

    // The maximum number of tabs allowed open at once in the text editor
    private final int MAX_TABS;

    // Contains the new phrase to replace the phrase in 'ogPhraseArea' in the text pane
    private final JTextPane newPhraseArea;

    // Used to store the initial phrase that is to be looked for in the text pane
    private final JTextPane ogPhraseArea;

    // Used to replace the original phrase with the new one
    private final JButton replaceButton;

    // Stores information regarding the replace process
    private final ReplaceData replaceData;

    // The text pane that will contain the text to scrape/ search for phrase
    private JTextPane textPane;

    /**
     *  Instantiates a new ReplaceBox
     *
     * @param editorContainer   the main GUI for the text editor
     */
    public ReplaceBox(EditorContainer editorContainer, int MAX_TABS, JTextPane textPane) {
        super(new FlowLayout(FlowLayout.LEFT));
        setLayout(new BoxLayout(this, BoxLayout.X_AXIS));

        this.editorContainer = editorContainer;
        this.MAX_TABS = MAX_TABS;
        this.textPane = textPane;
        replaceData = new ReplaceData();

        ogPhraseArea    = new JTextPane();
        newPhraseArea   = new JTextPane();

        caseSenButton = new JButton("Aa");
        replaceButton = new JButton("Replace");

        constructComponents();
    }

    /**
     *  Returns the text pane associated with this replace box
     *
     * @return   the text pane associated with this replace box
     */
    public JTextPane getTextPane() {
        return textPane;
    }

    /**
     *  Sets the text pane associated with this replace box
     *
     * @param textPane   the text pane associated with this replace box
     */
    public void setTextPane(JTextPane textPane) {
        this.textPane = textPane;
    }

    /**
     *  Removes and resets the replace box data at a specified index
     *
     * @param index     index in the tabbed pane
     */
    public void removeReplaceBoxAtIndex(int index) {
        if(index < 0 || index >= MAX_TABS)
            return;

        replaceData.resetAtIndex(index);
        setVisibleAtIndex(index, false);
    }

    /**
     *  Should be called whenever the contents of the replace box are to be saved
     *
     * @param tabIndex      the index at which to set the String text in each text area
     */
    public void setCurrentReplace(int tabIndex) {
        replaceData.ogText[tabIndex] = ogPhraseArea.getText();
        replaceData.newText[tabIndex] = newPhraseArea.getText();
    }

    /**
     *  Used to show/ hide the replace box depending on if the user
     *  wants it shown in that particular tab in the tabbed pane
     *
     *  More specifically, when the user switches to a different
     *  tab in the editor's tabbed pane, this method should be called
     *  to determine if the replace box was previously opened in this tab
     *  by the user.
     *
     * @param index     the index which will determine whether or not
     *                  to hide the replace box
     */
    public void setTab(int index) {
        if(index < 0 || index >= MAX_TABS)
            return;

        setVisible(replaceData.visible[index]);
    }

    /**
     *  Sets the color of all text in the box
     *
     * @param color     the new color
     */
    /*public void setTextColor(Color color) {
        caseSenButton.setForeground(color);
        replaceButton.setForeground(color);
    }*/

    /**
     *  Shows/ hides the replace box at the given index
     *
     * @param tabIndex  the tab index at which to close/ hide the replace box
     * @param flag      true if visible, false if not
     */
    public void showReplaceBox(int tabIndex, boolean flag) {
        JTextPane pane  = editorContainer.getTextPane(tabIndex);
        String selTxt   = pane.getSelectedText();

        setVisibleAtIndex(tabIndex, flag);

        // Only set designated text for text area if something is selected
        if(selTxt != null) {
            ogPhraseArea.setText(selTxt);
        }

        // Set highlighted text, focus, and caret position when/ where appropriate
        if(replaceData.visible[tabIndex]) {
            ogPhraseArea.selectAll();
            ogPhraseArea.requestFocus();
        }
    }

    /**
     * Defines the necessary properties for each component in the ReplaceBox instance
     */
    private void constructComponents() {
        caseSenButton.setToolTipText("Case Sensitive");
        replaceButton.setToolTipText("Replace All");

        setDimensions(caseSenButton, 50, 25);
        setDimensions(replaceButton, 75, 25);

        JScrollPane ogPhraseScrollPane = new JScrollPane(ogPhraseArea);
        ogPhraseScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        ogPhraseScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        ogPhraseScrollPane.setPreferredSize(new Dimension(250, this.getHeight()));
        ogPhraseScrollPane.setMinimumSize(new Dimension(250, this.getHeight()));
        ogPhraseScrollPane.setMaximumSize(new Dimension(250, this.getHeight()));

        JScrollPane newPhraseScrollPane = new JScrollPane(newPhraseArea);
        newPhraseScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        newPhraseScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        newPhraseScrollPane.setPreferredSize(new Dimension(250, this.getHeight()));
        newPhraseScrollPane.setMinimumSize(new Dimension(250, this.getHeight()));
        newPhraseScrollPane.setMaximumSize(new Dimension(250, this.getHeight()));

        caseSenButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int tabIndex = editorContainer.getTabbedPane().getSelectedIndex();

                replaceData.matchCases[tabIndex] = !replaceData.matchCases[tabIndex];

                if(replaceData.matchCases[tabIndex]) {
                    caseSenButton.setBackground(new Color(125, 150, 200));
                } else {
                    caseSenButton.setBackground(Color.lightGray);
                }
            }
        });
        replaceButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Index of the tab currently being looked at
                int tabIndex = editorContainer.getTabbedPane().getSelectedIndex();

                setCurrentReplace(tabIndex);

                String searchText   = ogPhraseArea.getText();
                String replaceText  = newPhraseArea.getText();

                // Phrase replaces itself
                if(searchText.equals(replaceText))
                    return;

                String message = String.format("Replace \"%s\" with \"%s?\"", searchText, replaceText);

                if(PopUp.displayConfirmMessage(editorContainer.getMainFrame(), message) == PopUp.OK_OPTION) {
                    JTextPane textPane = editorContainer.getTextPane(tabIndex);
                    if(replaceData.matchCases[tabIndex])
                        textPane.setText(textPane.getText().replaceAll(Pattern.quote(searchText), replaceText));
                    else
                        textPane.setText(textPane.getText().replaceAll(String.format("(?i)%s", Pattern.quote(searchText)), replaceText));
                }
            }
        });

        add(ogPhraseScrollPane);
        add(newPhraseScrollPane);
        add(caseSenButton);
        add(replaceButton);

        KeyAdapter keyAdapter = new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if(e.getKeyCode() == KeyEvent.VK_ESCAPE) {
                    int tabIndex = editorContainer.getTabbedPane().getSelectedIndex();
                    showReplaceBox(tabIndex, false);
                    editorContainer.resetTextPaneFocus();
                }
            }
        };
        ogPhraseArea.addKeyListener(keyAdapter);
        newPhraseArea.addKeyListener(keyAdapter);
        for(Component c : this.getComponents()) {
            c.addKeyListener(keyAdapter);
        }
    }

    /**
     *  Determines whether or not the replace box should be visible
     *  at a given tab in the tabbed pane
     *
     * @param index     the index at which to update the true/false state
     * @param flag      true if visible at this position in the tabbed pane, false if not
     */
    private void setVisibleAtIndex(int index, boolean flag) {
        if(index < 0 || index >= MAX_TABS)
            return;

        replaceData.visible[index] = flag;

        // Set visibility here if applicable; set corresponding text pane as well
        if(index == editorContainer.getTabbedPane().getSelectedIndex()) {
            setVisible(replaceData.visible[index]);
            setTextPane(editorContainer.getTextPane(index));
        }
    }

    /**
     *  Stores information regarding replacing a phrase in
     *  the text pane
     *
     * @author Justin Kocur
     */
    private class ReplaceData {
        // True if case sensitive (e.g. 'A' != 'a'), false if not (e.g. 'A' == 'a')
        private final boolean[] matchCases;

        // The text in the 'newPhraseArea' at each tab index
        private final String[] newText;

        // The text in the 'ogPhraseArea' at each tab index
        private final String[] ogText;

        // Determines if a specified tab should allow this replace box to be visible
        private final boolean[] visible;

        /**
         *  Creates a ReplaceData instance
         */
        public ReplaceData() {
            visible     = new boolean[ReplaceBox.this.MAX_TABS];
            matchCases  = new boolean[ReplaceBox.this.MAX_TABS];

            ogText  = new String[ReplaceBox.this.MAX_TABS];
            Arrays.fill(ogText, "");
            newText = new String[ReplaceBox.this.MAX_TABS];
            Arrays.fill(newText, "");
        }

        /**
         *  Resets all variables to their original values
         *
         * @param tabIndex  the index at which to reset the values
         */
        private void resetAtIndex(int tabIndex) {
            visible[tabIndex] = false;
            matchCases[tabIndex] = false;
            ogText[tabIndex] = "";
            newText[tabIndex] = "";
        }
    }
}
