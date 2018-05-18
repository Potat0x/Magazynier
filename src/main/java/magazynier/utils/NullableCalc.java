package magazynier.utils;

public class NullableCalc {
    public static Double multiplyNullable(Double a, Double b) {
        if (a != null && b != null) {
            return a * b;
        }
        return 0.0;
    }

    public static Double netValue(Double gross, Double tax) {
        if (gross != null && tax != null) {
            return gross * (100.0 - tax) / 100.0;
        }
        return 0.0;
    }
}
