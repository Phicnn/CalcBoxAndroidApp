package com.bk.calcbox;

import android.text.InputFilter;
import android.text.Spanned;

public class MaxDigitsInputFilter implements InputFilter {
    private final int maxDigits;

    public MaxDigitsInputFilter(int maxDigits) {
        this.maxDigits = maxDigits;
    }

    @Override
    public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
        String currentText = dest.toString();
        String newText = currentText.substring(0, dstart) + source.subSequence(start, end) + currentText.substring(dend);
        String[] lines = newText.split("[+\\-×÷]"); // Separate by operators
        for (String line : lines) {
            if (!line.matches(".*\\d+\\.\\d+.*")) { // Skip numbers with periods
                String digits = line.replaceAll("[^0-9]", ""); // Just get the numbers
                if (digits.length() > maxDigits) {
                    return ""; // Block entry
                }
            }
        }

        return null; // Allow entry
    }
}
