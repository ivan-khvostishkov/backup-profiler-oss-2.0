package net.nosocial.backupprofiler;

import java.io.File;

/**
 * @author ikh
 * @since 6/22/14
 */
public class PrepareProfile {
    public static void main(String[] args) {
        if (args.length == 0) {
            defaultArgs();
            System.exit(0);
        }
        if (args[0].equals("--list-roots")) {
            new RootsEnumerator(new StdOutRoots()).listRoots();
        }
    }

    private static void defaultArgs() {

        // TODO: run as root
//        new FileSystemTraverse(new ProfileEventLog(),
//                new String[]{"/root/backup/local-include.truffle"},
//                new String[]{"/root/backup/local-exclude.truffle", "/root/backup/local-ignore.truffle"}).start();

        String profileStartName = "profile-start.txt";
        if (new File(profileStartName).exists()) {
            if (!new File("important").exists() || new File("unimportant").exists()) {
                new File("important", "size").mkdirs();
                new File("important", "count").mkdirs();
                new File("important", "time").mkdirs();
                new File("unimportant", "size").mkdirs();
                new File("unimportant", "count").mkdirs();
            }
        } else {
            throw new IllegalStateException(profileStartName + " not found");
        }                                                           

        new FileSystemTraverse(new ProfileEventLog("important"),
                new String[]{profileStartName},
                new String[]{"profile-skip.txt",
                        "profile-unimportant.txt"}).start();

        new FileSystemTraverse(new ProfileEventLog("unimportant"),
                new String[]{"profile-unimportant.txt"},
                new String[]{}).start();
    }
}
