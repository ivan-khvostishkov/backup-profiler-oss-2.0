import net.nosocial.backupprofiler.PathTime;
import net.nosocial.backupprofiler.TimingProfile;
import net.nosocial.backupprofiler.ProfileObserver;
import net.nosocial.backupprofiler.ProfileReader;
import org.junit.Ignore;
import org.junit.Test;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * @author ikh
 * @since 2/28/16
 */
public class RealProfilerTest {
    public void handlesRealLog() throws IOException {
        InputStream input = new FileInputStream("/home/ikh/backupProfiler/profile-ignore.log");
        TimingProfile profile = new ProfileReader(true).multiplePaths().read(input);
        // TODO: acknowledge paths to get total vs remaining (acknowledged) time estimate and compare with previous time
        // TODO: watch for removed dirs and amend total time accordingly
        // TODO: skip dirs (with adding them to ignore list and bypassing total time)?

        System.out.println("Total time: " + profile.getOverallTime());
        System.out.println("Total size: " + profile.getTotalSize());

        ProfileObserver observer = new ProfileObserver(profile);
        for (int i = 0; i < 1000; i++) {
            PathTime pathTime = observer.nextPathTime();
            System.out.println(pathTime);
        }
    }
}
