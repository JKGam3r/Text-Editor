package main.data;

import main.gui.EditorContainer;

import java.awt.*;

/**
 * Holds the colors for each part of the GUI
 *
 * @author Justin Kocur
 */
// include different styles as public static
public class ColorData {
    /* STYLES ~ sets a color scheme */

    /** Uses the default colors for the editor */
    public static final int DEFAULT = 0;

    /** Halloween ~ black, purple, orange, green */
    public static final int HALLOWEEN = 1;

    /* The current colors for each part of the editor */

    // The background color of the selected text
    private Color bgSelectedText;

    // The color for the text pane's background
    private Color bgTextPane;

    /* The color for the numbering system's background (located next
       to the text pane) */
    private Color bgTextPaneNo;

    // The color of all borders in the editor
    private Color borders;

    // The color of the caret, located on the text pane
    private Color caretColor;

    // The color of the line that the caret is located on
    private Color currentLineHighlight;

    // Used to get each GUI component
    private EditorContainer editorContainer;

    // The color of the text pane's foreground; i.e. font text
    private Color fgTextPane;

    // The color of the numbering system's foreground; i.e. font text
    private Color fgTextPaneNo;

    // The foreground color of the selected text
    private Color fgSelectedText;

    /* The color of the text in the numbering system on the line that
       the caret is currently on */
    private Color fgSelTextPaneNo;

    // The background color for buttons, input boxes, etc
    private Color guiCompBackground;

    // The color of the menu text, pop-ups, etc. text
    private Color guiTextColor;

    // The colors of the panels (e.g. search box, file tree, etc)
    private Color panels;

    /* The color of the border (a line) separating the numbering system
       the text pane */
    private Color tPBorder;

    /* The color of a line underneath a tab in the editor representing
       the file selected */
    private Color selTab;

    // The color scheme for this ColorData instance
    private int style;

    /**
     * Creates a new ColorData class instance with the default color scheme
     *
     * @param editorContainer   holds all components; used to get colors of each component
     */
    public ColorData(EditorContainer editorContainer) {
        this.style = DEFAULT;
    }

    /**
     *  Allows for a custom color scheme
     *
     * @param editorContainer   holds all components; used to get colors of each component
     * @param STYLE             the type of color scheme to use
     */
    public ColorData(EditorContainer editorContainer, final int STYLE) {
        this.editorContainer = editorContainer;

        this.style = STYLE;

        determineStyle(STYLE);
    }

    /**
     * Returns the background color of the selected text
     *
     * @return  the background color of the selected text
     */
    public Color getBgSelectedText() {
        return bgSelectedText;
    }

    /**
     * Sets the background color of the selected text
     *
     * @param bgSelectedText  the background color of the selected text
     */
    public void setBgSelectedText(Color bgSelectedText) {
        this.bgSelectedText = bgSelectedText;

        for(int i = 0; i < editorContainer.getFileData().numTabsOpen(); i++) {
            if(editorContainer.getTextPane(i) != null)
                editorContainer.getTextPane(i).setSelectionColor(bgSelectedText);
        }
    }

    /**
     * Returns the color for the text pane's background
     *
     * @return  the color for the text pane's background
     */
    public Color getBgTextPane() {
        return bgTextPane;
    }

    /**
     * Sets the color for the text pane's background
     *
     * @param  bgTextPane  the color for the text pane's background
     */
    public void setBgTextPane(Color bgTextPane) {
        this.bgTextPane = bgTextPane;

        for(int i = 0; i < editorContainer.getFileData().numTabsOpen(); i++) {
            if(editorContainer.getTextPane(i) != null)
                editorContainer.getTextPane(i).setBackground(bgTextPane);
        }
    }

    /**
     * Returns the color for the numbering system's background (located next
     * to the text pane)
     *
     * @return  the color for the numbering system's background (located next
     * to the text pane)
     */
    public Color getBgTextPaneNo() {
        return bgTextPaneNo;
    }

    /**
     * Sets the color for the numbering system's background (located next
     * to the text pane)
     *
     * @param bgTextPaneNo  the color for the numbering system's background (located next
     * to the text pane)
     */
    public void setBgTextPaneNo(Color bgTextPaneNo) {
        this.bgTextPaneNo = bgTextPaneNo;

        for(int i = 0; i < editorContainer.getFileData().numTabsOpen(); i++) {
            if(editorContainer.getTextPane(i) != null
                    && editorContainer.getTextPane(i).getNumberingSystem() != null)
                editorContainer.getTextPane(i).getNumberingSystem().setBackgroundColor(bgTextPaneNo);
        }
    }

    /**
     *  Returns the color of all borders in the editor
     *
     * @return  the color of all borders in the editor
     */
    public Color getBorders() {
        return borders;
    }

    /**
     *  Sets the color of all borders in the editor
     *
     * @param borders  the color of all borders in the editor
     */
    public void setBorders(Color borders) {
        this.borders = borders;

        if(editorContainer.getFillerBox() != null)
            editorContainer.getFillerBox().setBorderColor(borders);

        if(editorContainer.getSearchBox() != null)
            editorContainer.getSearchBox().setBorderColor(borders);

        if(editorContainer.getReplaceBox() != null)
            editorContainer.getReplaceBox().setBorderColor(borders);

        if(editorContainer.getFillerBox() != null)
            editorContainer.getTabbedPane().setBorderColor(borders);

        if(editorContainer.getFileTree() != null)
            editorContainer.getFileTree().setBorderColor(borders);

        editorContainer.setFrameBorderColor(borders);
    }

    /**
     *  Returns the color of the caret, located on the text pane
     *
     * @return  the color of the caret, located on the text pane
     */
    public Color getCaretColor() {
        return caretColor;
    }

    /**
     *  Sets the color of the caret, located on the text pane
     *
     * @param caretColor  the color of the caret, located on the text pane
     */
    public void setCaretColor(Color caretColor) {
        this.caretColor = caretColor;

        for(int i = 0; i < editorContainer.getFileData().numTabsOpen(); i++) {
            if(editorContainer.getTextPane(i) != null)
                editorContainer.getTextPane(i).setCustomCaretColor(caretColor);
        }
    }

    /**
     *  Returns the color of the line that the caret is located on
     *
     * @return  the color of the line that the caret is located on
     */
    public Color getCurrentLineHighlight() {
        return currentLineHighlight;
    }

    /**
     *  Sets the color of the line (background) that the caret is located on
     *
     * @param currentLineHighlight  the color of the line that the caret is located on
     */
    public void setCurrentLineHighlight(Color currentLineHighlight) {
        this.currentLineHighlight = currentLineHighlight;

        for(int i = 0; i < editorContainer.getFileData().numTabsOpen(); i++) {
            if(editorContainer.getTextPane(i) != null) {
                editorContainer.getTextPane(i).setSelHighlightColor(currentLineHighlight);

                if(editorContainer.getTextPane(i).getNumberingSystem() != null)
                editorContainer.getTextPane(i).getNumberingSystem().setCurrentLineBackground(currentLineHighlight);
            }
        }
    }

    /**
     *  Returns the color of the text pane's foreground; i.e. font text
     *
     * @return  the color of the text pane's foreground; i.e. font text
     */
    public Color getFgTextPane() {
        return fgTextPane;
    }

    /**
     *  Sets the color of the text pane's foreground; i.e. font text
     *
     * @param fgTextPane  the color of the text pane's foreground; i.e. font text
     */
    public void setFgTextPane(Color fgTextPane) {
        this.fgTextPane = fgTextPane;

        for(int i = 0; i < editorContainer.getFileData().numTabsOpen(); i++) {
            if(editorContainer.getTextPane(i) != null)
                editorContainer.getTextPane(i).setForeground(fgTextPane);
        }
    }

    /**
     *  Returns the color of the numbering system's foreground; i.e. font text
     *
     * @return  the color of the numbering system's foreground; i.e. font text
     */
    public Color getFgTextPaneNo() {
        return fgTextPaneNo;
    }

    /**
     *  Sets the color of the numbering system's foreground; i.e. font text
     *
     * @param fgTextPaneNo  the color of the numbering system's foreground; i.e. font text
     */
    public void setFgTextPaneNo(Color fgTextPaneNo) {
        this.fgTextPaneNo = fgTextPaneNo;

        for(int i = 0; i < editorContainer.getFileData().numTabsOpen(); i++) {
            if(editorContainer.getTextPane(i) != null
                && editorContainer.getTextPane(i).getNumberingSystem() != null)
                editorContainer.getTextPane(i).getNumberingSystem().setForeground(fgTextPaneNo);
        }
    }

    /**
     *  Returns the foreground color of the selected text
     *
     * @return  the foreground color of the selected text
     */
    public Color getFgSelectedText() {
        return fgSelectedText;
    }

    /**
     *  Sets the foreground color of the selected/ highlighted text
     *
     * @param fgSelectedText  the foreground color of the selected/ highlighted text
     */
    public void setFgSelectedText(Color fgSelectedText) {
        this.fgSelectedText = fgSelectedText;

        for(int i = 0; i < editorContainer.getFileData().numTabsOpen(); i++) {
            if(editorContainer.getTextPane(i) != null)
                editorContainer.getTextPane(i).setSelectedTextColor(fgSelectedText);
        }
    }

    /**
     *  Returns the color of the text in the numbering system on the line that
     *  the caret is currently on
     *
     * @return  the color of the text in the numbering system on the line that
     * the caret is currently on
     */
    public Color getFgSelTextPaneNo() {
        return fgSelTextPaneNo;
    }

    /**
     *  Sets the color of the text in the numbering system on the line that
     *  the caret is currently on
     *
     * @param fgSelTextPaneNo  the color of the text in the numbering system on the line that
     * the caret is currently on
     */
    public void setFgSelTextPaneNo(Color fgSelTextPaneNo) {
        this.fgSelTextPaneNo = fgSelTextPaneNo;

        for(int i = 0; i < editorContainer.getFileData().numTabsOpen(); i++) {
            if(editorContainer.getTextPane(i) != null
                && editorContainer.getTextPane(i).getNumberingSystem() != null)
                editorContainer.getTextPane(i).getNumberingSystem().setCurrentLineForeground(fgSelTextPaneNo);
        }
    }

    /**
     *  Returns the background color for buttons, input boxes, etc
     *
     * @return  the background color for buttons, input boxes, etc
     */
    public Color getGuiCompBackground() {
        return guiCompBackground;
    }

    /**
     *  Sets the background color for buttons, input boxes, etc
     *
     * @param guiCompBackground  the background color for buttons, input boxes, etc
     */
    public void setGuiCompBackground(Color guiCompBackground) {
        this.guiCompBackground = guiCompBackground;

        if(editorContainer.getFillerBox() != null)
            editorContainer.getFillerBox().setBackgroundColor(guiCompBackground);

        if(editorContainer.getSearchBox() != null)
            editorContainer.getSearchBox().setBackgroundColor(guiCompBackground);

        if(editorContainer.getReplaceBox() != null)
            editorContainer.getReplaceBox().setBackgroundColor(guiCompBackground);
    }

    /**
     * Returns the color of the menu text, pop-ups, etc. text
     *
     * @return  the color of the menu text, pop-ups, etc. text
     */
    public Color getGuiTextColor() {
        return guiTextColor;
    }

    /**
     * Sets the color of the menu, pop-ups, etc. text
     *
     * @param guiTextColor  the color of the menu, pop-ups, etc. text
     */
    public void setGuiTextColor(Color guiTextColor) {
        this.guiTextColor = guiTextColor;

        if(editorContainer.getFillerBox() != null)
            editorContainer.getFillerBox().setTextColor(guiTextColor);

        if(editorContainer.getReplaceBox() != null)
            editorContainer.getReplaceBox().setTextColor(guiTextColor);

        if(editorContainer.getSearchBox() != null)
            editorContainer.getSearchBox().setTextColor(guiTextColor);

        if(editorContainer.getTabbedPane() != null)
            editorContainer.getTabbedPane().setForeground(guiTextColor);

        if(editorContainer.getMenuBar() != null) {
            for (Component c : editorContainer.getMenuBar().getComponents())
                c.setForeground(guiTextColor);
        }
    }

    /**
     *  Returns the colors of the panels (e.g. search box, file tree, etc)
     *
     * @return  the colors of the panels (e.g. search box, file tree, etc)
     */
    public Color getPanels() {
        return panels;
    }

    /**
     *  Sets the colors of the panels (e.g. search box, file tree, etc)
     *
     * @param panels  the colors of the panels (e.g. search box, file tree, etc)
     */
    public void setPanels(Color panels) {
        this.panels = panels;

        if(editorContainer.getFileTree() != null)
            editorContainer.getFileTree().setBackgroundColor(panels);

        if(editorContainer.getFillerBox() != null)
            editorContainer.getFillerBox().setBackground(panels);

        if(editorContainer.getSearchBox() != null)
            editorContainer.getSearchBox().setBackground(panels);

        if(editorContainer.getReplaceBox() != null)
            editorContainer.getReplaceBox().setBackground(panels);

        if(editorContainer.getTabbedPane() != null)
            editorContainer.getTabbedPane().setBackground(panels);

        if(editorContainer.getMenuBar() != null)
            editorContainer.getMenuBar().setBackground(panels);
    }

    /**
     *  Returns the color of the border (a line) separating the numbering system
     *  the text pane
     *
     * @return  the color of the border (a line) separating the numbering system
     * the text pane
     */
    public Color gettPBorder() {
        return tPBorder;
    }

    /**
     *  Sets the color of the border (a line) separating the numbering system
     *  the text pane
     *
     * @param tPBorder  the color of the border (a line) separating the numbering system
     * the text pane
     */
    public void settPBorder(Color tPBorder) {
        this.tPBorder = tPBorder;

        for(int i = 0; i < editorContainer.getFileData().numTabsOpen(); i++) {
            if(editorContainer.getTextPane(i) != null
                && editorContainer.getTextPane(i).getNumberingSystem() != null)
                editorContainer.getTextPane(i).getNumberingSystem().setSepBorder(tPBorder);
        }
    }

    /**
     *  Returns the color of a line underneath a tab in the editor representing
     *  the file selected
     *
     * @return  the color of a line underneath a tab in the editor representing
     * the file selected
     */
    public Color getSelTab() {
        return selTab;
    }

    /**
     *  Sets the color of a line underneath a tab in the editor representing
     *  the file selected
     *
     * @param selTab  the color of a line underneath a tab in the editor representing
     * the file selected
     */
    public void setSelTab(Color selTab) {
        this.selTab = selTab;

        if(editorContainer.getTabbedPane() != null)
            editorContainer.getTabbedPane().setSelTabColor(selTab);
    }

    /**
     *  Returns the color scheme for this ColorData instance
     *
     * @return  the color scheme for this ColorData instance
     */
    public int getStyle() {
        return style;
    }

    /**
     *  Sets the color scheme for this ColorData instance
     *
     * @param style  the color scheme for this ColorData instance
     */
    public void setStyle(int style) {
        this.style = style;

        determineStyle(style);
    }

    /**
     * Uses the default colors for the text editor
     */
    private void defaultStyle() {
        setBgTextPane(Color.WHITE);
        setBgTextPaneNo(Color.WHITE);
        setBgSelectedText(new Color(20, 180, 210));
        setBorders(new Color(0, 0, 0, 75));
        setCaretColor(Color.BLACK);
        setCurrentLineHighlight(new Color(0, 0, 0, 25));
        setFgTextPane(Color.BLACK);
        setFgTextPaneNo(Color.BLACK);
        setFgSelectedText(Color.WHITE);
        setFgSelTextPaneNo(new Color(175, 110, 150));
        setGuiCompBackground(Color.WHITE);
        setGuiTextColor(Color.BLACK);
        setPanels(Color.WHITE);
        settPBorder(new Color(0, 0, 0, 125));
        setSelTab(new Color(100, 150, 150));
    }

    /**
     * Uses the Halloween style for the text editor
     */
    private void halloweenStyle() {
        Color purple = new Color(136, 30, 228);
        Color orange = new Color(255, 154, 0);
        Color green  = new Color(133, 226, 31);

        setBgTextPane(orange);
        setBgTextPaneNo(orange);
        setBgSelectedText(green);
        setBorders(Color.BLACK);
        setCaretColor(purple);
        setCurrentLineHighlight(new Color(0, 0, 0, 25));
        setFgTextPane(purple);
        setFgTextPaneNo(Color.BLACK);
        setFgSelectedText(Color.BLACK);
        setFgSelTextPaneNo(green);
        setGuiCompBackground(orange);
        setGuiTextColor(green);
        setPanels(purple);
        settPBorder(new Color(0, 0, 0, 125));
        setSelTab(green);
    }

    /**
     *  Determines which style has been chosen and calls the appropriate method
     *  to change all parts of the editor
     *
     * @param STYLE     the selected style - color scheme - for the editor
     */
    private void determineStyle(final int STYLE) {
        if(editorContainer == null || editorContainer.getFileData() == null)
            return;

        switch(STYLE) {
            case HALLOWEEN:
                halloweenStyle();
                break;
            default:
                defaultStyle();
                break;
        }
    }
}
