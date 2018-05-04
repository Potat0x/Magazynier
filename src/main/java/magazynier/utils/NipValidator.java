package magazynier.utils;

public class NipValidator {

    public static boolean check(String nip) {

        if (nip != null) {
            final int NIP_LEN = 10;
            nip = nip.replaceAll("[- ]", "");
            if (nip.length() == NIP_LEN) {

                int mtprs[] = {6, 5, 7, 2, 3, 4, 5, 6, 7};

                int[] digits = new int[NIP_LEN];
                for (int i = 0; i < digits.length; i++) {
                    char c = nip.charAt(i);
                    if (Character.isDigit(nip.charAt(i))) {
                        digits[i] = Character.getNumericValue(c);
                    } else {
                        return false;
                    }
                }

                int sum = 0;
                for (int i = 0; i < nip.length() - 1; i++) {
                    sum += digits[i] * mtprs[i];
                }

                if ((sum % 11) == Character.getNumericValue(nip.charAt(nip.length() - 1)))
                    return true;
                else return false;
            }
        }


        return false;
    }
}
