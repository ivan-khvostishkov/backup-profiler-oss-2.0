package net.nosocial.backupprofiler;

import java.io.File;

/**
 * @author ikh
 * @since 6/22/14
 */
public class RootsEnumerator {
    private final StdOutRoots callback;

    public RootsEnumerator(StdOutRoots stdOutRoots) {

        this.callback = stdOutRoots;
    }

    public void listRoots() {
        for (File root : File.listRoots()) {
            callback.root(root);
        }

    }
}
