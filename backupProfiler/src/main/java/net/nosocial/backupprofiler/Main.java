package net.nosocial.backupprofiler;

import java.util.Arrays;

/**
 * Success story: 5 years, 512 GB hard drive. Disk almost full, even if I find something to remove,
 * disk space is still full. There is no more single big file that I can easily find and remove.
 * I need to scan almost all the directories deep inside the tree.
 *
 * Took me 1 hour to review 90% of all the files (1 000 000+) and 90% of total data (~485GB).
 *
 * From reviewed files I've cleaned more than 100 000 files and freed 100 GB of space.
 * I've created the lists of Important (include) and Trash (ignore) directories and files.
 *
 * Trash consisted of 13 dirs and files, including swap file, iso image, recycle bin, system dirs and downloads.
 * These files are useless and can be skipped and save space and time for a backup - 300 000 files, 85 GB.
 *
 * There were around 80 important files and directories - that I saved for tracking.
 * In total, after filtering trash, we had 650 000 files and 290 GB space. As we can see, we saved almost half
 * of a backup space.
 *
 * I will run a profile later and if these 70 files still occupy 90% of space and 90% of count in useful files -
 * nothing interesting happened, maybe, only few small files were added. Otherwise, if it's less - something important
 * happened and I will run incremental review, which takes much less time - may be just 10 minutes of my attention.
 *
 * This is how we backup all the important information, with full guarantee that we backup all the important
 * information, if and only important, and we know what this information is, even if we didn't knew before - with
 * the maximum level of detail.
 *
 * @author ikh
 * @since 6/22/14
 */
public class Main {
    public static void main(String[] args) {
        if (args.length > 0 && args[0].equals("--no-gui")) {
            noGuiMode(Arrays.copyOfRange(args, 1, args.length));
            System.exit(0);
        }
        defaultGuiMode(args);
    }

    private static void noGuiMode(String[] args) {
        PrepareProfile.main(args);
    }

    private static void defaultGuiMode(String[] args) {
        LaunchGui.main(args);

    }
}
