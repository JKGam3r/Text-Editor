package main.gui.components.fileinfo;

import main.actions.EditorActions;
import main.gui.EditorContainer;

import javax.swing.*;
import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.ArrayList;

public class FileTree {
    // Thickness of the border surrounding the FileTree
    private final int borderThickness = 2;

    private final EditorContainer editorContainer;

    // The minimum width of the JTree
    private final int minWidth = 50;

    private final JTree tree;

    // Button used to show/ hide the JTree in the project
    private final JButton treeVisibleButton;

    private final JScrollPane scrollPane;

    public FileTree(EditorActions editorActions, File rootFile, EditorContainer editorContainer) {
        FileTreeModel model = new FileTreeModel(rootFile);

        this.editorContainer = editorContainer;

        tree = new JTree();
        tree.setModel(model);

        treeVisibleButton = new JButton();
        constructTreeVisibleButton();

        JPanel panel = new JPanel();
        panel.add(treeVisibleButton, BorderLayout.NORTH);
        panel.add(tree, BorderLayout.SOUTH);

        scrollPane = new JScrollPane(tree);
        scrollPane.getHorizontalScrollBar().setUnitIncrement(16);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);

        scrollPane.setBorder(BorderFactory.createMatteBorder(borderThickness, borderThickness,
                1, borderThickness, Color.lightGray));

        tree.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                int selRow = tree.getRowForLocation(e.getX(), e.getY());
                TreePath selPath = tree.getPathForLocation(e.getX(), e.getY());
                if(selRow != -1) {
                    if(e.getClickCount() == 2 && selPath != null) {

                        String path = selPath.getLastPathComponent().toString();
                        if(new File(path).isFile())
                            editorActions.openFunctionality(path);
                    }
                }
            }
        });

        MouseAdapter mouseAdapter = new MouseAdapter() {
            boolean drag            = false;
            final int margin        = 10;
            int prevX               = -1;

            @Override
            public void mouseDragged(MouseEvent e) {
                int x = e.getX();

                if(onEdge(x, margin))
                    drag = true;

                if(prevX != -1 && drag) {
                    int w = (int) (tree.getPreferredSize().getWidth() + (x - prevX));

                    if(w >= minWidth) {
                        resizeWidths(w);
                        prevX = x;
                    } else {
                        resizeWidths(minWidth);
                        prevX = minWidth;
                    }
                } else
                    prevX = x;
            }

            @Override
            public void mouseMoved(MouseEvent e) {
                int x = e.getX();

                if(onEdge(x, margin))
                    tree.setCursor(new Cursor(Cursor.W_RESIZE_CURSOR));
                else
                    tree.setCursor(Cursor.getDefaultCursor());
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                drag = false;
            }

            @Override
            public void mouseExited(MouseEvent e) {
                if(drag)
                    tree.setCursor(new Cursor(Cursor.W_RESIZE_CURSOR));
            }

            private boolean onEdge(int x, int margin) {
                return x >= scrollPane.getX() + scrollPane.getWidth()
                        - scrollPane.getVerticalScrollBar().getWidth() - margin;
            }
        };

        tree.addMouseListener(mouseAdapter);
        tree.addMouseMotionListener(mouseAdapter);
    }

    /**
     *  Returns the background color for the file tree
     *
     * @return     the color to set as the background
     */
    public Color getBackgroundColor() {
        return tree.getBackground();
    }

    /**
     *  Sets the background color for the file tree
     *
     * @param color     the color to set as the background
     */
    public void setBackgroundColor(Color color) {
        tree.setBackground(color);
    }

    public JScrollPane getScrollableTree() {
        return scrollPane;
    }

    public void setBorderColor(Color color) {
        scrollPane.setBorder(BorderFactory.createMatteBorder(borderThickness, borderThickness,
                1, borderThickness, color));
    }

    public void setNewModel(File rootFile) {
        tree.setModel(new FileTreeModel(rootFile));
        scrollPane.setViewportView(tree);
        editorContainer.getMainFrame().setVisible(true);
    }

    private void constructTreeVisibleButton() {
        treeVisibleButton.setSize(50, 25);
        treeVisibleButton.setPreferredSize(new Dimension(50, 25));
        treeVisibleButton.setBounds(0, 0, 50, 25);

        treeVisibleButton.addActionListener(new ActionListener() {
            boolean treeVisible = true;

            @Override
            public void actionPerformed(ActionEvent e) {
                treeVisible = !treeVisible;

                tree.setVisible(treeVisible);
            }
        });
    }

    private void resizeWidths(int w) {
        tree.setSize(w, tree.getHeight());
        tree.setPreferredSize(new Dimension(w, (int) tree.getPreferredSize().getHeight()));

        /*editorContainer.getTabbedPane().setSize(editorContainer.getMainFrame().getWidth() - w - 15,
                editorContainer.getTabbedPane().getHeight());
        editorContainer.getTabbedPane().setPreferredSize(new Dimension(editorContainer.getMainFrame().getWidth() - w - 15,
                (int) editorContainer.getTabbedPane().getPreferredSize().getHeight()));*/

        editorContainer.getTabbedPane().setBounds(0, 0, editorContainer.getMainFrame().getWidth() - w - 15,
                editorContainer.getTabbedPane().getHeight());

        scrollPane.setViewportView(tree);
        editorContainer.getMainFrame().setVisible(true);
    }

    private static class FileTreeModel implements TreeModel {
        private final ArrayList<TreeModelListener>  mListeners  = new ArrayList<>();

        // We specify the root directory when we create the model.
        private final File root;

        public FileTreeModel(File root) {
            this.root = root;
        }

        // The model knows how to return the root object of the tree
        public Object getRoot() {
            return root;
        }

        // Tell JTree whether an object in the tree is a leaf
        public boolean isLeaf(Object node) {
            return ((File) node).isFile();
        }

        // Tell JTree how many children a node has
        public int getChildCount(Object parent) {
            String[] children = ((File) parent).list();

            if(children == null)
                return 0;
            return children.length;
        }

        // Fetch any numbered child of a node for the JTree
        // Our model returns File objects for all nodes in the tree.  The
        // JTree displays these by calling the File.toString() method.
        public Object getChild(Object parent, int index) {
            String[] children = ((File) parent).list();

            if((children == null) || (index >= children.length))
                return null;
            return new File((File) parent, children[index]);
        }

        // Figure out a child's position in its parent node
        public int getIndexOfChild(Object parent, Object child) {
            String[] children = ((File) parent).list();

            if(children == null)
                return -1;

            String childName = ((File) child).getName();

            for(int i = 0; i < children.length; i++) {
                if(childName.equals(children[i]))
                    return i;
            }

            return -1;
        }

        public void valueForPathChanged(TreePath path, Object newValue) {
            reload();
        }

        public void addTreeModelListener(TreeModelListener listener) {
            mListeners.add(listener);
        }

        public void removeTreeModelListener(TreeModelListener listener) {
            mListeners.remove(listener);
        }

        // https://stackoverflow.com/questions/36804035/java-jtree-from-directory-shows-full-path-instead-of-just-the-name-of-the-file
        public void reload() {
            // Need to duplicate the code because the root can formally be
            // no an instance of the TreeNode.
            final int n = getChildCount(getRoot());
            final int[] childIdx = new int[n];
            final Object[] children = new Object[n];

            for (int i = 0; i < n; i++) {
                childIdx[i] = i;
                children[i] = getChild(getRoot(), i);
            }

            fireTreeStructureChanged(this, new Object[] { getRoot() }, childIdx, children);
        }

        protected void fireTreeStructureChanged(final Object source, final Object[] path, final int[] childIndices, final Object[] children) {
            final TreeModelEvent event = new TreeModelEvent(source, path, childIndices, children);
            for (final TreeModelListener l : mListeners) {
                l.treeStructureChanged(event);
            }
        }
    }
}
