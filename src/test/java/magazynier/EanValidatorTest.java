package magazynier;

import magazynier.utils.validators.EanValidator;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class EanValidatorTest {

    private EanValidator eanValidator = new EanValidator();

    @Test
    public void testCorrectEans() {
        String[] correctEans = {"8801643134204", "5908267910900", "5908267922644", "4719331301798", "0730143308366",
                "5032037095693", "5032037087131", "0718037856780"};

        for (String ean : correctEans) {
            assertTrue(eanValidator.check(ean));
        }
    }

    @Test
    public void testIncorrectEans() {
        String[] invalidEans = {"", "a", "123", "8801623134204", "3908264910900", "5905267922644", "4712931301798",
                "0730143302346", "07301433308366", "073014330836", "50320370871311", "3032037095653"};

        for (String ean : invalidEans) {
            assertFalse(eanValidator.check(ean));
        }
    }
}
