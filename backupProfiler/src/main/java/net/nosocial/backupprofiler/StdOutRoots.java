package net.nosocial.backupprofiler;

import java.io.File;

/**
 * @author ikh
 * @since 6/22/14
 */
public class StdOutRoots {
    public void root(File root) {
        System.out.println(root.getAbsolutePath());
    }
}
