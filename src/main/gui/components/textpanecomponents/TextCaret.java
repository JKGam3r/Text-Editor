package main.gui.components.textpanecomponents;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultCaret;
import javax.swing.text.JTextComponent;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.geom.Rectangle2D;

/**
 * Based off of the DefaultCaret, this class modifies the interaction
 * between selected text and mouse events, along with changing the
 * way the caret is painted.
 * More specifically, there are two major modifications:
 *      1)  After text is selected, dragging the mouse from the
 *      selected area to a different area of the text component
 *      will move that text from its original position to the
 *      new position.
 *      2)  The paint() function has been overridden to create
 *      a new custom caret that can be modified in shape and
 *      color as needed.
 *
 * @author Justin Kocur
 */
public class TextCaret extends DefaultCaret {
    // The color of the caret
    private Color caretColor;

    // True if caret should be drawn, false if not
    private boolean caretVisible;

    /* The component to which this caret belongs to.
       Used for accessing certain methods from the
       component for modified behavior. */
    private JTextComponent component;

    // True if mouse being dragged, false if not
    private boolean dragged;

    // The selected text to move to a new location; null if none
    private String dragItem;

    private Point dragPoint;

    // The last time the visibility of the caret was updated
    private long lastBlinkTime;

    /**
     *  Creates a new TextCaret instance.
     *  Initially sets the caret visibility on, blink rate
     *  to 500 (blink every 0.5 sec), and adds a new
     *  ChangeListener that keeps the caret visible when
     *  the text component is being updated (more specifically
     *  when the caret state is changed).
     *
     * @param component     text component that this caret class
     *                      belongs to
     */
    public TextCaret(JTextComponent component) {
        this.component = component;

        caretColor      = Color.black;
        caretVisible    = true;
        lastBlinkTime   = System.currentTimeMillis();

        setBlinkRate(500);

        addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                keepCaretVisible();
            }
        });
    }

    /**
     *  Returns the color of the caret
     *
     * @return  the color of the caret
     */
    public Color getCaretColor() {
        return caretColor;
    }

    /**
     *  Sets the color of the caret
     *
     * @param caretColor  the color of the caret
     */
    public void setCaretColor(Color caretColor) {
        this.caretColor = caretColor;
    }

    /**
     *  Allows for the un-selection of text
     *
     * @param e     MouseEvent instance
     */
    @Override
    public void mouseClicked(MouseEvent e) {
        if(e.getClickCount() == 1)
            setDot(position(e.getPoint()));
    }

    /**
     *  Determines if something is being dragged
     *
     * @param e     MouseEvent instance
     */
    @Override
    public void mouseDragged(MouseEvent e) {
        if (dragItem != null) {
            component.setCursor(Cursor.getDefaultCursor());
            dragged = true;
            dragPoint = e.getPoint();

            keepCaretVisible();
        } else
            moveDot(position(e.getPoint()));
    }

    /**
     *  Retrieves the selected part of the text component
     *
     * @param e     MouseEvent instance
     */
    @Override
    public void mousePressed(MouseEvent e) {
        int mx = e.getX();
        int my = e.getY();

        int bX = -1;
        int bY = -1;

        if(component.getSelectedText() != null) {
            try {
                Rectangle2D r = component.modelToView2D(component.getSelectionEnd());

                bX = (int) r.getX();
                bY = (int) r.getY();
            } catch (BadLocationException badLocationException) {
                badLocationException.printStackTrace();
            }
        }

        int pos = position(e.getPoint());

        if (component.getSelectedText() != null
                && pos >= component.getSelectionStart()
                && pos <= component.getSelectionEnd()
        && mx <= bX && my <= bY) {
            dragItem = component.getSelectedText();
            return;
        }

        super.mousePressed(e);

        if(e.getClickCount() == 1)
            setDot(pos);
    }

    /**
     *  Moves any selected text to the designated position
     *
     * @param e     MouseEvent instance
     */
    @Override
    public void mouseReleased(MouseEvent e) {
        if(dragItem != null && dragged) {
            try {
                int pos         = position(e.getPoint());
                int removeIdx   = pos < component.getSelectionStart()
                        ? component.getSelectionStart() + dragItem.length()
                        : component.getSelectionStart();

                component.getDocument().insertString(pos, dragItem, null);

                component.setCaretPosition(pos + dragItem.length());
                component.moveCaretPosition(pos);

                component.getDocument().remove(removeIdx,
                        component.getSelectionEnd() - component.getSelectionStart());

            } catch (BadLocationException badLocationException) {
                badLocationException.printStackTrace();
            }

            component.setCursor(new Cursor(Cursor.TEXT_CURSOR));
        }

        dragged = false;
        dragItem = null;
        dragPoint = null;
    }

    /**
     *  Paints a new custom caret
     *
     * @param g     Graphics content
     */
    @Override
    public void paint(Graphics g) {
        damage(getBounds());

        if(!isActive()) {
            caretVisible = false;
            return;
        }

        if(System.currentTimeMillis() - lastBlinkTime >= getBlinkRate()) {
            caretVisible = !caretVisible;
            lastBlinkTime = System.currentTimeMillis();
        }

        if(!caretVisible)
            return;

        try {
            Rectangle2D r;
            if(dragged)
                r = component.modelToView2D(position(dragPoint));
            else
                r = component.modelToView2D(component.getCaretPosition());

            int strW        = 2;
            int margY       = 2;

            Graphics2D g2d = (Graphics2D) g;
            g2d.setStroke(new BasicStroke(strW));
            g2d.setColor(caretColor);
            g2d.drawLine((int) (r.getX() - strW / 2) + 1, (int) (r.getY() + margY),
                    (int) (r.getX() - strW / 2) + 1, (int) (r.getY() + r.getHeight() - margY));
        } catch (BadLocationException e) {
            e.printStackTrace();
        }
    }

    private void keepCaretVisible() {
        lastBlinkTime = System.currentTimeMillis();
        caretVisible = true;
    }

    private int position(Point pt) {
        int p               = 0;
        String text         = component.getText();
        int charWidth       = component.getGraphics().getFontMetrics()
                                .charWidth('a'); // all chars same width
        double fudgeFactor  = 1.0 / 3.0;
        double currentWidth = 0;
        int lineHeight      = component.getGraphics().getFontMetrics()
                                .getHeight();
        int currentHeight   = 0;
        int currentLine     = 1;
        int lastLine        = component.getDocument().getDefaultRootElement().getElementCount();

        for(; p < text.length(); p++) {
            char c = text.charAt(p);

            if((currentWidth >= pt.x - charWidth && currentWidth <= pt.x + charWidth
                    || c == '\r')
                && ((currentHeight >= pt.y - lineHeight && currentHeight <= pt.y + lineHeight)
                        || currentLine == lastLine)) { // 'currentLine == lastLine' if cursor far below last line of text
                --currentLine;
                return c == '\n' ? p - currentLine + 1 : p - currentLine;
            }
            else {
                if(c == '\r') {
                    currentWidth = 0;
                    currentHeight += lineHeight;

                    currentLine++;
                } else if(c != '\n')
                    currentWidth += (charWidth - fudgeFactor);
            }
        }

        if(text.length() <= 0)
            return 0;
        return p - currentLine + 1;
    }
}