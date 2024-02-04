package net.nosocial.backupprofiler;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * @author ikh
 * @since 6/22/14
 */
public class ProfileEventLog implements TraverseEvent {
    Logger logger = LoggerFactory.getLogger(ProfileEventLog.class);
    private final BufferedWriter sizeProfileWriter;
    private final FileWriter countProfileWriter;

    long totalSize = 0;
    long totalCount = 0;

    public ProfileEventLog(String outputDir) {
        try {
            sizeProfileWriter = new BufferedWriter(new FileWriter(new File(outputDir, "size/profile.log")));
            countProfileWriter = new FileWriter(new File(outputDir, "count/profile.log"));
        } catch (IOException e) {
            throw new IllegalArgumentException(e);
        }
    }

    public void traverse(File file, String formattedFileName) {
        System.out.println(String.format("Traverse into file %s", file.toString()));
        try {
            sizeProfileWriter.write(String.format("%d %s", totalSize, formattedFileName));
            countProfileWriter.write(String.format("%d %s", totalCount, formattedFileName));

            sizeProfileWriter.write(System.lineSeparator());
            countProfileWriter.write(System.lineSeparator());

        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
        totalSize += file.length();
        totalCount++;
    }

    public void skip(File file) {
        System.out.println(String.format("Skipping file %s", file.toString()));
    }

    public void done() {
        System.out.println("Done.");
        try {
            sizeProfileWriter.write(String.format("%d %d", totalSize, totalSize));
            countProfileWriter.write(String.format("%d %d", totalCount, totalCount));

            sizeProfileWriter.write(System.lineSeparator());
            countProfileWriter.write(System.lineSeparator());

            sizeProfileWriter.close();
            countProfileWriter.close();
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    public void errorListFiles(File file) {
        System.out.println(String.format("Error listing files in directory %s", file.toString()));
    }
}
