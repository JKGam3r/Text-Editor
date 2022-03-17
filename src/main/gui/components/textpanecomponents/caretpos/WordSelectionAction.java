package main.gui.components.textpanecomponents.caretpos;

import main.data.LineData;

import java.awt.event.*;
import javax.swing.*;
import javax.swing.text.*;

/**
 * Controls how much of the text is selected when the user
 * double clicks the text pane with the mouse
 *
 * @author Justin Kocur
 */
public class WordSelectionAction extends TextAction {
    /**
     * Constructor with appropriate call to super constructor
     */
    public WordSelectionAction() {
        super("Select Word");
    }

    /**
     *  Selects appropriate part of the text when called
     *
     * @param e     used to get text component to access
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        JTextComponent target = getTextComponent(e);

        if (target != null && target.getText().length() > 0) {
            String text     = target.getText() + "\r\n";
            int lineNumber  = LineData.getLineNumber((JTextPane) target);
            int offset      = target.getCaretPosition() + lineNumber;

            int start   = beginWord(text, offset, lineNumber);
            int end     = endWord(text, offset, lineNumber);

            target.setCaretPosition(start);
            target.moveCaretPosition(end);
        }
    }

    /**
     *  Finds the start position of the text to select
     *
     * @param text      text in the text component
     * @param offset    position of the caret before selection
     * @return          the start position (index) for the text selection
     */
    private int beginWord(String text, int offset, int lineNumber) {
        // Find all extra characters that should be included
        boolean startFound  = false;
        int beginOffset     = offset;

        // Char detected at offset
        char c1 = text.charAt(Math.max(beginOffset - 1, 0));

        // Char on other side of caret
        char c2 = beginOffset < text.length() ? text.charAt(beginOffset) : c1;

        for(int i = beginOffset - 1; i >= 0; i--) {
            if(!isNumberChar(c1) || (!isNumberChar(c2) && c2 != '\r'))
                break;

            if(text.charAt(i) == ' ' || i == 0)
                return Math.max(i - lineNumber, 0);
            else if(text.charAt(i) == '\n')
                return Math.max(i - lineNumber + 1, 0);

            if(!isNumberChar(text.charAt(i)))
                break;
        }

        for (int i = beginOffset - 1; i >= 0 && !startFound && text.charAt(i) != '\r'; i--) {
            if(isStoppingChar(c1) && isStoppingChar(c2)) {
                if (text.charAt(i) != c1)
                    startFound = true;
                else
                    beginOffset--;
            } else {
                if (isStoppingChar(text.charAt(i)))
                    startFound = true;
                else
                    beginOffset--;
            }
        }

        return Math.max(beginOffset - lineNumber, 0);
    }

    /**
     *  Finds the end position of the text to select
     *
     * @param text      text in the text component
     * @param offset    position of the caret before selection
     * @return          the end position (index) for the text selection
     */
    private int endWord(String text, int offset, int lineNumber) {
        // Find all extra characters that should be included
        int length          = text.length();
        int endOffset       = offset;
        boolean endFound    = false;

        // Char detected at offset
        char c1 = text.charAt(Math.max(endOffset - 1, 0));

        // Char on other side of caret
        char c2 = endOffset < text.length() ? text.charAt(endOffset) : c1;

        boolean nLFirst = false;

        for(int i = endOffset; i < length; i++) {
            if(text.charAt(i) == '\n' && i == endOffset)
                nLFirst = true;

            if((!isNumberChar(c1) && c1 != '\r') || (!isNumberChar(c2) && c2 != '\n'))
                break;

            if(text.charAt(i) == ' '
                    || text.charAt(i) == '\r'
                    || i == length - 1) {
                if(nLFirst)
                    return Math.min(i - lineNumber, length - lineNumber - 3);
                else
                    return Math.min(i - lineNumber, length);
            }

            if(!isNumberChar(text.charAt(i)))
                if(text.charAt(i) != '\n')
                    break;
        }

        for (int i = endOffset; i < length && !endFound; i++) {
            if(c2 == '\n'
                && endOffset + 1 < text.length()) {
                c2 = text.charAt(endOffset + 1);
            } else {
                if (isStoppingChar(c1) && isStoppingChar(c2)) {
                    if (text.charAt(i) != c1)
                        endFound = true;
                    else
                        endOffset++;
                } else {
                    if (isStoppingChar(text.charAt(i)))
                        endFound = true;
                    else
                        endOffset++;
                }
            }
        }

        return Math.min(endOffset - lineNumber, length);
    }

    private boolean isNumberChar(char c) {
        return c == '.' || (c >= '0' && c <= '9');
    }

    /**
     *  Determines if a character is a language char (alphabet),
     *  a number, or underscore
     *
     * @param c     the char to check
     * @return      true if stopping character, false if not
     *              (IS a language char, number, or underscore)
     */
    private boolean isStoppingChar(char c) {
        return (c < 'a' || c > 'z')
                && (c < 'A' || c > 'Z')
                && (c < 128 || c > 165) // non-English
                && (c < '0' || c > '9') // numbers
                && c != '_';
    }
}
