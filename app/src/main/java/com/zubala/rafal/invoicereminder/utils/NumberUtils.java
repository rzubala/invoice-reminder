package com.zubala.rafal.invoicereminder.utils;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;

/**
 * Created by rzubala on 08.03.18.
 */

public class NumberUtils {

    private static DecimalFormat decimalFormatCurrency;

    public static final String NUMBER_FORMAT_CURRENCY = "#.00";

    public static String formatNumberCurrency(Double f) {
        if (f == null) {
        return "";
        }
        String result = "";
        if (decimalFormatCurrency == null) {
            buildDecimalFormatCurrency();
        }
        result = decimalFormatCurrency.format(f);
        if (result.trim().equals("-0")) {
            result = "0";
        }
        return result;
        }


    private static void buildDecimalFormatCurrency() {
        DecimalFormatSymbols otherSymbols = new DecimalFormatSymbols();
        otherSymbols.setDecimalSeparator('.');
        decimalFormatCurrency = (DecimalFormat) DecimalFormat.getInstance();
        decimalFormatCurrency.applyPattern(NUMBER_FORMAT_CURRENCY);
        decimalFormatCurrency.setDecimalFormatSymbols(otherSymbols);
    }
}
