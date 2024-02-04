import net.nosocial.backupprofiler.FormatData;
import org.junit.Test;

import static junit.framework.TestCase.assertEquals;

/**
 * @author ikh
 * @since 10/18/19
 */
public class TestFormatNumbers {
    @Test
    public void formatsAllNumbers() {

        assertEquals("10 k", FormatData.humanReadableByteCount(10000));
        assertEquals("3 G", FormatData.humanReadableByteCount(3440797027L));
        assertEquals("11 M", FormatData.humanReadableByteCount(10963210L));
        assertEquals("10 B", FormatData.humanReadableByteCount(10));
        assertEquals("569 M", FormatData.humanReadableByteCount(569023296));
    }
}
