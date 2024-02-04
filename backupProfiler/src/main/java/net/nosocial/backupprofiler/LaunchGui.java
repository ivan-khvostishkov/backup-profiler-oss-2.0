package net.nosocial.backupprofiler;

import org.jdesktop.swingx.JXTreeTable;
import org.jdesktop.swingx.treetable.FileSystemModel;
import org.jdesktop.swingx.treetable.TreeTableModel;

import javax.swing.*;
import java.awt.*;

/**
 * @author ikh
 * @since 6/22/14
 */
public class LaunchGui {
    public static void main(String[] args) {
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                createAndShowGUI();
            }
        });
    }

    private static void createAndShowGUI() {
        profilerFrame();
        newProjectFrame();

    }

    private static void newProjectFrame() {
        JFrame frame = new JFrame("New Configuration - Backup Profiler");
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        JPanel includePanel = new JPanel();
        includePanel.setBorder(BorderFactory.createTitledBorder("Include directories"));
        JPanel excludePanel = new JPanel();
        excludePanel.setBorder(BorderFactory.createTitledBorder("Exclude directories"));

        JSplitPane includeExclude = new JSplitPane(JSplitPane.VERTICAL_SPLIT, includePanel, excludePanel);

        JPanel fileTreePanel = new JPanel();
        JSplitPane treeWithLists = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, fileTreePanel, includeExclude);

        JPanel configurationPanel = new JPanel();
        configurationPanel.setBorder(BorderFactory.createEtchedBorder());
        configurationPanel.setLayout(new BorderLayout());
        configurationPanel.add(treeWithLists);

        frame.getContentPane().add(new JLabel("Configuration name:"));
        frame.getContentPane().add(new JLabel("Configuration directory:"));
        frame.getContentPane().add(configurationPanel);

        frame.pack();
        frame.setVisible(true);
    }

    private static void profilerFrame() {
        JFrame frame = new JFrame("spacecraft - [/home/root/backup/spacecraft] - Backup Profiler");
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        TreeTableModel treeTableModel = new FileSystemModel(); // any TreeTableModel
        JXTreeTable treeTable = new JXTreeTable(new ProfilerTreeTableModel());
        JScrollPane     scrollPane = new JScrollPane(treeTable);

        frame.getContentPane().add(scrollPane);

        frame.pack();
        frame.setVisible(true);
    }
}
