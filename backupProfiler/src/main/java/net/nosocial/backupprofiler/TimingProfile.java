package net.nosocial.backupprofiler;

import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

/**
 * @author ikh
 * @since 2/28/16
 */
public class TimingProfile {
    private final boolean multiplePaths;
    private long totalSize;

    private Map<String, Long> timeMap = new HashMap<>();
    private Stack<PathObservation> stack = new Stack<>();

    private long lastTime = 0;
    private long firstTime = -1;

    public TimingProfile(boolean multiplePaths) {
        this.multiplePaths = multiplePaths;
    }

    public long getTotalTime(String path) {
        Long result = timeMap.get(path);
        if (result == null) {
            throw new IllegalArgumentException("Path not found: " + path);
        }
        return result;
    }


    public long getTotalSize() {
        return totalSize;
    }

    public void totalSize(long time, long totalSize) {
        this.totalSize = totalSize;
        updateOverallTime(time);

        while (!stack.isEmpty()) {
            PathObservation previousPath = stack.pop();
            timeMap.put(previousPath.getPath(), time - previousPath.getTime());
        }
    }

    public void observe(long time, String path) {
        if (totalSize != 0L) {
            throw new IllegalStateException("Total size already reported");
        }
        updateOverallTime(time);

        if (stack.isEmpty()) {
            stack.push(new PathObservation(time, path));
        } else if (!multiplePaths) {
            while (!FileSystemTraverse.isInside(stack.peek().getPath(), path)) {
                PathObservation previousPath = stack.pop();
                timeMap.put(previousPath.getPath(), time - previousPath.getTime());

                if (stack.isEmpty()) {
                    throw new IllegalArgumentException("Path is out of root: " + path);
                }
            }
            stack.push(new PathObservation(time, path));
        } else {
            while (!stack.isEmpty() && !FileSystemTraverse.isInside(stack.peek().getPath(), path)) {
                PathObservation previousPath = stack.pop();
                timeMap.put(previousPath.getPath(), time - previousPath.getTime());
            }
            stack.push(new PathObservation(time, path));
        }
    }

    private void updateOverallTime(long time) {
        if (firstTime == -1) {
            firstTime = time;
        }
        lastTime = time;
    }

    public Map<String, Long> getTimeMap() {
        return timeMap;
    }

    public long getOverallTime() {
        return lastTime - firstTime;
    }

    public boolean hasTotalTime(String path) {
        return timeMap.get(path) != null;
    }


    public int getPathsCount() {
        return timeMap.size();
    }
}
