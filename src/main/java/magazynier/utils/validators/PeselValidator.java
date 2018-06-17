package magazynier.utils.validators;

public class PeselValidator implements Validator {
    /**
     * Sprawdza poprawnosc numeru PESEL
     *
     * @param pesel numer PESEL
     * @return true, jezeli PESEL prawidlowy, w przecywnym wypadku false
     */
    @Override
    public boolean check(String pesel) {

        final int PESEL_LEN = 11;

        if (pesel != null && pesel.length() == PESEL_LEN) {

            int mtprs[] = {1, 3, 7, 9, 1, 3, 7, 9, 1, 3};

            int[] digits = new int[PESEL_LEN];
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
