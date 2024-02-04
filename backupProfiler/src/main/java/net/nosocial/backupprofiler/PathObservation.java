package net.nosocial.backupprofiler;

/**
 * @author ikh
 * @since 2/28/16
 */
public class PathObservation {
    private final long time;
    private final String path;

    public PathObservation(long time, String path) {

        this.time = time;
        this.path = path;
    }

    public long getTime() {
        return time;
    }

    public String getPath() {
        return path;
    }
}
