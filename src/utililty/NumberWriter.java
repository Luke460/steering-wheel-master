package utililty;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

public class NumberWriter {

    private DecimalFormat decimalFormat;

    public NumberWriter(int precision) {
        this.decimalFormat = new DecimalFormat("0", DecimalFormatSymbols.getInstance(Locale.ENGLISH));
        this.decimalFormat.setMaximumFractionDigits(precision);
        this.decimalFormat.setMinimumFractionDigits(1);
    }

    public String getStringValue(double value) {
        return this.decimalFormat.format(value);
    }

}
