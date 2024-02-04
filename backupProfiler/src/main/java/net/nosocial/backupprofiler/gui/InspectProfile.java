package net.nosocial.backupprofiler.gui;

import net.nosocial.backupprofiler.*;
import org.jdesktop.swingx.renderer.DefaultListRenderer;

import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.*;
import java.io.*;
import java.util.*;

/**
 * @author ikh
 * @since 6/5/16
 */
public class InspectProfile implements WindowListener, KeyListener {

    public static final String PROFILE_LOG = "profile.log";
    public static final String PROFILE_ACKNOWLEDGE = "profile-ack.txt";

    public static volatile boolean profileComplete;

    public static final boolean IS_UNIX = File.separatorChar == '/';

    private JPanel panel1;
    private JList list1;
    private JLabel label1;
    private TimingProfile profile;

    private PathTime[] data;
    private DefaultListModel<PathTime> dataModel;

    private Stack<String> prefixHistory;

    private Font unreadFont = new Font(Font.MONOSPACED, Font.BOLD, 12);
    private Font readFont = new Font(Font.MONOSPACED, Font.PLAIN, 12);
    private Set<String> acknowledgedPaths;

    public InspectProfile() {
        System.out.println(String.format("%s%s", "Inspecting profile", profileComplete ? "" : " (incomplete)"));
        
        list1.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                super.keyTyped(e);
            }

            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyChar() == 't') {
                    int indices[] = ((JList) e.getComponent()).getSelectedIndices();
                    for (int index : indices) {
                        acknowledgePath(index);
                    }
                    list1.repaint();
                    updateLabel();
                } else if (e.getKeyChar() == '\n') {
                    int index = ((JList) e.getComponent()).getSelectedIndex();

                    if (index >= 0) {
                        prefixHistory.push(dataModel.getElementAt(index).getPath());
                        initDataModel();
                        list1.setModel(dataModel);
                        ((JList) e.getComponent()).setSelectedIndex(0);
                    }
                    list1.repaint();
                    updateLabel();
                    updateTitle();
                } else if (e.getKeyChar() == '\b') {
                    int index = ((JList) e.getComponent()).getSelectedIndex();

                    if (index >= 0 && prefixHistory.size() > 1) {
                        String selectedPrefix = prefixHistory.pop();
                        initDataModel();
                        list1.setModel(dataModel);
                        int newIndex = findDataModelIndex(selectedPrefix);
                        ((JList) e.getComponent()).setSelectedIndex(newIndex);
                        ((JList) e.getComponent()).ensureIndexIsVisible(newIndex);
                    }
                    list1.repaint();
                    updateLabel();
                    updateTitle();
                } else if (e.getKeyChar() == 'c') {
                    int index = ((JList) e.getComponent()).getSelectedIndex();
                    String path = dataModel.getElementAt(index).getPath();

                    StringSelection stringSelection = new StringSelection(path);
                    Clipboard clip = Toolkit.getDefaultToolkit().getSystemClipboard();
                    clip.setContents(stringSelection, null);

                    System.out.println("Copy to clipboard: " + path);
                } else {
                    super.keyPressed(e);
                }

            }

            @Override
            public void keyReleased(KeyEvent e) {
                super.keyReleased(e);
            }
        });
    }

    private int findDataModelIndex(String selectedPrefix) {
        for (int i = 0; i < dataModel.size(); i++) {
            if (dataModel.getElementAt(i).getPath().equals(selectedPrefix)) {
                return i;
            }
        }
        return -1;
    }

    private void acknowledgePath(int index) {
        PathTime selectedPath = dataModel.getElementAt(index);
        System.out.println("Toggle: " + selectedPath);

        boolean found = false;
        for (String acknowledgedPath : new HashSet<>(acknowledgedPaths)) {
            if (FileSystemTraverse.isInside(selectedPath.getPath(), acknowledgedPath)) {
                acknowledgedPaths.remove(acknowledgedPath);
                System.out.println("Remove: " + selectedPath);
                if (acknowledgedPath.equals(selectedPath.getPath())) {
                    found = true;
                }
            } else if (FileSystemTraverse.isInside(acknowledgedPath, selectedPath.getPath())) {
                System.out.println("Unable to toggle child");
                found = true;
                break;
            }
        }
        if (!found) {
            System.out.println("Add: " + selectedPath);
            acknowledgedPaths.add(selectedPath.getPath());
        }

        recalculateDataPercentage();
    }

    private void recalculateDataPercentage() {
        for (PathTime pathTime : data) {
            pathTime.initAcknowledgedTime();
        }

        for (String acknowledgedPath : acknowledgedPaths) {
            if (!profile.hasTotalTime(acknowledgedPath)) {
                continue;
            }
            for (PathTime pathTime : data) {
                if (FileSystemTraverse.isInside(pathTime.getPath(), acknowledgedPath)) {
                    pathTime.addAcknowledgedTime(profile.getTotalTime(acknowledgedPath));
                }
                if (pathTime.getPath().equals(acknowledgedPath)) {
                    break;
                }
            }
        }
    }

    private static JFrame frame;

    public static void main(String[] args) {
        profileComplete = args.length == 0 || !args[0].equals("--incomplete");

        frame = new JFrame("InspectProfile");
        InspectProfile inspectProfile = new InspectProfile();
        inspectProfile.updateLabel();
        inspectProfile.updateTitle();

        frame.addWindowListener(inspectProfile);

        frame.setContentPane(inspectProfile.panel1);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
    }

    private void updateLabel() {
        label1.setText(formatAcknowledgedPercentage());
    }

    private void updateTitle() {
        frame.setTitle(prefixHistory.peek());
    }

    public PathTime[] loadData() throws IOException {
        InputStream input = new FileInputStream(PROFILE_LOG);
        this.profile = new ProfileReader(profileComplete).multiplePaths().read(input);

        // TODO: compare with previous time
        // TODO: watch for removed dirs and amend total time accordingly
        // TODO: skip dirs (with adding them to ignore list and bypassing total time)?
        // TODO: jump to next unacknowledged
        // TODO: search list


        System.out.println("Total time: " + profile.getOverallTime());
        System.out.println("Total size: " + FormatData.humanReadableByteCount(profile.getTotalSize()));

        int count = profile.getPathsCount();
        PathTime[] result = new PathTime[count];

        ProfileObserver observer = new ProfileObserver(profile);
        for (int i = 0; i < count; i++) {
            result[i] = observer.nextPathTime();
        }

        return result;
    }

    private void saveAcknowledgeData() throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(PROFILE_ACKNOWLEDGE))) {
            for (String acknowledgedPath : acknowledgedPaths) {
                writer.write(acknowledgedPath);
                writer.newLine();
                if (!new File(acknowledgedPath).exists()) {
                    System.out.println("Warning, removed (?) path: " + acknowledgedPath);
                }
            }
        }
    }


    private Set<String> loadAcknowledgeData() throws IOException {
        Set<String> result = new HashSet<>();

        File ackFile = new File(PROFILE_ACKNOWLEDGE);

        if (!ackFile.isFile() || !ackFile.exists()) {
            return  result;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(ackFile))) {
            for (String acknowlededPath = reader.readLine(); acknowlededPath != null;
                 acknowlededPath = reader.readLine()) {

                result.add(acknowlededPath);

                if (findDataModelIndex(acknowlededPath) < 0) {
                    System.out.println("Warning, not exists in profile (profile incomplete?): " + acknowlededPath);
                }
            }
        }

        return result;
    }

    private void createUIComponents() throws IOException {
        data = loadData();
        prefixHistory = new Stack<>();

        // FIXME: detect windows vs. unix from data files
        if (IS_UNIX) {
            prefixHistory.push("/");
        } else {
            prefixHistory.push("C:\\");
        }

        initDataModel();
        acknowledgedPaths = loadAcknowledgeData();
        recalculateDataPercentage();
        list1 = new JList<>(dataModel);
        list1.setCellRenderer(new TogglePathRenderer());
        label1 = new JLabel();
    }

    private void initDataModel() {
        dataModel = new DefaultListModel<>();
        fillDataModel(data, dataModel, prefixHistory.peek());
    }

    private void fillDataModel(PathTime[] data, DefaultListModel<PathTime> dataModel, String prefix) {
        dataModel.clear();
        for (PathTime pathTime : data) {
            if (FileSystemTraverse.isInside(prefix, pathTime.getPath())) {
                dataModel.addElement(pathTime);
            }
        }
    }

    private String formatAcknowledgedPercentage() {
        long acknowledgedSum = getAcknowledgedSum();
        long overallTime = profile.getOverallTime();
        double percent = acknowledgedSum * 100.0 / overallTime;

        long remaining90 = (long) (overallTime * 0.9 - acknowledgedSum);
        if (remaining90 < 0)
            remaining90 = 0;
        long remaining95 = (long) (overallTime * 0.95 - acknowledgedSum);
        if (remaining95 < 0)
            remaining95 = 0;
        long remaining99 = (long) (overallTime * 0.99 - acknowledgedSum);
        if (remaining99 < 0)
            remaining99 = 0;

        return String.format("Acknowledged: %s / %s (%.0f%%)  Remaining for target 90%%: %s, 95%%: %s, 99%%: %s",
                FormatData.humanReadableByteCount(acknowledgedSum),
                FormatData.humanReadableByteCount(overallTime), percent,
                FormatData.humanReadableByteCount(remaining90),
                FormatData.humanReadableByteCount(remaining95),
                FormatData.humanReadableByteCount(remaining99));
    }

    private long getAcknowledgedSum() {
        long sum = 0;
        for (String acknowledgedPath : acknowledgedPaths) {
            if (profile.hasTotalTime(acknowledgedPath)) {
                sum += profile.getTotalTime(acknowledgedPath);
            }
        }
        return sum;
    }

    @Override
    public void windowOpened(WindowEvent e) {
    }

    @Override
    public void windowClosing(WindowEvent e) {
        try {
            saveAcknowledgeData();
        } catch (IOException e1) {
            throw new IllegalStateException(e1);
        }
        System.out.println(formatAcknowledgedPercentage());
        System.out.println("Saving done.");
    }

    @Override
    public void windowClosed(WindowEvent e) {
    }

    @Override
    public void windowIconified(WindowEvent e) {
    }

    @Override
    public void windowDeiconified(WindowEvent e) {
    }

    @Override
    public void windowActivated(WindowEvent e) {
    }

    @Override
    public void windowDeactivated(WindowEvent e) {
    }

    @Override
    public void keyTyped(KeyEvent e) {
    }

    @Override
    public void keyPressed(KeyEvent e) {
    }

    @Override
    public void keyReleased(KeyEvent e) {
    }

    private class TogglePathRenderer extends DefaultListRenderer {
        @Override
        public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
            Component result = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            String itemPath = dataModel.getElementAt(index).getPath();
            if (acknowledgedPaths.contains(itemPath)) {
                result.setFont(readFont);
                result.setForeground(Color.BLACK);
            } else if (acknowledgedPathsHasPrefix(itemPath)) {
                result.setFont(readFont);
                result.setForeground(Color.GRAY);
            } else {
                result.setFont(unreadFont);
                result.setForeground(Color.BLACK);
            }
            return result;
        }
    }

    private boolean acknowledgedPathsHasPrefix(String itemPath) {
        for (String acknowledgedPath : acknowledgedPaths) {
            if (FileSystemTraverse.isInside(acknowledgedPath, itemPath)) {
                return true;
            }
        }
        return false;
    }
}
