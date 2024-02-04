import net.nosocial.backupprofiler.*;
import org.junit.Test;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import static junit.framework.TestCase.assertNull;
import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

/**
 * @author ikh
 * @since 2/28/16
 */
public class ProfilerTest {
    @Test
    public void readsHierarchyFromLog() throws IOException {
        InputStream input = new FileInputStream("src/test/test-resources/profile-test.log");
        TimingProfile profile = new ProfileReader().read(input);
        assertEquals(4686382001L, profile.getTotalSize());
        assertEquals(7, profile.getOverallTime());
        assertEquals(1, profile.getTotalTime("/root/"));
        assertEquals(1, profile.getTotalTime("/bin/"));
        assertEquals(1, profile.getTotalTime("/bin/ls"));
        assertEquals(3, profile.getTotalTime("/home/"));
        assertEquals(3, profile.getTotalTime("/home/ikh/"));
        assertEquals(1, profile.getTotalTime("/home/ikh/Pictures"));
        assertEquals(1, profile.getTotalTime("/sbin/"));
        assertEquals(7, profile.getTotalTime("/"));
    }

    @Test
    public void incompleteLog() throws IOException {
        InputStream input = new FileInputStream("src/test/test-resources/incomplete-test.log");
        TimingProfile profile = new ProfileReader(false).read(input);
        assertEquals(4, profile.getOverallTime());
        assertEquals(0, profile.getTotalSize());
        assertEquals(1, profile.getTotalTime("/root/"));
        assertEquals(1, profile.getTotalTime("/bin/"));
        assertEquals(1, profile.getTotalTime("/bin/ls"));
    }

    @Test
    public void walkTroughProfile() throws IOException {
        InputStream input = new FileInputStream("src/test/test-resources/profile-test.log");
        assert input != null;
        TimingProfile profile = new ProfileReader().read(input);

        ProfileObserver observer = new ProfileObserver(profile);

        PathTime pathTime;

        pathTime = observer.nextPathTime();
        assertEquals("/", pathTime.getPath());
        assertEquals(7L, pathTime.getTime());

        pathTime = observer.nextPathTime();
        assertEquals("/home/", pathTime.getPath());
        assertEquals(3L, pathTime.getTime());

        pathTime = observer.nextPathTime();
        assertEquals("/home/ikh/", pathTime.getPath());
        assertEquals(3L, pathTime.getTime());

        pathTime = observer.nextPathTime();
        assertEquals("/bin/", pathTime.getPath());
        assertEquals(1L, pathTime.getTime());

        pathTime = observer.nextPathTime();
        assertEquals("/bin/ls", pathTime.getPath());
        assertEquals(1L, pathTime.getTime());

        pathTime = observer.nextPathTime();
        assertEquals("/home/ikh/Music", pathTime.getPath());
        assertEquals(1L, pathTime.getTime());

        pathTime = observer.nextPathTime();
        assertEquals("/home/ikh/Pictures", pathTime.getPath());
        assertEquals(1L, pathTime.getTime());

        pathTime = observer.nextPathTime();
        assertEquals("/home/ikh/Videos", pathTime.getPath());
        assertEquals(1L, pathTime.getTime());

        pathTime = observer.nextPathTime();
        assertEquals("/opt/", pathTime.getPath());
        assertEquals(1L, pathTime.getTime());

        pathTime = observer.nextPathTime();
        assertEquals("/root/", pathTime.getPath());
        assertEquals(1L, pathTime.getTime());

        pathTime = observer.nextPathTime();
        assertEquals("/sbin/", pathTime.getPath());
        assertEquals(1L, pathTime.getTime());

        pathTime = observer.nextPathTime();
        assertEquals("/sbin/mkswap", pathTime.getPath());
        assertEquals(1L, pathTime.getTime());

        pathTime = observer.nextPathTime();
        assertNull(pathTime);
    }

    @Test
    public void multiplePaths() throws IOException {
        InputStream input = new FileInputStream("src/test/test-resources/multiple-paths-test.log");
        TimingProfile profile = new ProfileReader(false).multiplePaths().read(input);
        assertEquals(0, profile.getTotalSize());
        assertEquals(2, profile.getOverallTime());
        assertEquals(1, profile.getTotalTime("/tmp"));
        assertEquals(1, profile.getTotalTime("/var/"));
        assertFalse(profile.hasTotalTime("/home/"));
    }

    @Test
    public void multiplePathsComplete() throws IOException {
        InputStream input = new FileInputStream("src/test/test-resources/multiple-paths-complete-test.log");
        TimingProfile profile = new ProfileReader(true).multiplePaths().read(input);
        assertEquals(490878556160L, profile.getTotalSize());
        assertEquals(4, profile.getOverallTime());
        assertEquals(1, profile.getTotalTime("/tmp"));
        assertEquals(1, profile.getTotalTime("/var/"));
        assertEquals(2, profile.getTotalTime("/home/"));
    }

    @Test
    public void windowsLog() throws IOException {
        InputStream input = new FileInputStream("src/test/test-resources/profile-windows.log");
        TimingProfile profile = new ProfileReader(false).read(input);
        assertEquals(19369, profile.getOverallTime());
        assertEquals(0, profile.getTotalSize());
        assertEquals(6, profile.getTotalTime("C:\\Users\\ikh\\.bash_history"));
    }


    @Test
    public void detectsParentChildForDirsAndFiles() throws IOException {
        assertTrue(FileSystemTraverse.isInside("/", "/abc"));
        assertFalse(FileSystemTraverse.isInside("/abc", "/abcd"));
        assertTrue(FileSystemTraverse.isInside("/var/log/", "/var/log/bind/"));
        assertTrue(FileSystemTraverse.isInside("/var/log/", "/var/log/bind/named.log"));
        assertFalse(FileSystemTraverse.isInside("/var/log/", "/var/log"));
        assertFalse(FileSystemTraverse.isInside("/var/log/", "/var/run/"));

        assertFalse(FileSystemTraverse.isInside("/abc/log", "/abc/log/cde"));

        assertTrue(FileSystemTraverse.isInside("C:\\", "C:\\Users\\ikh\\.bash_history"));

        assertTrue(FileSystemTraverse.isInside("/var/log/", "/var/log/"));
        assertTrue(FileSystemTraverse.isInside("/var/log/messages", "/var/log/messages"));

    }

    @Test
    public void substringPaths() throws IOException {
        InputStream input = new FileInputStream("src/test/test-resources/substring-paths.log");
        TimingProfile profile = new ProfileReader(true).multiplePaths().read(input);
        assertEquals(490878556160L, profile.getTotalSize());
        assertEquals(4, profile.getOverallTime());
        assertEquals(1, profile.getTotalTime("/ab"));
        assertEquals(3, profile.getTotalTime("/abc/"));
        assertEquals(3, profile.getTotalTime("/abc/tmp"));
    }


    @Test
    public void calculateTimeCorrectlyWhenErrorsFound() throws IOException {
        InputStream input = new FileInputStream("src/test/test-resources/apple-computer.log");
        TimingProfile profile = new ProfileReader(false).read(input);
        assertEquals(153, profile.getTotalTime("/home/ikh/QEMU/mnt/Users/ikh/AppData/Roaming/Apple Computer/iTunes/"));
        assertEquals(882, profile.getTotalTime("/home/ikh/QEMU/mnt/Users/ikh/AppData/Roaming/"));
    }

}
