package com.bk.calcbox;

import java.text.DecimalFormat;

public class CalcFunc {

    DecimalFormat formatter;

    public String Calc(String ifade) {
        try {
            // Multiplication and Division Operations
            ifade = ifade.replaceAll(",", "");
            while (ifade.contains("×") || ifade.contains("÷")) {
                int carpmaIndex = ifade.indexOf("×");
                int bolmeIndex = ifade.indexOf("÷");

                int operatorIndex = (carpmaIndex != -1 && (bolmeIndex == -1 ||
                        carpmaIndex < bolmeIndex)) ? carpmaIndex : bolmeIndex;

                // Get the numbers to the left and right of the operator
                int solIndex = operatorIndex - 1;
                while (solIndex >= 0 && (Character.isDigit(ifade.charAt(solIndex))
                        || ifade.charAt(solIndex) == '.' || ifade.charAt(solIndex) == 'E')) {
                    solIndex--;
                }
                double sayi1 = Double.parseDouble(ifade.substring(solIndex + 1, operatorIndex));

                int sagIndex = operatorIndex + 1;
                boolean stopSagIndex = false;
                while (sagIndex < ifade.length() && (Character.isDigit(ifade.charAt(sagIndex))
                        || ifade.charAt(sagIndex) == '.' || ifade.charAt(sagIndex) == 'E')) {
                    sagIndex++;
                    stopSagIndex = true;
                }
                double sayi2;
                if (sagIndex == ifade.length() && !stopSagIndex) {
                    sayi2 = 1;
                } else {
                    sayi2 = Double.parseDouble(ifade.substring(operatorIndex + 1, sagIndex));
                }
                // Perform the action and update the statement
                double sonuc;
                if (ifade.charAt(operatorIndex) == '×') {
                    sonuc = sayi1 * sayi2;
                } else {
                    sonuc = sayi1 / sayi2;
                }
                ifade = ifade.substring(0, solIndex + 1) + sonuc + ifade.substring(sagIndex);
                sagIndex--;
            }

            // Addition and Subtraction OperationsUygulamak için tıkla
            if (!ifade.contains("e") && !ifade.contains("E")) {
                ifade = ifade.replaceAll("(?<!\\d)-", "0-");
            }
            ifade = ifade.replaceAll(",", "");
            String[] tokens = ifade.split("(?<=[\\d.])(?=[+-])|(?<=[+-])(?=[\\d.])");
            double sonuc;
            try {
                if (tokens[0].contains("E-") || tokens[0].contains("e-")) {
                    tokens[0] = tokens[0].replace("E-", "E-" + tokens[1]);
                }
                sonuc = Double.parseDouble(tokens[0]);

                for (int i = 1; i < tokens.length; i += 2) {
                    String operator = tokens[i];
                    if (i + 1 >= tokens.length) {
                        break;
                    }

                    double sayi;

                    try {
                        sayi = Double.parseDouble(tokens[i + 1]);
                    } catch (NumberFormatException e) {
                        // If the number is not entered, show an error message and end the loop
                        return "Eksik sayı!";
                    }

                    switch (operator) {
                        case "+":
                            sonuc += sayi;
                            break;
                        case "-":
                            sonuc -= sayi;
                            break;
                    }
                }
            } catch (NumberFormatException e) {
                return "Invalid expression!";
            }

            return formatResult(sonuc);
        } catch (NumberFormatException e) {
            return "Invalid number!";
        }
    }

    // Auxiliary method formatting the result
    public String formatResult(double result) {
        // Checks if the result is an integer
        if (result == (int) result) {
            // If the result is an integer, it is formatted without fractions
            int intResult = (int) result;
            if (String.valueOf(intResult).length() >= 4) {
                // If the number has 4 or more digits, formats with thousands brackets
                formatter = new DecimalFormat("#,###");
                return formatter.format(intResult);
            } else {
                // If the number is less than 4 digits, it normally formats
                return String.valueOf(intResult);
            }
        } else {
            // If the result is fractional, formats as fractional
            formatter = new DecimalFormat("#,###.#########"); // Maximum 9 decimal places
            formatter.setMaximumFractionDigits(12); // Set the maximum number of decimal places you want
            return formatter.format(result);
        }
    }
}
