package magazynier;

import magazynier.utils.validators.NipValidator;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class NipTest {

    private static NipValidator nipValidator = new NipValidator();

    @Test
    public void testCorrectNips() {
        String[] correctNips = {"768-000-24-66", "123-456-32-18", "9451972201", "9492107026", "526 - 10 - 44 - 039", "5213415590"};

        for (String nip : correctNips) {
            assertTrue(nipValidator.check(nip));
        }
    }

    @Test
    public void testIncorrectNips() {
        String[] invalidNips = {"", "a", "123", " ", "--", "328945239848879", "90190515836", "768-200-24-66", "123-452-12-18", "2461972201",
                "9492107526", "526 - 18 - 44 - 039", "213415590", "590115590", "20190315833"};

        for (String nip : invalidNips) {
            assertFalse(nipValidator.check(nip));
        }
    }
}
