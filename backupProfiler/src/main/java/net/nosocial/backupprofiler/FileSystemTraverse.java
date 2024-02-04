package net.nosocial.backupprofiler;

import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

/**
 * @author ikh
 * @since 6/22/14
 */
public class FileSystemTraverse {
    private final TraverseEvent eventLog;
    private final String[] includeList;
    private final String[] excludeList;

    private List<String> includeFiles = new ArrayList<>();
    private List<String> excludeFiles = new ArrayList<>();


    public FileSystemTraverse(TraverseEvent eventLog, String[] includeList, String[] excludeList) {

        this.eventLog = eventLog;
        this.includeList = includeList;
        this.excludeList = excludeList;
    }

    public void start() {
        try {
            init();
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }


        for (String root : includeFiles) {
            traverse(new File(root));
        }

        eventLog.done();
    }

    private void init() throws IOException {
        for (String s : includeList) {
            List<String> lines = IOUtils.readLines(new FileReader(s));
            includeFiles.addAll(lines);
        }

        for (String s : excludeList) {
            List<String> lines = IOUtils.readLines(new FileReader(s));
            excludeFiles.addAll(lines);
        }
    }

    private void traverse(File file) {

        String formattedFileName = FormatData.formatFileName(file);

        if (isExclude(file.getAbsolutePath())) {
            eventLog.skip(file);
        } else {
            eventLog.traverse(file, formattedFileName);

            if (file.isDirectory() && !Files.isSymbolicLink(file.toPath())) {
                File[] files = file.listFiles();
                if (files != null) {
                    for (File child : files) {
                        traverse(child);
                    }
                } else {
                    eventLog.errorListFiles(file);
                }
            }

        }

    }

    private boolean isExclude(String root) {
        for (String excludeFile : excludeFiles) {
            if (isInside(excludeFile, root)) {
                return true;
            }
        }
        return false;
    }

    public static boolean isInside(String parent, String child) {
        if (parent.endsWith("/") || parent.endsWith("\\")) {
            return child.startsWith(parent);
        }
        return parent.equals(child);
    }


}
