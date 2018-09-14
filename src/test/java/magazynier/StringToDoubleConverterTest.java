package magazynier;

import magazynier.utils.StringToDoubleConverter;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class StringToDoubleConverterTest {

    @Test
    public void shouldMatch() {
        List<String> validNumbers = Arrays.asList("0", "1", "-1", "2.23", "-22.3", "3,45", "-4,35");
        for (String vn : validNumbers) {
            assertEquals(Double.valueOf(StringToDoubleConverter.convert(vn)), Double.valueOf(vn.replaceAll(",", ".")));
        }
    }

    @Test
    public void shouldThrowException() {
        List<String> invalidNumbers = Arrays.asList("0.0.", "0..2", ".1", "-1,", "2..23", "-2.2.3", "3,4.5", "-4,35,", "a", ",", ".");
        for (String vn : invalidNumbers) {
            try {
                //noinspection ResultOfMethodCallIgnored
                Double.valueOf(StringToDoubleConverter.convert(vn));
                fail();
            } catch (Exception e) {
                //ok, continue
            }
        }
    }
}