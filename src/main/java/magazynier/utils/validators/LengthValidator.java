package magazynier.utils.validators;

public class LengthValidator implements Validator {

    private int maxLength;

    public LengthValidator(int maxLength) {
        this.maxLength = maxLength;
    }

    @Override
    public boolean check(String value) {
        return value == null || value.length() <= maxLength;
    }
}
