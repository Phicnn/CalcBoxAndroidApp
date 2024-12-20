package com.bk.calcbox;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;


public class MainActivity extends AppCompatActivity {

    private TextView textViewSonuc;
    private boolean checkEqualBtn = false;
    private final CalcFunc calcFunc = new CalcFunc();
    private final MaxDigitsInputFilter maxDigitsInputFilter = new MaxDigitsInputFilter(12);

    @SuppressLint({"ClickableViewAccessibility", "SetTextI18n"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textViewSonuc = findViewById(R.id.textViewSonuc);

        Button buttonTopla = findViewById(R.id.buttonTopla);
        Button buttonCikar = findViewById(R.id.buttonCikar);
        Button buttonCarp = findViewById(R.id.buttonCarp);
        Button buttonBol = findViewById(R.id.buttonBol);
        Button buttonEsit = findViewById(R.id.buttonEsit);
        Button buttonSil = findViewById(R.id.buttonSil);

        // Button click listeners
        buttonTopla.setOnClickListener(v -> onOperatorClick(buttonTopla.getText().toString()));
        buttonCikar.setOnClickListener(v -> onOperatorClick(buttonCikar.getText().toString()));
        buttonCarp.setOnClickListener(v -> onOperatorClick(buttonCarp.getText().toString()));
        buttonBol.setOnClickListener(v -> onOperatorClick(buttonBol.getText().toString()));

        buttonEsit.setOnTouchListener(new View.OnTouchListener() {
            private CountDownTimer countDownTimer;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                textViewSonuc.setFilters(new MaxDigitsInputFilter[0]); // Remove input filter
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    countDownTimer = new CountDownTimer(1000, 1000) {
                        @Override
                        public void onTick(long millisUntilFinished) {
                            // A transaction can be made every second
                        }

                        @Override
                        public void onFinish() {
                            textViewSonuc.setText(""); // Clear screen

                        }
                    }.start();
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    if (countDownTimer != null) {
                        countDownTimer.cancel(); // Cancel counter
                    }
                    // To calculate, we send our value to CalcFunc
                    String result = calcFunc.Calc(textViewSonuc.getText().toString());
                    if (isNumeric(result)) {
                        textViewSonuc.setText(result);
                        checkEqualBtn = true;
                    }

                }
                return true;
            }
        });
        buttonSil.setOnClickListener(v -> {
            String currentExpression = textViewSonuc.getText().toString();
            if (!currentExpression.isEmpty()) {
                try {
                    currentExpression = currentExpression.substring(0, currentExpression.length() - 1);
                    String splitted[] = currentExpression.split("(?<=[\\d.])(?=[+\\-×÷])|(?<=[+\\-×÷])(?=[\\d.])");
                    String lastIndex = splitted[splitted.length-1].replaceAll(",","");
                    lastIndex = calcFunc.formatResult(Double.parseDouble(lastIndex));
                    splitted[splitted.length-1] = lastIndex;
                    currentExpression = String.join("",splitted);
                    textViewSonuc.setText(currentExpression);
                } catch (NumberFormatException e) {
                    if (!currentExpression.isEmpty()) {
                        textViewSonuc.setText(currentExpression.substring(0, currentExpression.length() - 1));
                    } else {
                        textViewSonuc.setText(currentExpression);
                    }
                }

            }
        });
    }


    // Helper function to handle operator button clicks
    @SuppressLint("SetTextI18n")
    private void onOperatorClick(String operator) {
        String currentExpression = textViewSonuc.getText().toString();
        checkEqualBtn = false;

        if (!currentExpression.isEmpty() && !isOperator(currentExpression.charAt(currentExpression.length() - 1))) {
            textViewSonuc.append(operator);
        } else if (!currentExpression.isEmpty()) {
            // Replace the last operator with the new one
            textViewSonuc.setText(currentExpression.substring(0, currentExpression.length() - 1) + operator);
        } else if (operator.equals("-") && currentExpression.isEmpty()) {
            textViewSonuc.append(operator);
        }
    }

    private boolean isOperator(char karakter) {
        return karakter == '+' || karakter == '-' || karakter == '×' || karakter == '÷';
    }

    public void sayiTiklandi(View view) {
        Button button = (Button) view;
        String count = button.getText().toString();
        if (checkEqualBtn || (!textViewSonuc.getText().toString().matches(".*[×÷\\-+].*")
                && textViewSonuc.getText().toString().toLowerCase().contains("e"))) {
            textViewSonuc.setText("");
            checkEqualBtn = false;
        }
        textViewSonuc.append(count);

        String ifade = textViewSonuc.getText().toString();
        // Separate expression by operators
        String[] parts = ifade.split("[^0-9,]"); // Sort by characters other than numbers and commas
        textViewSonuc.setFilters(new MaxDigitsInputFilter[]{maxDigitsInputFilter});
        if (parts.length > 1) {
            // The part after the operator
            String lastPart = parts[parts.length - 1];
            String processedLastPart = calcFunc.formatResult(Double.parseDouble(lastPart.replaceAll(",", "")));
            String newIfade = ifade.substring(0, ifade.lastIndexOf(lastPart)) + processedLastPart;
            textViewSonuc.setText(newIfade);
        } else {
            textViewSonuc.setText(calcFunc.formatResult(Double.parseDouble(ifade.replaceAll(",", ""))));
        }
    }

    public static boolean isNumeric(String strNum) {
        if (strNum == null || strNum.isEmpty()) {
            return false;
        }
        try {
            if (strNum.contains(",")) {
                strNum = strNum.replace(",", "");
            }
            Double.parseDouble(strNum);
        } catch (NumberFormatException nfe) {
            return false;
        }
        return true;
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("textViewSonuc", textViewSonuc.getText().toString());
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        textViewSonuc.setText(savedInstanceState.getString("textViewSonuc"));
    }

}