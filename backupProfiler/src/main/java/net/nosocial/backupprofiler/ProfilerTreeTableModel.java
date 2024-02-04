package net.nosocial.backupprofiler;

import org.jdesktop.swingx.treetable.AbstractTreeTableModel;

/**
 * @author ikh
 * @since 6/22/14
 */
public class ProfilerTreeTableModel extends AbstractTreeTableModel {
    @Override
    public int getColumnCount() {
        return 2;
    }

    @Override
    public String getColumnName(int column) {
        switch (column) {
            case 0:
                return "Name";
            case 1:
                return "Size";
        }
        return null;
    }

    @Override
    public Object getValueAt(Object node, int column) {
        return null;
    }

    @Override
    public Object getChild(Object parent, int index) {
        return null;
    }

    @Override
    public int getChildCount(Object parent) {
        return 0;
    }

    @Override
    public int getIndexOfChild(Object parent, Object child) {
        return -1;
    }
}
