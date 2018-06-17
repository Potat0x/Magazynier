package magazynier.utils.validators;

/**
 * Interfejs dla walidatorow tekstu.
 *
 * @author ziemniak
 */
public interface Validator {
    /**
     * Sprawdza, czy wartosc jest poprawna.
     *
     * @param value wartosc do sprawdzenia
     * @return true, jezeli wartosc prawidlowa, w przeciwnym wypadku false
     */
    boolean check(String value);
}
