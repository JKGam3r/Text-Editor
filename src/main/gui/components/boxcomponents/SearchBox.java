package main.gui.components.boxcomponents;

import main.data.LineData;
import main.gui.EditorContainer;

import javax.swing.*;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultHighlighter;
import javax.swing.text.Highlighter;
import java.awt.*;
import java.awt.event.*;
import java.util.*;

/**
 * Allows the user to search for a phrase (sequence of characters)
 * in the currently selected tab in the text editor
 * There should only be one SearchBox instance for the application,
 * which is why type int 'MAX_TABS' and type boolean-array 'visible'
 * (located within 'SearchData' class type) are used
 *
 * @author Justin Kocur
 */
public class SearchBox extends Box {

    /* Case sensitivity button; toggle on so that cases matter (e.g. 'A' != 'a')
       or toggle off so that cases do not matter (e.g. 'A' == 'a') */
    private final JButton caseSenButton;

    /* Shows the current entry being looked at divided by the total number
       of found matches (i.e. in the form a / b) */
    private final JLabel countLabel;

    // The main GUI application for the text editor
    private final EditorContainer editorContainer;

    // The initial text of the count label when nothing is being searched
    private final String initLblTxt;

    // The maximum number of tabs allowed in this text editor
    private final int MAX_TABS;

    /* Allows the user to find the previous and next occurrence,
       respectively, of a phrase */
    private final JButton prevButton, nextButton;

    // The component in which the user will type a phrase to search for
    private final JTextPane searchBox;

    // Stores information for the searches in each tab
    private final SearchData searchData;

    // Used to manage highlighting for user searches
    private final SearchHighlighter searchHighlighter;

    // Contains a list of selectable Strings that the user already typed in
    private final JComboBox<String> searchHistoryComboBox;

    // Responsible for controlling search actions
    private final SearchListener listener;

    // The text pane that will contain the text to scrape/ search for phrase
    private JTextPane textPane;

    /**
     * Instantiates a new SearchBox for the user.  Creates and defines several
     * components necessary for the search box to properly function.
     *
     * @param editorContainer   the main GUI application for the text editor
     * @param MAX_TABS          the max number of tabs allowed in editor
     * @param textPane          the text pane used to look for a phrase given by the user
     */
    public SearchBox(EditorContainer editorContainer, final int MAX_TABS, JTextPane textPane) {
        super(new FlowLayout(FlowLayout.LEFT));
        setLayout(new BoxLayout(this, BoxLayout.X_AXIS));

        this.editorContainer = editorContainer;
        this.MAX_TABS = MAX_TABS;
        this.textPane = textPane;

        listener = new SearchListener();

        searchHighlighter = new SearchHighlighter();

        searchData = new SearchData();

        searchBox = new JTextPane();

        initLblTxt = "0/0 results";

        prevButton = new JButton("Prev");
        nextButton = new JButton("Next");
        caseSenButton = new JButton("Aa");

        countLabel = new JLabel(initLblTxt);

        searchHistoryComboBox = new JComboBox<>();

        constructComponents();
    }

    /**
     *  Returns the text pane associated with this search box
     *
     * @return   the text pane associated with this search box
     */
    public JTextPane getTextPane() {
        return textPane;
    }

    /**
     *  Sets the text pane associated with this search box
     *
     * @param textPane   the text pane associated with this search box
     */
    public void setTextPane(JTextPane textPane) {
        this.textPane = textPane;
    }

    /**
     *  Returns whether or not the text in the search bar changed
     *
     * @param index     the index of the text pane in the tabbed pane
     * @return          true if search text changed, false if not
     */
    public boolean isSearchChanged(int index) {
        String lastSearchedPhrase   = SearchBox.this.searchData.currentSearches[index];
        String currentPhrase        = SearchBox.this.searchData.matchCases[index] ? SearchBox.this.searchBox.getText()
                                    : SearchBox.this.searchBox.getText().toLowerCase();

        return !lastSearchedPhrase.equals(currentPhrase);
    }

    /**
     *  Returns whether or not the text in the text pane changed from the last
     *  time it was searched for a phrase
     *
     * @param index     the index of the text pane in the tabbed pane
     * @return          true if text changed, false if not
     */
    public boolean isTextChanged(int index) {
        String wholeText = editorContainer.getTextPane(index).getText();

        return !wholeText.equals(SearchBox.this.searchData.lastRecordedText[index]);
    }

    /**
     * Rehighlights the text
     *
     * @param selectFlag    true if text should be highlighted, false if 'select' method
     *                      from text pane should be used
     */
    public void rehighlight(boolean selectFlag) {
        int tabIndex = editorContainer.getTabbedPane().getSelectedIndex();

        listener.findAllOccurrences(searchBox.getText(),
                editorContainer.getTextPane(tabIndex).getText(), selectFlag);
    }

    /**
     *  Removes and resets the search box data at a specified index
     *
     * @param index     index in the tabbed pane
     */
    public void removeSearchBoxAtIndex(int index) {
        if(index < 0 || index >= MAX_TABS)
            return;

        searchData.resetAtIndex(index);
        setVisibleAtIndex(index, false);
    }

    /**
     *  Should be called whenever the contents of the search box are to be saved
     *  to the 'currentSearches' data structure
     *
     * @param tabIndex      the index at which to set the element value in the 'currentSearches' data structure
     */
    public void setCurrentSearch(int tabIndex) {
        searchData.currentSearches[tabIndex] = searchBox.getText();
    }

    /**
     *  Used to show/ hide the search bar depending on if the user
     *  wants it shown in that particular tab in the tabbed pane
     *
     *  More specifically, when the user switches to a different
     *  tab in the editor's tabbed pane, this method should be called
     *  to determine if the search box was previously opened in this tab
     *  by the user.
     *
     * @param index     the index which will determine whether or not
     *                  to hide the search bar
     */
    public void setTab(int index) {
        if(index < 0 || index >= MAX_TABS)
            return;

        setVisible(searchData.visible[index]);
        searchBox.setText(searchData.currentSearches[index]);
        countLabel.setText(searchData.results[index] != null ? searchData.results[index] : initLblTxt);
    }

    /**
     * Defines properties for the various components that allow the
     * search box to operate
     */
    private void constructComponents() {
        prevButton.setToolTipText("Previous Occurrence");
        nextButton.setToolTipText("Next Occurrence");
        caseSenButton.setToolTipText("Case Sensitive");

        searchHistoryComboBox.addItem("");
        searchHistoryComboBox.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                if(!Objects.requireNonNull(searchHistoryComboBox.getSelectedItem()).toString().equals("")) {
                    searchBox.setText(Objects.requireNonNull(searchHistoryComboBox.getSelectedItem()).toString());
                    rehighlight(true);
                }
            }
        });

        // Add listener
        prevButton.addActionListener(listener);
        nextButton.addActionListener(listener);
        caseSenButton.addActionListener(listener);

        // Set sizes for each component
        setDimensions(prevButton, 75, 25);
        setDimensions(nextButton, 75, 25);
        setDimensions(caseSenButton, 50, 25);
        setDimensions(countLabel, 75, 15);

        setDimensions(searchHistoryComboBox, 100, this.getHeight());
        searchHistoryComboBox.setMinimumSize(new Dimension(100, this.getHeight()));
        searchHistoryComboBox.setMaximumSize(new Dimension(100, this.getHeight()));

        JScrollPane scrollPane = new JScrollPane(searchBox);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setPreferredSize(new Dimension(250, this.getHeight()));
        scrollPane.setMinimumSize(new Dimension(250, this.getHeight()));
        scrollPane.setMaximumSize(new Dimension(250, this.getHeight()));

        // Add all components to the SearchBox class type
        add(scrollPane);
        add(caseSenButton);
        add(countLabel);
        add(prevButton);
        add(nextButton);
        add(searchHistoryComboBox);
        KeyAdapter keyAdapter = new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if(e.getKeyCode() == KeyEvent.VK_ESCAPE) {
                    int tabIndex = editorContainer.getTabbedPane().getSelectedIndex();
                    showSearchBox(tabIndex, false);
                    editorContainer.resetTextPaneFocus();
                }
            }
        };
        searchBox.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if(e.getKeyCode() == KeyEvent.VK_ESCAPE) {
                    int tabIndex = editorContainer.getTabbedPane().getSelectedIndex();
                    showSearchBox(tabIndex, false);
                    editorContainer.resetTextPaneFocus();
                }
            }

            @Override
            public void keyReleased(KeyEvent e) {
                int index = editorContainer.getTabbedPane().getSelectedIndex();

                if(isSearchChanged(index) || isTextChanged(index)) {
                    rehighlight(true);
                }
            }
        });
        for(Component c : this.getComponents()) {
            c.addKeyListener(keyAdapter);
            c.setFocusable(false);
        }
    }

    /**
     *  Determines whether or not the search bar should be visible
     *  at a given tab in the tabbed pane
     *
     * @param index     the index at which to update the true/false state
     * @param flag      true if visible at this position in the tabbed pane, false if not
     */
    private void setVisibleAtIndex(int index, boolean flag) {
        if(index < 0 || index >= MAX_TABS)
            return;

        searchData.visible[index] = flag;

        // Set visibility here if applicable; set corresponding text pane as well
        if(index == editorContainer.getTabbedPane().getSelectedIndex()) {
            setVisible(searchData.visible[index]);
            setTextPane(editorContainer.getTextPane(index));
        }
    }

    /**
     *  Shows/ hides the search box at the given index
     *
     * @param tabIndex  the tab index at which to close/ hide the search box
     * @param flag      true if visible, false if not
     */
    public void showSearchBox(int tabIndex, boolean flag) {
        JTextPane pane  = editorContainer.getTextPane(tabIndex);
        String selTxt   = pane.getSelectedText();

        setVisibleAtIndex(tabIndex, flag);

        // Only set designated text for text area if something is selected
        if(selTxt != null) {
            searchBox.setText(selTxt);
        }

        // Set highlighted text and focus
        if(searchData.visible[tabIndex]) {
            searchBox.requestFocus();
            searchBox.selectAll();
        } else {
            searchHighlighter.removeAllHighlights(tabIndex);

            searchData.lastRecordedText[tabIndex] = "";

            searchData.results[tabIndex] = initLblTxt;
            searchData.phraseCount[tabIndex] = 1;
            searchData.totalOccurrences[tabIndex] = 0;

            countLabel.setText(initLblTxt);
        }
    }

    /**
     * Stores information relating to each individual tab's search bar information
     *
     * @author Justin Kocur
     */
    private class SearchData {
        // Contains the current searched phrase at different tabs
        private final String[] currentSearches;

        /* The last time the text was recorded, i.e. when the prev/ next buttons
           were used to search the text pane */
        private final String[] lastRecordedText;

        // True if case sensitive (e.g. 'A' != 'a'), false if not (e.g. 'A' == 'a')
        private final boolean[] matchCases;

        // The number of instances a phrase is found in the text pane
        private final int[] phraseCount;

        /* The current entry over the total entries that pop up
           when searching in the form 'a / b' */
        private final String[] results;

        // The total number of occurrences of the user-typed phrase
        private final int[] totalOccurrences;

        // Determines if a specified tab should allow this search box to be visible
        private final boolean[] visible;

        /**
         * Initializes a SearchData instance
         */
        public SearchData() {
            visible = new boolean[SearchBox.this.MAX_TABS];

            currentSearches = new String[SearchBox.this.MAX_TABS];
            Arrays.fill(currentSearches, "");

            results = new String[SearchBox.this.MAX_TABS];
            Arrays.fill(results, initLblTxt);

            lastRecordedText = new String[SearchBox.this.MAX_TABS];
            Arrays.fill(lastRecordedText, "");

            phraseCount = new int[SearchBox.this.MAX_TABS];
            Arrays.fill(phraseCount, 1);

            totalOccurrences = new int[SearchBox.this.MAX_TABS];
            matchCases = new boolean[SearchBox.this.MAX_TABS];
        }

        /**
         * Resets all data regarding the search information on a specified tab 'index'
         *
         * @param index     the tab index at which to reset the tab search information
         */
        public void resetAtIndex(int index) {
            visible[index] = false;
            currentSearches[index] = "";
            results[index] = initLblTxt;
            lastRecordedText[index] = "";
            phraseCount[index] = 1;
            totalOccurrences[index] = 0;
            matchCases[index] = false;
        }
    }

    /**
     * Used to highlight the user-searched phrase in a specified text pane
     *
     * @author Justin Kocur
     */
    private class SearchHighlighter {
        // The color to highlight all character sequences that match the user-typed phrase
        private final Color phraseColor;

        /* The specific instance of the user-typed phrase being looked at (such as the user
           hitting the previous or next button) */
        private final Color selectedColor;

        /**
         * Constructs a SearchHighlighter instance
         */
        public SearchHighlighter() {
            phraseColor     = new Color(93, 226, 60, 125);
            selectedColor   = new Color(13, 213, 252, 125);
        }

        /**
         * Highlights all character sequences in the given text pane
         * at the specified index that matches the user-typed phrase
         *
         * @param list              used for the iterator (and to reset it); goes over each element in the list to highlight
         *                          each instance of the phrase
         * @param index             the index in the tabbed pane at which to get the highlighter
         * @param phrase            the user-typed phrase to highlight
         * @param selectFlag        true if text at 'selectedIndex' should be highlighted, false if text pane's 'select' method should be used
         * @param selectedIndex     the index in the text of the text pane to highlight with the 'selectedColor' color
         */
        public void highlightAll(LinkedList<Integer> list, int index, String phrase, boolean selectFlag, int selectedIndex) {
            removeAllHighlights(index);

            Highlighter highlighter                 = SearchBox.this.editorContainer.getTextPane(index).getHighlighter();
            Highlighter.HighlightPainter p1         = new DefaultHighlighter.DefaultHighlightPainter(phraseColor);
            Highlighter.HighlightPainter p2         = new DefaultHighlighter.DefaultHighlightPainter(selectedColor);
            int length                              = phrase.length();

            // Go over each instance of the phrase in the text pane
            for (int start : list) {
                int end = start + length;
                int addedLines = editorContainer.getFileData().isOpenedFromDir(index) ?
                        0 : LineData.getIndexLineNumber(editorContainer.getTextPane(index), start);

                try {
                    if (start == selectedIndex) {
                        if(selectFlag)
                            highlighter.addHighlight(start - addedLines, end - addedLines, p2);
                        else {
                            editorContainer.getTextPane(index).requestFocus();
                            editorContainer.getTextPane(index).select(start - addedLines, end - addedLines);
                        }
                    }
                    else
                        highlighter.addHighlight(start - addedLines, end - addedLines, p1);
                } catch (BadLocationException ex) {
                    ex.printStackTrace();
                }
            }
        }

        /**
         *  Highlights part of the text in a text pane.  This method should only be used
         *  if there are no other highlights
         *
         * @param index             the index of the text pane in the tabbed pane
         * @param selectedIndex     the start of the text to select
         * @param length            the length of the text to select
         */
        public void highlightSelected(int index, int selectedIndex, int length) {
            removeAllHighlights(index); // this should only remove one highlight

            Highlighter highlighter                 = SearchBox.this.editorContainer.getTextPane(index).getHighlighter();
            Highlighter.HighlightPainter p          = new DefaultHighlighter.DefaultHighlightPainter(selectedColor);
            int addedLines                          = editorContainer.getFileData().isOpenedFromDir(index) ?
                                                    0 : LineData.getIndexLineNumber(editorContainer.getTextPane(index), selectedIndex);

            try {
                highlighter.addHighlight(selectedIndex - addedLines, selectedIndex + length - addedLines, p);
            } catch (BadLocationException ex) {
                ex.printStackTrace();
            }
        }

        /**
         *  Removes all the highlights from a text pane
         *
         * @param index     index in the tabbed pane at which the text pane is located
         */
        public void removeAllHighlights(int index) {
            SearchBox.this.editorContainer.getTextPane(index).getHighlighter().removeAllHighlights();
        }
    }

    /**
     * An 'ActionListener' class used to allow the user to interact with the search bar
     *
     * @author Justin Kocur
     */
    private class SearchListener implements ActionListener {
        /* Contains all starting indexes (in the String of a text pane)
           of the designated phrase being searched for */
        private final LinkedList<Integer> indexes;

        // Used to easily traverse between indexes when user presses previous and next buttons
        private ListIterator<Integer> iterator;

        // The last index returned by the list iterator
        private int lastTextIndex;

        /* The maximum number of occurrences that will be highlighted.
           If the number of max occurrences is exceeded, only the currently
           selected occurrence being looked at will be highlighted. */
        private final int MAX_HIGHLIGHT_OCCURRENCES = 2_000;

        // The maximum number of searches the user can look at from search history
        private final int MAX_SEARCHES;

        /**
         *  Constructs a new SearchListener instance
         */
        public SearchListener() {
            indexes             = new LinkedList<>();
            iterator            = indexes.listIterator();
            MAX_SEARCHES        = 10;
        }

        /**
         *  Allows interactive functionality with the search bar.
         *  Called when user presses a button.
         *
         * @param e     ActionEvent instance for getting source of action
         */
        @Override
        public void actionPerformed(ActionEvent e) {
            // Retrieves the object (specifically the GUI button) that the user pressed
            Object source = e.getSource();

            // Index of the tab currently being looked at
            int tabIndex = editorContainer.getTabbedPane().getSelectedIndex();

            // Search for the button that was pressed
            if(source == SearchBox.this.prevButton) {
                prevFunctionality();
            } else if(source == SearchBox.this.nextButton) {
                nextFunctionality();
            } else if(source == SearchBox.this.caseSenButton) {
                caseSenFunctionality(tabIndex);
            }
        }

        /**
         *  Adds a new element into the combo box that the user-typed into the search bar.
         *  Repeats are allowed in the combo box.
         *
         * @param phrase    the new user-typed search phrase to add to the search history combo box
         */
        private void addSearch(String phrase) {
            // String item already at head of list
            if(phrase.equals(SearchBox.this.searchHistoryComboBox.getItemAt(1)))
                return;

            // Max number of entries reached (+1 to account for additional empty String)
            if(SearchBox.this.searchHistoryComboBox.getItemCount() >= MAX_SEARCHES + 1) {
                SearchBox.this.searchHistoryComboBox.removeItemAt(MAX_SEARCHES);
            }

            SearchBox.this.searchHistoryComboBox.insertItemAt(phrase, 1);
        }

        private void caseSenFunctionality(int tabIndex) {
            searchData.matchCases[tabIndex] = !searchData.matchCases[tabIndex];

            if(searchData.matchCases[tabIndex]) {
                caseSenButton.setBackground(new Color(125, 150, 200));
            } else {
                caseSenButton.setBackground(Color.lightGray);
            }

            rehighlight(true);
        }

        /**
         *  Adds all starting indexes of the searched-for phrase in the linked list
         *
         * @param searchText    the text the user is searching for via the search bar
         * @param wholeText     the text in the text pane
         * @param selectFlag    true if selected text should be highlighted, false if text pane's
         *                      'select' method should be used
         */
        private void findAllOccurrences(String searchText, String wholeText, boolean selectFlag) {
            // Index of the tab currently being looked at
            int tabIndex = editorContainer.getTabbedPane().getSelectedIndex();
            SearchBox.this.searchHighlighter.removeAllHighlights(tabIndex);

            lastTextIndex = 0;

            setCurrentSearch(tabIndex);

            SearchBox.this.searchData.phraseCount[tabIndex] = 1;

            // Implicit cursor should be placed back at the beginning of the list
            iterator = indexes.listIterator();

            // Clear all previous entries
            while(iterator.hasNext()) {
                iterator.next();
                iterator.remove();
            }

            // No search
            if(searchText.equals("")) {
                SearchBox.this.searchData.phraseCount[tabIndex] = 0;
                SearchBox.this.searchData.totalOccurrences[tabIndex] = 0;
                setCountLblTxt(0, 0);
                return;
            }

            addSearch(searchText);

            if(!SearchBox.this.searchData.matchCases[tabIndex]) {
                searchText = searchText.toLowerCase();
                wholeText = wholeText.toLowerCase();
            }

            int currentIndex    = wholeText.indexOf(searchText);
            int index           = currentIndex + searchText.length();
            int numOccurrences  = 0;

            /* Search through the whole text pane; note that a character is
               only counted once, e.g. text 'abcabca' with search term 'abca'
               will mean the linked list will only have one item: abca|bca, where
               where the loop will continue with 'bca'*/
            while(currentIndex != -1) {
                numOccurrences++;
                iterator.add(index - searchText.length());

                if(index < wholeText.length()) {
                    currentIndex = wholeText.substring(index).indexOf(searchText);
                    index += currentIndex + searchText.length();
                } else
                    currentIndex = -1;
            }

            // Implicit cursor should be placed back at the beginning of the list
            iterator = indexes.listIterator();

            /* Set implicit cursor to where highlighted text is, or if no text highlighted, set to
               closest instance of phrase to caret position in text */
            String selectedText = editorContainer.getTextPane(tabIndex).getSelectedText();
            int idx             = -1;
            if(selectedText != null && selectedText.equals(searchText)) {
                if(iterator.hasNext())
                    idx = iterator.next();

                while(iterator.hasNext() && idx != editorContainer.getTextPane(tabIndex).getSelectionStart()) {
                    idx = iterator.next();

                    SearchBox.this.searchData.phraseCount[tabIndex]++;
                }

                lastTextIndex = idx;
            }

            // Find closest instance of phrase to caret position
            else {
                int caretPos    = editorContainer.getTextPane(tabIndex).getCaretPosition();
                int prevIdx     = 0;
                int length      = searchText.length();

                while(iterator.hasNext()) {
                    prevIdx = idx;
                    idx     = iterator.next();

                    // Position found
                    if(prevIdx + length <= caretPos && idx >= caretPos) {
                        // Caret closer to prevIdx
                        if(caretPos - prevIdx + length - 1 <= idx - caretPos) {
                            iterator.previous();
                            idx = prevIdx;
                            SearchBox.this.searchData.phraseCount[tabIndex]--;
                        }
                        break;
                    }

                    if(iterator.hasNext())
                        SearchBox.this.searchData.phraseCount[tabIndex]++;
                }

                lastTextIndex = idx;
            }

            // Highlight appropriately
            if(indexes.size() > 0) {
                if(numOccurrences <= MAX_HIGHLIGHT_OCCURRENCES) {
                    if (idx == -1)
                        SearchBox.this.searchHighlighter.highlightAll(indexes, tabIndex, searchText, selectFlag, indexes.getFirst());
                    else
                        SearchBox.this.searchHighlighter.highlightAll(indexes, tabIndex, searchText, selectFlag, idx);
                } else {
                    if (idx == -1)
                        SearchBox.this.searchHighlighter.highlightSelected(tabIndex, indexes.getFirst(), searchText.length());
                    else
                        SearchBox.this.searchHighlighter.highlightSelected(tabIndex, idx, searchText.length());
                }
            }

            if(numOccurrences == 0)
                setCountLblTxt(0, numOccurrences);
            else
                setCountLblTxt(SearchBox.this.searchData.phraseCount[tabIndex], numOccurrences);

            SearchBox.this.searchData.totalOccurrences[tabIndex] = numOccurrences;

            // Set the last recorded text used to find user-typed phrase
            SearchBox.this.searchData.lastRecordedText[tabIndex] = wholeText;

            // Still 0, set to first index
            if(numOccurrences != 0 && lastTextIndex == 0)
                lastTextIndex = indexes.getFirst();
        }

        /**
         * Defines what the next button should do upon a user pressing it
         */
        private void nextFunctionality() {
            // Index of the tab currently being looked at
            int tabIndex = editorContainer.getTabbedPane().getSelectedIndex();

            // The phrase currently in the search bar
            String currentPhrase = SearchBox.this.searchData.matchCases[tabIndex] ? SearchBox.this.searchBox.getText()
                    : SearchBox.this.searchBox.getText().toLowerCase();

            // Check if there is a next element
            if(iterator.hasNext() && SearchBox.this.searchData.totalOccurrences[tabIndex] > 1) {
                int tempIdx = iterator.next();

                // Handle what happens at end of iterator
                if(lastTextIndex == tempIdx)
                    lastTextIndex = iterator.next();
                else
                    lastTextIndex = tempIdx;

                // Advance implicit cursor
                SearchBox.this.searchData.phraseCount[tabIndex]++;
                if(SearchBox.this.searchData.totalOccurrences[tabIndex] <= MAX_HIGHLIGHT_OCCURRENCES)
                    SearchBox.this.searchHighlighter.highlightAll(indexes, tabIndex, currentPhrase, false, lastTextIndex);
                else
                    SearchBox.this.searchHighlighter.highlightSelected(tabIndex, lastTextIndex, currentPhrase.length());
            }

            if(SearchBox.this.searchData.totalOccurrences[tabIndex] == 0)
                setCountLblTxt(0, SearchBox.this.searchData.totalOccurrences[tabIndex]);
            else
                setCountLblTxt(SearchBox.this.searchData.phraseCount[tabIndex], SearchBox.this.searchData.totalOccurrences[tabIndex]);
        }

        /**
         * Defines what the previous button should do upon a user pressing it
         */
        private void prevFunctionality() {
            // Index of the tab currently being looked at
            int tabIndex = editorContainer.getTabbedPane().getSelectedIndex();

            // The phrase currently in the search bar
            String currentPhrase = SearchBox.this.searchData.matchCases[tabIndex] ? SearchBox.this.searchBox.getText()
                    : SearchBox.this.searchBox.getText().toLowerCase();

            // Check if there is a previous element
            if(iterator.hasPrevious() && SearchBox.this.searchData.totalOccurrences[tabIndex] > 1) {
                int tempIdx = iterator.previous();

                // Handle what happens at end of iterator
                if(lastTextIndex == tempIdx)
                    lastTextIndex = iterator.previous();
                else
                    lastTextIndex = tempIdx;

                // Advance implicit cursor
                SearchBox.this.searchData.phraseCount[tabIndex]--;
                if(SearchBox.this.searchData.totalOccurrences[tabIndex] <= MAX_HIGHLIGHT_OCCURRENCES)
                    SearchBox.this.searchHighlighter.highlightAll(indexes, tabIndex, currentPhrase, false, lastTextIndex);
                else
                    SearchBox.this.searchHighlighter.highlightSelected(tabIndex, lastTextIndex, currentPhrase.length());
            }

            if(SearchBox.this.searchData.totalOccurrences[tabIndex] == 0)
                setCountLblTxt(0, SearchBox.this.searchData.totalOccurrences[tabIndex]);
            else
                setCountLblTxt(SearchBox.this.searchData.phraseCount[tabIndex], SearchBox.this.searchData.totalOccurrences[tabIndex]);
        }

        /**
         *  Sets the text on the count label.  Used for updating the current
         *  entry being looked at
         *
         * @param count             the current entry being looked at
         * @param occurrences       the maximum number of times the user-looked-up
         *                          phrase was found in the text pane
         */
        private void setCountLblTxt(int count, int occurrences) {
            int index = editorContainer.getTabbedPane().getSelectedIndex();
            searchData.results[index] = String.format("%d/%d results", count, occurrences);

            SearchBox.this.countLabel.setText(searchData.results[index]);
        }
    }
}
