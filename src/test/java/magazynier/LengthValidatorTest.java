package magazynier;

import magazynier.utils.validators.LengthValidator;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class LengthValidatorTest {

    private int MAX_LEN = 3;
    private LengthValidator lengthValidator = new LengthValidator(MAX_LEN);

    @Test
    public void nullTest() {
        assertTrue(lengthValidator.check(null));
    }

    @Test
    public void notNullTest() {
        assertTrue(lengthValidator.check(""));
        assertTrue(lengthValidator.check("a"));
        assertTrue(lengthValidator.check("ab"));
        assertTrue(lengthValidator.check("abc"));
        assertFalse(lengthValidator.check("abcde"));
    }
}
