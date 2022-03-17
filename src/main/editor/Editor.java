package main.editor;

import main.data.FileData;
import main.gui.EditorContainer;
import main.gui.components.PopUp;

import java.awt.*;

// fix moving text

// fix caret placement on same line, fix caret placement if on last line

// line numbering update, caret placement with select highlight then arrows, selection

// https://stackoverflow.com/questions/36491408/unable-to-select-text-with-number-and-underscore-on-doubleclick-in-jtextpane
// canvas, type counter timer, sockets, game

// mouse listeners with the text panes

// highlight all numbers with decimals, update search when changing case, highlight whole line, highlight + drag text

// file tree - resize, hide/ show, change design

// to-do: highlighter, find should have additional options
// modify undo/ redo to include all actions,
// settings in File Menu, terminal, REPL, side bar with files, find option only works with single lines
// ctrl + mouse, highlighting when find option pulled up

// finished: escape key on search box component, JOptionPane pop ups when exiting, fix cursor with text panes,
    // search history, fixed bugs (cursor not present after being present when
//    // find button pressed, entries go to 0 but should go to 1), undo/ redo buttons, menu separators, right click, duplicate line,
    // duplicate highlighted text, x-button, pop up x button when exiting, some actions not working, change tab title when not saved,
// inset adjustment causing problems, make designated order for methods, find should locate to highlighted text, replace different cases,
// using actual click commands - update un/saved, tabs messed up, highlight current line, line numbering off when in long text file,
// highlighter spill over - maybe use graphics
// stop highlighting all instances after certain amount, replace,
//    change search box to work such that typing something in the text area will
//    make results appear, highlighter: number glitch
// find - highlight selected instance of phrase, selected instance of phrase should be closest to caret position - when
// typing or hitting prev/ next
// outside punctuation, closing parenthesis
    // Selecting correct text when double clicking - fix highlighting whole line, highlight appropriate with CTRL

/**
 * The hub for the text editor, combining all different files into a central point.
 * Essentially the Driver class.
 *
 * ORGANIZATION: Each .java file is ordered in the following way from top to bottom:
 *          * Access Modifier (AM)
 *      1) Outer class
 *      2) Public static variables
 *      3) Protected static variables
 *      4) Private static variables
 *      5) Instance variables (same AM order)
 *      6) Constructors (same AM order)
 *      7) Enumerations
 *      8) Static methods (same AM order)
 *      9) Instance methods (same AM order)
 *      10) Nested static classes (same AM order)
 *      11) Nested instance classes (same AM order)
 *
 *      *** Key Notes:
 *          - All types (same precedence level) are in alphabetical order according to the names of the
 *          individual variable, method, etc., except for getter/ setter pairs and the main method
 *          - Classes and other types that have been borrowed from other creators may not follow these rules.
 *          - Some variable declarations/ initializations happen on the same line, separated by a comma.
 *          In these such cases, only the name of the first variable will be taken into account when ordering
 *          in alphabetic order.  This is specifically done with dimensions (width and height), in which
 *          it is common to list width, followed by height.
 *          - Overridden (@Override) and other tags are not taken into account for alphabetic order.
 *          - Access modifiers, from top to bottom, are: public, protected, private.  All public variables
 *          are listed in alphabetical order, followed by protected in ABC order, then finally private.
 *          E.g. ~ public int x; followed by protected double d;
 *          - Getters/ setters disobey alphabetical order on the same precedence level since they are
 *          treated as a pair to represent outside access from their containing class.  They are listed
 *          before the rest of the methods in their designated precedence order.  As may be expected, they
 *          will follow their own alphabetic order based on the variable name they represent, with the getter
 *          coming first, followed by the setter.
 *          - Method parameters and local variables (in general, declarations/ initializations) have no
 *          specified order in a method
 *          - Final/ constant variables, methods, classes, etc. do not have a defined order and are placed
 *          in the file based on their other characteristics, such as access modifier or class vs. instance
 *          - Variable types (non/primitive) adn method return types are not taken into account for the order
 *          - Main method(s) is/ are placed at the end of the file, ignoring the overall order
 *
 * @author Justin Kocur
 */
public class Editor {
    /** The name of this text editor */
    public static final String EDITOR_NAME = "The Justin Editor 5.0";

    // The maximum number of tabs this text editor will allow open at one time
    private static final int MAX_TABS = 10;

    // The Dimension instance of the user's screen
    private static final Dimension SCREEN_SIZE = Toolkit.getDefaultToolkit().getScreenSize();

    // The width and height, scaled to the dimensions of the user's screen
    private static final int WIDTH = (int) (SCREEN_SIZE.getWidth() * 0.6),
            HEIGHT = (int) (SCREEN_SIZE.getHeight() * 0.7);

    /**
     * Constructs the editor.
     * Initializes a new FileData type for storing information on each
     * tab in the text editor, and adds it as an argument to the GUI
     * class EditorContainer, responsible for displaying the graphics
     *
     * @param MAX_TABS   the maximum number of tabs allowed open in the editor at once
     * @param WIDTH      initial width of the application
     * @param HEIGHT     initial height of the application
     */
    public Editor(final int MAX_TABS, final int WIDTH, final int HEIGHT) {
        FileData data = new FileData(MAX_TABS);
        new EditorContainer(MAX_TABS, WIDTH, HEIGHT, data);
    }

    /**
     * Runs the program with any given arguments
     *
     * @param args  command line arguments given by the user
     *              *** EITHER NONE OR ALL ARGUMENTS MUST BE ENTERED
     *              *** TO SET INDIVIDUAL ARGS TO DEFAULT, USE -1 (e.g. args are 100, -1, 5,
     *                  in which the second argument, -1, will set its corresponding attribute to a
     *                  default, pre-assigned value)
     *                  The arguments, in the order they must be listed, are:
     *                      1) MAX_TABS - max num of tabs allowed open at once
     *                      2) WIDTH - the initial width for the GUI app
     *                      3) HEIGHT - the initial height for the GUI app
     */
    public static void main(String[] args) {
        // User abstained from arguments; default provided
        if(args == null || args.length == 0) {
            new Editor(MAX_TABS, WIDTH, HEIGHT);
        }

        // Arguments entered in; -1 should be entered for default functionality
        else {
            // Incorrect number of arguments entered
            if(args.length != 3) {
                PopUp.displayErrorMessage(null, "Error: Incorrect number of arguments entered!");
                return;
            }
            new Editor(Integer.parseInt(args[0]), Integer.parseInt(args[1]), Integer.parseInt(args[2]));
        }
    }
}
