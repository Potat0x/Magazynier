package magazynier;

import magazynier.utils.PeselValidator;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class PeselTest {

    private static PeselValidator peselValidator = new PeselValidator();

    @Test
    public void testCorrectPesels() {
        String[] correctPesels = {"90090515836", "92071314764", "81100216357", "80072909146", "90080517455", "90060804786",
                "65071209862", "67040500538", "05250577108", "44051401458", "00210136412", "00810157509", "22722459609",
                "05250578710", "44051401458", "49040501580", "49040501580"};


        for (String pesel : correctPesels) {
            assertTrue(peselValidator.check(pesel));
        }
    }

    @Test
    public void testIncorrectPesels() {
        String[] invalidPesels = {"", "a", "123", "328945239848879", "90190515836", "92021314764", "81300216357", "80472909146",
                "90880517455", "05250578712", "67040507538", "20210136412", "00810157508", "22722459607", "05550578710",
                "44051451458", "49030501580", "49020501580", "00210136410", "00810157504", "22722459601"};

        for (String pesel : invalidPesels) {
            assertFalse(peselValidator.check(pesel));
        }
    }
}
