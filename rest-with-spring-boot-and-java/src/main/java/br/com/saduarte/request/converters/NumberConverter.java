package br.com.saduarte.request.converters;

import br.com.saduarte.exception.UnsupportesMathOperationException;

public class NumberConverter {

    public static Double convertToDouble(String strNumber) throws IllegalArgumentException {
        if(strNumber == null || strNumber.isEmpty()) throw new UnsupportesMathOperationException("Please set a numeric value!");
        String number = strNumber.replace(",", ".");
        return Double.parseDouble(number);
    }

    public static boolean isNumeric(String strNumber) {
        if(strNumber == null || strNumber.isEmpty()) return  false;
        String number = strNumber.replace(",", "."); //R$ 5,00 USD 5.00
        return (number.matches("[-+]?[0-9]*\\.?[0-9]+"));
    }
}

