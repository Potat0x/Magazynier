package magazynier.utils.validators;

/**
 * Walidator dlugosci
 *
 * @author ziemniak
 */
public class LengthValidator implements Validator {

    private int maxLength;

    /**
     * @param maxLength maksymalna dozwolona dlugosc tekstu
     */
    public LengthValidator(int maxLength) {
        this.maxLength = maxLength;
    }

    /**
     * Sprawdza, czy tekst nie przekracza dozwolonej dlugosci
     *
     * @param value tekst do sprawdzenia
     * @return true, jezeli dlugosc nie jest przekroczona, w przeciwnym wypadku false
     */
    @Override
    public boolean check(String value) {
        return value == null || value.length() <= maxLength;
    }
}
