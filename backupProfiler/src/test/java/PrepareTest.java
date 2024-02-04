import net.nosocial.backupprofiler.FileSystemTraverse;
import net.nosocial.backupprofiler.ProfileEventLog;
import net.nosocial.backupprofiler.TraverseEvent;
import org.junit.Test;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertTrue;

/**
 * @author ikh
 * @since 10/18/19
 */
public class PrepareTest {
    @Test
    public void traverseFs() {
        final boolean[] finished = {false};
        List<String> traversed = new ArrayList<>();

        new FileSystemTraverse(new TraverseEvent() {
            @Override
            public void done() {
                finished[0] = true;
            }

            @Override
            public void skip(File file) {
                throw new UnsupportedOperationException("not yet implemented: " + file.getName());
            }

            @Override
            public void traverse(File file, String formattedFileName) {
                traversed.add(formattedFileName);
            }

            @Override
            public void errorListFiles(File file) {
                throw new UnsupportedOperationException("not yet implemented");
            }
        },
                new String[]{"test-backup/profile-start.txt"},
                new String[]{}).start();

        assertTrue(finished[0]);
        Collections.sort(traversed);
        assertEquals("/home/ikh/backupProfiler/", traversed.get(0));
        assertEquals("/home/ikh/backupProfiler/.git/", traversed.get(1));
        assertEquals("/home/ikh/backupProfiler/.git/COMMIT_EDITMSG", traversed.get(2));
    }
}
