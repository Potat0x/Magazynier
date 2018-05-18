package magazynier.utils;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;

public class MoneyValueFormat {
    private DecimalFormat valueFormat;

    public MoneyValueFormat() {
        valueFormat = new DecimalFormat("0.00");
        DecimalFormatSymbols sym = DecimalFormatSymbols.getInstance();
        sym.setDecimalSeparator('.');
        valueFormat.setDecimalFormatSymbols(sym);
    }

    public String format(Double v) {
        return valueFormat.format(v);
    }
}
