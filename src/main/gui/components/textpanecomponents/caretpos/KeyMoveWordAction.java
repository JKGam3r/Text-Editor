package main.gui.components.textpanecomponents.caretpos;

import main.data.LineData;

import javax.swing.*;
import javax.swing.text.JTextComponent;
import javax.swing.text.TextAction;
import java.awt.event.ActionEvent;

/**
 * Selects a part of the text when either the left or right arrow keys are used
 * in conjunction with a modifier key(s)
 *
 * @author Justin Kocur
 */
public class KeyMoveWordAction extends TextAction {
    /** Use for left arrow key control over the caret */
    public static final int LEFT_ARROW = 0;

    /** Use for right arrow key control over the caret */
    public static final int RIGHT_ARROW = 1;

    // Left or right arrow direction specified in constructor
    private final int ARROW;

    /**
     * Constructor with appropriate call to super constructor
     *
     * @param ARROW     direction in which the caret is moving
     */
    public KeyMoveWordAction(final int ARROW) {
        super("Key Select Word");

        this.ARROW = ARROW;
    }

    /**
     *  Moves to appropriate part of the text when called
     *
     * @param e     used to get text component to access
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        JTextComponent target = getTextComponent(e);

        if (target != null && target.getText().length() > 0) {
            int lineNumber  = LineData.getLineNumber((JTextPane) target);
            int offset      = target.getCaretPosition() + lineNumber;

            // Can't go any further
            if(ARROW == LEFT_ARROW && offset <= 0)
                return;
            if(ARROW == RIGHT_ARROW && offset >= target.getText().length())
                return;

            if(ARROW == LEFT_ARROW) {
                target.setCaretPosition(moveLeft(target, offset, lineNumber));
            } else if(ARROW == RIGHT_ARROW) {
                target.setCaretPosition(moveRight(target, offset, lineNumber));
            }
        }
    }

    /**
     *  Moves the caret some amount to the left
     *
     * @param target            text component to select on
     * @param offset            position of the caret before selection
     * @return                  the new position in the text to move to
     */
    private int moveLeft(JTextComponent target, int offset, int lineNumber) {
        // Find all extra characters that should be included
        String text         = target.getText();
        boolean startFound  = false;
        int beginOffset     = offset;

        // Char detected at offset
        char c1 = text.charAt(Math.max(beginOffset - 1, 1));

        if(isNumberChar(c1)) {
            for (int i = beginOffset - 1; i >= 0; i--) {
                if (!isNumberChar(text.charAt(i))
                        || i == 0) {
                    return i == 0 ? 0 : i - lineNumber + 1;
                }
            }
        }

        for (int i = beginOffset - 1; i >= 0 && !startFound; i--) {
            if(isStoppingChar(c1)) {
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
     *  Moves the caret some amount to the right
     *
     * @param target            text component to select on
     * @param offset            position of the caret before selection
     * @return                  the new position in the text to move to
     */
    private int moveRight(JTextComponent target, int offset, int lineNumber) {
        // Find all extra characters that should be included
        String text         = target.getText();
        int length          = target.getDocument().getLength();
        int endOffset       = offset /*- lineNumber*/;
        boolean endFound    = false;

        // Char detected at offset
        char c1 = text.charAt(Math.min(endOffset, text.length() - 1));

        int n = 0;

        if(c1 == '\n')
            n++;

        if(isNumberChar(c1) || c1 == '\r') {
            for(int i = endOffset + 1; i < length; i++) {
                if (!isNumberChar(text.charAt(i))
                        || i == length - 1) {
                    if(c1 == '\r')
                        return i - lineNumber;

                    return i == length - 1 ? length : i - lineNumber - n;
                }
            }
        }

        for (int i = endOffset; i < length + lineNumber + n && !endFound; i++) {
            if (isStoppingChar(c1) && c1 != '\n') {
                if (text.charAt(i) != c1)
                    endFound = true;
                else
                    endOffset++;
            } else {
                if (isStoppingChar(text.charAt(i)) && text.charAt(i) != '\n')
                    endFound = true;
                else
                    endOffset++;
            }
        }

        if(c1 == '\n')
            endOffset--;

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
