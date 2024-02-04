package net.nosocial.backupprofiler;

import java.io.File;

/**
 * @author ikh
 * @since 10/18/19
 */
public interface TraverseEvent {
    void done();

    void skip(File file);

    void traverse(File file, String formattedFileName);

    void errorListFiles(File file);
}
