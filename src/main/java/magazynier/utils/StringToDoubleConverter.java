package magazynier.utils;

import java.util.regex.Pattern;

public class StringToDoubleConverter {

    private static final Pattern numberPattern = Pattern.compile("-?(([0-9]+)|([0-9]+([,.])[0-9]+))");

    public static double convert(String number) {

        if (!numberPattern.matcher(number).matches()) {
            throw new NumberFormatException("\"" + number + "\" cant be parsed to double.");
        }

        number = number.replaceAll(",", ".");
        return Double.parseDouble(number);
    }
}
