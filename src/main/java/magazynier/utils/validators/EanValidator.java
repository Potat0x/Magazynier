package magazynier.utils.validators;

/**
 * Walidator EAN
 *
 * @author ziemniak
 */
public class EanValidator implements Validator {
    /**
     * Sprawdza poprawnosc numeru EAN
     *
     * @param pesel numer EAN
     * @return true, jezeli EAN prawidlowy, w przecywnym wypadku false
     */
    @Override
    public boolean check(String pesel) {

        final int EAN_LEN = 13;

        if (pesel != null && pesel.length() == EAN_LEN) {

            int mtprs[] = {1, 3, 1, 3, 1, 3, 1, 3, 1, 3, 1, 3};

            int[] digits = new int[EAN_LEN];
            for (int i = 0; i < digits.length; i++) {
                char c = pesel.charAt(i);
                if (Character.isDigit(c)) {
                    digits[i] = Character.getNumericValue(c);
                } else {
                    return false;
                }
            }

            int sum = 0;
            for (int i = 0; i < pesel.length() - 1; i++) {
                sum += digits[i] * mtprs[i];
            }

            if ((10 - (sum % 10)) % 10 == Character.getNumericValue(pesel.charAt(pesel.length() - 1)))
                return true;
            else return false;
        }
        return false;
    }
}
