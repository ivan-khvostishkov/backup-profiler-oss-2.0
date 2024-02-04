package net.nosocial.backupprofiler;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * @author ikh
 * @since 2/28/16
 */
public class ProfileReader {
    private boolean complete = true;
    private boolean multiplePaths;

    public ProfileReader(boolean complete) {
        this.complete = complete;
    }

    public ProfileReader() {
        this(true);
    }

    public TimingProfile read(InputStream input) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(input));

        TimingProfile result = new TimingProfile(multiplePaths);
        Long time = null;
        String path = null;
        for (String line = reader.readLine(); line != null; line = reader.readLine()) {
            if (path != null && !isPath(path)) {
                throw new IllegalArgumentException("Invalid format, expected path: " + path);
            }
            String arg[] = line.split("[ \t]+", 2);
            if (arg.length != 2) {
                throw new IllegalArgumentException("Invalid format, expected time and path: " + line);
            }
            time = Long.valueOf(arg[0]);
            path = arg[1];

            if (path.startsWith("/bin/tar: ")) {
                // FIXME: workaround for tar errors for estimate
                continue;
            }

            if (isPath(path)) {
                result.observe(time, path);
            }
        }

        if (complete) {
            assert path != null;
            if (isPath(path)) {
                throw new IllegalStateException("Last line is path, not a total size, profile is incomplete? Try with --incomplete .");
            }
            Long totalSize = Long.valueOf(path);
            result.totalSize(time, totalSize);
        }

        return result;
    }

    private boolean isPath(String path) {
        // TODO: other drives
        return path.startsWith("/") || path.startsWith("C:\\");
    }

    public ProfileReader multiplePaths() {
        multiplePaths = true;
        return this;
    }
}
