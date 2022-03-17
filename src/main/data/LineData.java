package main.data;

import javax.swing.*;
import javax.swing.text.BadLocationException;
import javax.swing.text.Element;
import java.awt.*;
import java.util.LinkedList;

/**
 * Contains information regarding the various lines in each text pane (e.g. current
 * line caret is on)
 *
 * @author Justin Kocur
 */
public class LineData {
    /**
     * CREDIT: https://community.oracle.com/tech/developers/discussion/1355067/getting-current-line-from-jtextpane-or-am-i-stupid
     *
     * Gets the current line in a text pane
     *
     * @param textPane      the text pane to extract the current line of text
     * @return              the String on the given line
     */
    public static LineInfo currentLine(JTextPane textPane) {
        // Get section element
        Element section = textPane.getDocument().getDefaultRootElement();

        // Get number of paragraphs.
        // In a text pane, a span of characters terminated by single
        // newline is typically called a paragraph.
        int paraCount = section.getElementCount();

        int position = textPane.getCaret().getDot();

        // Get index ranges for each paragraph
        for (int i = 0; i < paraCount; i++)
        {
            Element e1 = section.getElement(i);

            int rangeStart = e1.getStartOffset();
            int rangeEnd = e1.getEndOffset();

            try
            {
                String para = textPane.getText(rangeStart, rangeEnd - rangeStart);

                if (position >= rangeStart && position <= rangeEnd)
                    return new LineInfo(para, rangeStart);
            }
            catch (BadLocationException ex)
            {
                System.err.println(ex.getMessage());
            }
        }

        return null;
    }

    public static int numWholeLinesSelected(JTextPane textPane) {
        if(textPane.getSelectedText() == null)
            return 0;

        int lineCount = 0;

        // Get section element
        Element section = textPane.getDocument().getDefaultRootElement();

        // Get number of paragraphs.
        // In a text pane, a span of characters terminated by single
        // newline is typically called a paragraph.
        int paraCount = section.getElementCount();

        // Get index ranges for each paragraph
        for (int i = 0; i < paraCount; i++)
        {
            Element e1 = section.getElement(i);

            int rangeStart = e1.getStartOffset();
            int rangeEnd = e1.getEndOffset();

            if ((rangeStart > textPane.getSelectionStart() && rangeStart < textPane.getSelectionEnd())
                    && ((rangeEnd > textPane.getSelectionStart() && rangeEnd < textPane.getSelectionEnd())
                    || rangeEnd >= textPane.getText().length() - 1)) {
                lineCount++;
            }
        }

        if(paraCount <= 3)
            lineCount--;

        return Math.max(lineCount, 0);
    }

    /**
     * CREDIT: https://community.oracle.com/tech/developers/discussion/1355067/getting-current-line-from-jtextpane-or-am-i-stupid
     *
     * Gets the current line number of the caret in a text pane
     *
     * @param textPane      the text pane to extract the current line number
     * @return              the number of the line the caret is on
     */
    public static int getLineNumber(JTextPane textPane) {
        // Get section element
        Element section = textPane.getDocument().getDefaultRootElement();

        // Get number of paragraphs.
        // In a text pane, a span of characters terminated by single
        // newline is typically called a paragraph.
        int paraCount = section.getElementCount();

        int position = textPane.getCaret().getDot();

        // Get index ranges for each paragraph
        for (int i = 0; i < paraCount; i++)
        {
            Element e1 = section.getElement(i);

            int rangeStart = e1.getStartOffset();
            int rangeEnd = e1.getEndOffset();

            if (position >= rangeStart && position <= rangeEnd)
                return i;
        }

        return -1;
    }

    /**
     * CREDIT: https://community.oracle.com/tech/developers/discussion/1355067/getting-current-line-from-jtextpane-or-am-i-stupid
     *
     * Gets the line number at a designated index
     *
     * @param textPane      the text pane to extract the current line number
     * @param index         the index at which to locate the line
     * @return              the number of the line the index is on
     */
    public static int getIndexLineNumber(JTextPane textPane, int index) {
        // Get section element
        Element section = textPane.getDocument().getDefaultRootElement();

        // Get number of paragraphs.
        // In a text pane, a span of characters terminated by single
        // newline is typically called a paragraph.
        int paraCount = section.getElementCount();

        // Get index ranges for each paragraph
        for (int i = 0; i < paraCount; i++)
        {
            Element e1 = section.getElement(i);

            int rangeStart = e1.getStartOffset() + i;
            int rangeEnd = e1.getEndOffset() + i;

            if (index >= rangeStart && index <= rangeEnd) {
                return i;
            }
        }

        return -1;
    }

    /**
     * CREDIT: https://community.oracle.com/tech/developers/discussion/1355067/getting-current-line-from-jtextpane-or-am-i-stupid
     *
     * Duplicates the current line that the caret is on, or the selected text, in a text pane
     *
     * @param textPane      the text pane to extract the current line of text
     */
    public static void duplicateLine(JTextPane textPane) {
        int caretPos            = textPane.getCaretPosition();
        int length              = 0;

        if(textPane.getSelectedText() != null) {
            int start   = textPane.getSelectionEnd();
            String text = textPane.getSelectedText();

            try {
                textPane.getStyledDocument().insertString(start, text, null);
            } catch(BadLocationException b) {
                System.err.println(b.getMessage());
            }

            return;
        }

        // Get section element
        Element section = textPane.getDocument().getDefaultRootElement();

        // Get number of paragraphs.
        // In a text pane, a span of characters terminated by single
        // newline is typically called a paragraph.
        int paraCount = section.getElementCount();

        int position = textPane.getCaret().getDot();

        // Get index ranges for each paragraph
        for (int i = 0; i < paraCount; i++)
        {
            Element e1      = section.getElement(i);
            int rangeStart  = e1.getStartOffset();
            int rangeEnd    = e1.getEndOffset();

            try
            {
                String para = textPane.getText(rangeStart, rangeEnd - rangeStart);

                if (position >= rangeStart && position < rangeEnd) {
                    length = para.length();
                    textPane.getStyledDocument().insertString(rangeStart, para, null);
                }
            }
            catch (BadLocationException ex)
            {
                System.err.println(ex.getMessage());
            }
        }

        textPane.setCaretPosition(caretPos + length);
        textPane.getCaret().setVisible(true);
    }

    /**
     * CREDIT: https://community.oracle.com/tech/developers/discussion/1355067/getting-current-line-from-jtextpane-or-am-i-stupid
     *
     * Joins the current line that the caret is on with the line below it. Does nothing if
     * no line below caret line
     *
     * @param textPane      the text pane to extract the current line of text
     */
    public static void joinLines(JTextPane textPane) {
        int caretPos            = textPane.getCaretPosition();
        int length              = 0;
        int topIdx              = -1;
        int topLen              = 0;

        // Get section element
        Element section = textPane.getDocument().getDefaultRootElement();

        // Get number of paragraphs.
        // In a text pane, a span of characters terminated by single
        // newline is typically called a paragraph.
        int paraCount = section.getElementCount();

        int position = textPane.getCaret().getDot();

        // Get index ranges for each paragraph
        for (int i = 0; i < paraCount; i++)
        {
            Element e1      = section.getElement(i);
            int rangeStart  = e1.getStartOffset();
            int rangeEnd    = e1.getEndOffset();

            try
            {
                String para = textPane.getText(rangeStart, rangeEnd - rangeStart);

                // Join lines here
                if(topIdx != -1) {
                    length = para.length();
                    textPane.getStyledDocument().insertString(topIdx + topLen - 1, para.replaceAll("\n", ""), null);
                    textPane.getStyledDocument().remove(i == paraCount - 1 ? topIdx + topLen + length - 2
                            : topIdx + topLen + length - 1,  length);

                    // Break necessary to avoid exceptions with 'rangeStart' and 'rangeEnd'
                    break;
                }

                if (position >= rangeStart && position < rangeEnd && i != paraCount - 1) {
                    topIdx = rangeStart;
                    topLen = para.length();
                }
            }
            catch (BadLocationException ex)
            {
                System.err.println(ex.getMessage());
            }
        }

        if(topIdx + topLen + length - 2 >= 0)
            textPane.setCaretPosition(topIdx + topLen + length - 2);
        textPane.getCaret().setVisible(true);
    }

    // index of the start line
    public static int startOfLine(JTextPane textPane, int index) {
        int count = 0;
        int save = 0;

        int i = 0;
        for(; i < index; i++) {
            if(textPane.getText().charAt(i) == '\n') {
                count += save;
                save = 0;
            } else {
                save += 1;
            }
        }

        if(textPane.getText().charAt(i) == '\n')
            count += save;

        return count;
    }

    /**
     * Contains information regarding a line of text.  Should be used for a text area, text pane,
     * or a similar component.
     *
     * @author Justin Kocur
     */
    public static class LineInfo {
        // Contains a line of text in a text pane
        private String line;

        // The start index of the String 'line' in the text pane
        private int startIdx;

        /**
         * Standard constructor with 'line' = null and 'startIdx' = 0
         */
        public LineInfo() {

        }

        /**
         * Creates a LineInfo instance with the designated parameters
         *
         * @param line          represents a line of text in a text pane
         * @param startIdx      the start index of the String 'line' in the text pane
         */
        public LineInfo(String line, int startIdx) {
            this.line = line;
            this.startIdx = startIdx;
        }

        /**
         * Returns the line of text associated with this LineInfo instance
         *
         * @return  the line of text associated with this LineInfo instance
         */
        public String getLine() {
            return line;
        }

        /**
         * Sets the line of text associated with this LineInfo instance
         *
         * @param line  the line of text associated with this LineInfo instance
         */
        public void setLine(String line) {
            this.line = line;
        }

        /**
         *  Returns the start index of the line of text
         *
         * @return  the start index of the line of text
         */
        public int getStartIdx() {
            return startIdx;
        }

        /**
         *  Sets the start index of the line of text
         *
         * @param startIdx  the start index of the line of text
         */
        public void setStartIdx(int startIdx) {
            this.startIdx = startIdx;
        }
    }
}
