package main.data;

import java.util.Arrays;

/**
 * Contains information for each individual file that is currently open
 * in the text editor
 * Although no getters/ setters are provided to change the references for
 * the arrays labeled 'names,' 'ogText,' and 'paths,' there are getters and
 * setters for modifying and retrieving elements in these arrays given an
 * index.  Therefore, the methods labeled 'get/setTabName,' 'get/setTabOgText,'
 * and 'get/setTabPath' will be placed above all other methods that aren't
 * considered getters and setters.
 *
 * @author Justin Kocur
 */
public class FileData {
    // The last known path from which the user opened a file
    private String lastOpenPath;

    // The last known path from which the user saved a file
    private String lastSavePath;

    // The maximum number of tabs this text editor will allow open at one time
    private final int MAX_TABS;

    /* The names of each file that is currently opened;
           "Untitled" if tab not open at index */
    private final String[] names;

    /* The last known text in a file before it was last saved;
       "" if tab not open at index */
    private final String[] ogText;

    /* True if the file at an arbitrary index was opened from a file;
       false if first created in this text editor */
    private final boolean[] openedFromDir;

    /* The paths of each file that is currently opened (saved files only);
       null if tab not open at index */
    private final String[] paths;

    // The position/ index at which to add a new tab
    private int tabPointer;

    /**
     *  Creates a designated type that holds data for text files in this application
     *
     * @param MAX_TABS  the maximum number of tabs allowed open in the text editor at once
     */
    public FileData(final int MAX_TABS) {
        this.MAX_TABS = MAX_TABS;
        tabPointer = -1;

        names           = new String[MAX_TABS];
        paths           = new String[MAX_TABS];
        ogText          = new String[MAX_TABS];
        openedFromDir   = new boolean[MAX_TABS];

        Arrays.fill(names, "Untitled");
        Arrays.fill(ogText, "");

        lastOpenPath = "c:";
        lastSavePath = "c:";
    }

    /**
     *  Returns the last open path from the user
     *
     * @return  the last open path from the user
     */
    public String getLastOpenPath() {
        return lastOpenPath;
    }

    /**
     *  Sets the last open path from the user
     *
     * @param lastOpenPath  the last open path from the user
     */
    public void setLastOpenPath(String lastOpenPath) {
        this.lastOpenPath = lastOpenPath;
    }

    /**
     *  Returns the last save path from the user
     *
     * @return  the last save path from the user
     */
    public String getLastSavePath() {
        return lastSavePath;
    }

    /**
     *  Sets the last save path from the user
     *
     * @param lastSavePath  the last save path from the user
     */
    public void setLastSavePath(String lastSavePath) {
        this.lastSavePath = lastSavePath;
    }

    /**
     *  Returns the name of the file at the designated index
     *
     * @param index     the index at which to fetch the name of the file
     * @return          the String name of the file; null if out of bounds
     */
    public String getTabName(int index) {
        // Index out of bounds
        if(index < 0 || index >= MAX_TABS)
            return null;

        return names[index];
    }

    /**
     *  Sets the name of a file at the designated index
     *
     * @param index     the index at which to change the name of a tab
     * @param name      the new name for the tab
     * @return          true if successfully changed, false if index out of
     *                  bounds or tab not available
     */
    public boolean setTabName(int index, String name) {
        // Index out of bounds
        if(index < 0 || index >= numTabsOpen())
            return false;

        names[index] = name;
        return true;
    }

    /**
     *  Returns the original text of the file at the designated index
     *
     * @param index     the index at which to fetch the original text of the file
     * @return          the String representation of the original text of the file; null if out of bounds
     */
    public String getTabOgText(int index) {
        // Index out of bounds
        if(index < 0 || index >= MAX_TABS)
            return null;

        return ogText[index];
    }

    /**
     *  Sets the text of a file at the designated index
     *
     * @param index     the index at which to change the name of a tab
     * @param text      the new text for the tab
     * @return          true if successfully changed, false if index out of
     *                  bounds or tab not available
     */
    public boolean setTabOgText(int index, String text) {
        // Index out of bounds
        if(index < 0 || index >= numTabsOpen())
            return false;

        ogText[index] = text;
        return true;
    }

    /**
     *  Returns the 'openedFromDir' state of the file at the designated index
     *
     * @param index     the index at which to fetch the opened state of the file
     * @return          true if opened from file, false if not
     */
    public boolean isOpenedFromDir(int index) {
        // Index out of bounds
        if(index < 0 || index >= MAX_TABS)
            return false;

        return openedFromDir[index];
    }

    /**
     *  Sets the 'openedFromDir' state at the designated index
     *
     * @param index     the index to access
     * @param flag      the new state for the tab
     * @return          true if successfully changed, false if index out of
     *                  bounds or tab not available
     */
    public boolean setOpenedFromDir(int index, boolean flag) {
        // Index out of bounds
        if(index < 0 || index >= numTabsOpen())
            return false;

        openedFromDir[index] = flag;
        return true;
    }

    /**
     *  Returns the path of the file at the designated index
     *
     * @param index     the index at which to fetch the path of the file
     * @return          the String path of the file; null if out of bounds
     */
    public String getTabPath(int index) {
        // Index out of bounds
        if(index < 0 || index >= MAX_TABS)
            return null;

        return paths[index];
    }

    /**
     *  Sets the path of a file at the designated index
     *
     * @param index     the index at which to change the name of a tab
     * @param path      the new path for the tab
     * @return          true if successfully changed, false if index out of
     *                  bounds or tab not available
     */
    public boolean setTabPath(int index, String path) {
        // Index out of bounds
        if(index < 0 || index >= numTabsOpen())
            return false;

        paths[index] = path;
        return true;
    }

    /**
     *  Adds new tab information into the arrays, unless there is no more space
     *
     * @return          true if successfully added, false if there is no more space
     */
    public boolean addTabData() {
        // No more space
        if(numTabsOpen() >= MAX_TABS)
            return false;

        tabPointer++;
        return true;
    }

    /**
     *  Adds new tab information into the arrays, unless there is no more space
     *
     * @param name  the name given to the file
     * @param path  the path given to the file
     * @param text  the original text given to the file
     * @param flag  determines whether opened from directory
     * @return      true if successfully added, false if there is no more space
     */
    public boolean addTabData(String name, String path, String text, boolean flag) {
        // No more space
        if(numTabsOpen() >= MAX_TABS)
            return false;

        tabPointer++;
        names[tabPointer] = name;
        paths[tabPointer] = path;
        ogText[tabPointer] = text;
        openedFromDir[tabPointer] = flag;
        return true;
    }

    /**
     *  Returns the number of tabs currently open
     *
     * @return  the number of tabs currently open
     */
    public int numTabsOpen() {
        return tabPointer + 1;
    }

    /**
     *  Removes tab information at a designated position
     *
     * @param index     the index at which to remove data
     * @return          true if successfully removed, false if no such data exists
     */
    public boolean removeTabData(int index) {
        // Index out of bounds
        if(index < 0 || index >= numTabsOpen())
            return false;

        // Shift all content on right of removed to the left
        for(int i = index; i < numTabsOpen(); i++) {
            if(i + 1 >= MAX_TABS) {
                names[i] = "Untitled";
                paths[i] = null;
                ogText[i] = "";
                openedFromDir[i] = false;
            } else {
                names[i] = names[i + 1];
                paths[i] = paths[i + 1];
                ogText[i] = ogText[i + 1];
                openedFromDir[i] = openedFromDir[i + 1];
            }
        }
        tabPointer--;
        return true;
    }
}
