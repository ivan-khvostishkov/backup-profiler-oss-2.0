import net.nosocial.backupprofiler.FormatData;
import org.junit.Test;

import java.io.File;

import static junit.framework.TestCase.assertEquals;

/**
 * @author ikh
 * @since 10/18/19
 */
public class TestFormatFileName {
    @Test
    public void formatsPathsCorrectly() {

        assertEquals("src/test/test-resources/multiple-paths-complete-test.log",
                FormatData.formatFileName(new File("src/test/test-resources/multiple-paths-complete-test.log")));

        assertEquals("src/test/test-resources/",
                FormatData.formatFileName(new File("src/test/test-resources")));
        
        assertEquals("src/test/test-resources/",
                FormatData.formatFileName(new File("src/test/test-resources/")));

        assertEquals("/",
                FormatData.formatFileName(new File("/")));
    }
}
