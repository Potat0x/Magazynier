package magazynier.utils;

/**
 * Umozliwia wykonywanie obliczen na wartosciach mogacych przyjmowac wartosc null.
 *
 * @author ziemniak
 */
public class NullableCalc {

    /**
     * Wykonuje mnozenie. Gdy co najmniej jeden argument jest null, zwraca 0.
     *
     * @param a czynnik 1
     * @param b czynnik 2
     * @return Wynik mnozenia
     */

    public static Double multiplyNullable(Double a, Double b) {
        if (a != null && b != null) {
            return a * b;
        }
        return 0.0;
    }

    /**
     * Oblicza wartosc netto. Gdy co najmniej jeden argument jest null, zwraca 0.
     *
     * @param gross wartosc brutto
     * @param tax   wysokosc podatku w % (liczba z zakresu [0; 100]
     * @return wartosc netto
     */
    public static Double netValue(Double gross, Double tax) {
        if (gross != null && tax != null) {
            return gross * (100.0 - tax) / 100.0;
        }
        return 0.0;
    }
}
