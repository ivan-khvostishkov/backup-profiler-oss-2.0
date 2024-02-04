package net.nosocial.backupprofiler;

/**
 * @author ikh
 * @since 2/28/16
 */
public class PathTime {
    private String path;
    private long time;
    private long acknowledgedTime;

    public PathTime(String path, long time) {
        this.path = path;
        this.time = time;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    @Override
    public String toString() {
        return String.format("%6s %4s %s", FormatData.humanReadableByteCount(time),
                getAcknowledgedPercentAsString(), path);
    }

    private String getAcknowledgedPercentAsString() {
        int percent = getAcknowledgedPercent();
        if (percent == 0) {
            return "";
        }
        return percent + "%";
    }

    private int getAcknowledgedPercent() {
        if (time == 0) {
            return 0;
        }
        return (int) (acknowledgedTime * 100 / time);
    }

    public void initAcknowledgedTime() {
        acknowledgedTime = 0;
    }

    public void addAcknowledgedTime(long time) {
        acknowledgedTime += time;
    }
}
