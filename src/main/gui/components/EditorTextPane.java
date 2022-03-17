package main.gui.components;

import main.data.ColorData;
import main.data.LineData;
import main.gui.components.textpanecomponents.TextCaret;
import main.gui.components.textpanecomponents.TextLineNumber;

import javax.swing.*;
import javax.swing.text.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.geom.Rectangle2D;

/**
 * CREDIT: https://stackoverflow.com/questions/5405550/highlight-current-row-in-jtextpane
 */
public class EditorTextPane extends JTextPane {

    // The TextLineNumber class instance used for this text pane
    private TextLineNumber numberingSystem;

    // The caret being used for this text pane
    private final TextCaret textCaret;

    // The color of the selected line (caret line)
    private Color selHighlightColor;

    /**
     *  Constructs a new text pane
     */
    public EditorTextPane() {
        super();

        textCaret = new TextCaret(this);

        selHighlightColor = new Color(0, 0, 0, 25);

        // Has to be marked as transparent so the background is not replaced by
        // super.paintComponent(g);
        setOpaque(false);
        setSelectionColor(new Color(20, 180, 210));
        setCaret(textCaret);

        addKeyListener(new KeyAdapter() {
            boolean shiftPressed    = false;
            boolean ctrlPressed     = false;

            @Override
            public void keyPressed(KeyEvent e) {
                int key = e.getKeyCode();

                if(key == KeyEvent.VK_SHIFT)
                    shiftPressed = true;

                if(key == KeyEvent.CTRL_DOWN_MASK)
                    ctrlPressed = true;

                if(!shiftPressed && !ctrlPressed) {
                    if (getSelectedText() != null) {
                        if (key == KeyEvent.VK_LEFT)
                            setCaretPosition(getSelectionStart() + 1);
                        else if (key == KeyEvent.VK_RIGHT)
                            setCaretPosition(getSelectionEnd() - 1);
                    }
                }
            }

            @Override
            public void keyReleased(KeyEvent e) {
                if(e.getKeyCode() == KeyEvent.VK_SHIFT)
                    shiftPressed = false;
                if(e.getKeyCode() == KeyEvent.CTRL_DOWN_MASK)
                    ctrlPressed = false;
            }
        });
    }

    public EditorTextPane(ColorData colorData) {
        this();

        setSelectionColor(colorData.getBgSelectedText());
        setBackground(colorData.getBgTextPane());
        setCustomCaretColor(colorData.getCaretColor());
        setSelHighlightColor(colorData.getCurrentLineHighlight());
        setForeground(colorData.getFgTextPane());
        setSelectedTextColor(colorData.getFgSelectedText());
    }

    /**
     *  Returns the color of the caret
     *
     * @return  the color of the caret
     */
    public Color getCustomCaretColor() {
        return textCaret.getCaretColor();
    }

    /**
     *  Sets the color of the caret
     *
     * @param caretColor  the color of the caret
     */
    public void setCustomCaretColor(Color caretColor) {
        textCaret.setCaretColor(caretColor);
    }

    /**
     *  Returns the TextLineNumber class instance used for this text pane
     *
     * @return  the TextLineNumber class instance used for this text pane
     */
    public TextLineNumber getNumberingSystem() {
        return numberingSystem;
    }

    /**
     *  Sets the TextLineNumber class instance used for this text pane
     *
     * @param numberingSystem  the TextLineNumber class instance used for this text pane
     */
    public void setNumberingSystem(TextLineNumber numberingSystem) {
        this.numberingSystem = numberingSystem;
    }

    /**
     *  Returns the color of the selected line (caret line)
     *
     * @return  the color of the selected line (caret line)
     */
    public Color getSelHighlightColor() {
        return selHighlightColor;
    }

    /**
     *  Sets the color of the selected line (caret line)
     *
     * @param selHighlightColor  the color of the selected line (caret line)
     */
    public void setSelHighlightColor(Color selHighlightColor) {
        this.selHighlightColor = selHighlightColor;
    }

    public void addDocumentFilter() {
        ((AbstractDocument) getDocument()).setDocumentFilter(new DocumentFilter() {
            @Override
            public void remove(FilterBypass fb, int offset, int length)
                    throws BadLocationException {

                // Must not have any selected text to remove and that there is 1+ chars
                // after the char being removed
                if(getSelectedText() == null && offset + 1 < getText().length()) {
                    switch (getText().charAt(offset)) {
                        case '"':
                            if(getText().charAt(offset + 1) == '"')
                                length++;
                            break;

                        case '\'':
                            if(getText().charAt(offset + 1) == '\'')
                                length++;
                            break;

                        case '(':
                            if(getText().charAt(offset + 1) == ')')
                                length++;
                            break;

                        case '{':
                            if(getText().charAt(offset + 1) == '}')
                                length++;
                            break;

                        case '[':
                            if(getText().charAt(offset + 1) == ']')
                                length++;
                            break;
                    }
                }

                super.remove(fb, offset, length);
            }

            @Override
            public void replace(final FilterBypass fb, final int offset, final int length,
                                final String text, final AttributeSet attrs)
                    throws BadLocationException {

                // Text highlighted, check if "key" char is added; if so, wrap text around it
                if(getSelectedText() != null) {
                    int start   = getSelectionStart();
                    int end     = getSelectionEnd() - getSelectionStart() + 1;

                    switch (text) {
                        case "\"":
                            getDocument().insertString(start, "\"", null);
                            getDocument().insertString(start + end, "\"", null);
                            setSelectionEnd(start + end);
                            break;

                        case "'":
                            getDocument().insertString(start, "'", null);
                            getDocument().insertString(start + end, "'", null);
                            setSelectionEnd(start + end);
                            break;

                        case "(":
                            getDocument().insertString(start, "(", null);
                            getDocument().insertString(start + end, ")", null);
                            setSelectionEnd(start + end);
                            break;

                        case "{":
                            getDocument().insertString(start, "{", null);
                            getDocument().insertString(start + end, "}", null);
                            setSelectionEnd(start + end);
                            break;

                        case "[":
                            getDocument().insertString(start, "[", null);
                            getDocument().insertString(start + end, "]", null);
                            setSelectionEnd(start + end);
                            break;

                        default:
                            super.replace(fb, offset, length, text, attrs);
                    }
                }
                // Place in appropriate character if "cue" char entered
                else {
                    int caretStart = getCaretPosition();

                    // Closer already added; advance cursor and then exit
                    if((offset >= 0 && offset < getText().length())
                        && ((getText().charAt(offset) == '"' && text.equals("\""))
                        || (getText().charAt(offset) == '\'' && text.equals("'"))
                        || (getText().charAt(offset) == ')' && text.equals(")"))
                        || (getText().charAt(offset) == '}' && text.equals("}"))
                        || (getText().charAt(offset) == ']' && text.equals("]")))) {
                        if(offset - 1 >= 0) {
                            if(getText().charAt(offset - 1) != '\\') {
                                setCaretPosition(getCaretPosition() + 1);
                                return;
                            } else {
                                super.replace(fb, offset, length, text, attrs);
                                return;
                            }
                        } else {
                            setCaretPosition(getCaretPosition() + 1);
                            return;
                        }
                    }

                    // Add closer (indent appropriately for newline '\n')
                    switch (text) {
                        case "\"":
                            getDocument().insertString(caretStart, "\"", null);
                            getDocument().insertString(caretStart + 1, "\"", null);
                            setCaretPosition(caretStart + 1);
                            break;

                        case "'":
                            getDocument().insertString(caretStart, "'", null);
                            getDocument().insertString(caretStart + 1, "'", null);
                            setCaretPosition(caretStart + 1);
                            break;

                        case "(":
                            getDocument().insertString(caretStart, "(", null);
                            getDocument().insertString(caretStart + 1, ")", null);
                            setCaretPosition(caretStart + 1);
                            break;

                        case "{":
                            getDocument().insertString(caretStart, "{", null);
                            getDocument().insertString(caretStart + 1, "}", null);
                            setCaretPosition(caretStart + 1);
                            break;

                        case "[":
                            getDocument().insertString(caretStart, "[", null);
                            getDocument().insertString(caretStart + 1, "]", null);
                            setCaretPosition(caretStart + 1);
                            break;

                        default:
                            super.replace(fb, offset, length, text, attrs);
                    }
                }
            }
        });
    }

    @Override
    public void repaint(long tm, int x, int y, int width, int height) {
        // This forces repaints to repaint the entire TextPane.
        super.repaint(tm, 0, 0, getWidth(), getHeight());
    }

    @Override
    protected void paintComponent(Graphics g) {
        g.setColor(getBackground());
        g.fillRect(0, 0, getWidth(), getHeight());
        try {
            Rectangle2D rect = modelToView2D(getCaretPosition());
            if (rect != null) {
                g.setColor(selHighlightColor);
                g.fillRect(0, (int) rect.getY(), getWidth(), (int) rect.getHeight());

                if(getSelectedText() != null) {
                    Rectangle2D startRect   = this.getUI().modelToView2D(this, getSelectionStart(), null);
                    Rectangle2D endRect   = this.getUI().modelToView2D(this, getSelectionEnd(), null);
                    int numLines            = LineData.numWholeLinesSelected(this);

                    g.setColor(getSelectionColor());

                    if(rect.getY() != startRect.getY()
                            || (getCaretPosition() == getSelectionStart() && startRect.getY() != endRect.getY()))
                        g.fillRect((int) startRect.getX(), (int) startRect.getY(), getWidth(), (int) rect.getHeight());

                    g.fillRect(getMargin().left, (int) (startRect.getY() + startRect.getHeight()),
                            getWidth(), (int) startRect.getHeight() * numLines);

                }
            }

        } catch (BadLocationException e) {
            System.err.println("Error repainting.");
        }

        super.paintComponent(g);
    }
}