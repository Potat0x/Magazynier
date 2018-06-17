package magazynier.utils;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;

/**
 * Klasa ulatwiajaca formatowania liczb do postaci kwotowej.
 * Zawsze dwa miejsca po przecinku.
 * Jezeli poczatkowa liczba ma wiecej miejsc po przecinku, to zostanie odpowiednio zaokraglona do dwoch miejsc.
 *
 * @author ziemniak
 */

public class MoneyValueFormat {
    private DecimalFormat valueFormat;

    public MoneyValueFormat() {
        valueFormat = new DecimalFormat("0.00");
        DecimalFormatSymbols sym = DecimalFormatSymbols.getInstance();
        sym.setDecimalSeparator('.');
        valueFormat.setDecimalFormatSymbols(sym);
    }


    /**
     * Formatuje wartość
     *
     * @param v wartosc do sformatowania
     * @return sformatowana liczba w postaci Stringa
     */
    public String format(Double v) {
        return valueFormat.format(v);
    }
}
