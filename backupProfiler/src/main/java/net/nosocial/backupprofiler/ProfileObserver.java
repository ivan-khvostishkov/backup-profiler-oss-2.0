package net.nosocial.backupprofiler;

import java.util.*;

/**
 * @author ikh
 * @since 2/28/16
 */
public class ProfileObserver {

    private final Iterator<PathTime> iterator;


    public ProfileObserver(TimingProfile profile) {
        profile.getTimeMap();
        List<PathTime> list = new ArrayList<>();
        for (Map.Entry<String, Long> entry : profile.getTimeMap().entrySet()) {
            list.add(new PathTime(entry.getKey(), entry.getValue()));
        }

        Collections.sort(list, new Comparator<PathTime>() {
            @Override
            public int compare(PathTime o1, PathTime o2) {
                if (o1.getTime() != o2.getTime()) {
                    return 0 - Math.round(Math.signum(o1.getTime() - o2.getTime()));
                }
                return o1.getPath().compareTo(o2.getPath());
            }
        });

        iterator = list.iterator();
    }

    public PathTime nextPathTime() {
        if (!iterator.hasNext()) {
            return null;
        }
        return iterator.next();
    }
}
