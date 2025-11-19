package com.iot.project2;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.io.CharArrayWriter;

public class SubActivity extends AppCompatActivity {
    private TextView textView;
    private void appendText(String str) {
        String current = textView.getText().toString();
        textView.setText(current + str);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_sub);

        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();
        textView = findViewById(R.id.textView);


        Button button1 = findViewById(R.id.btn1);
        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                appendText("1");
            }
        });
        Button button2 = findViewById(R.id.btn2);
        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                appendText("2");
            }
        });
        Button button3 = findViewById(R.id.btn3);
        button3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                appendText("3");
            }
        });
        Button button4 = findViewById(R.id.btn4);
        button4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                appendText("4");
            }
        });
        Button button5 = findViewById(R.id.btn5);
        button5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                appendText("5");
            }
        });
        Button button6 = findViewById(R.id.btn6);
        button6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                appendText("6");
            }
        });
        Button button7 = findViewById(R.id.btn7);
        button7.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                appendText("7");
            }
        });
        Button button8 = findViewById(R.id.btn8);
        button8.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                appendText("8");
            }
        });
        Button button9 = findViewById(R.id.btn9);
        button9.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                appendText("9");
            }
        });
        Button button0 = findViewById(R.id.btn0);
        button0.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                appendText("0");
            }
        });
        Button buttonadd = findViewById(R.id.btn10);
        buttonadd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                appendText("+");
            }
        });
        Button buttonmiuns = findViewById(R.id.btn11);
        buttonmiuns.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                appendText("-");
            }
        });
        Button buttonmul = findViewById(R.id.btn12);
        buttonmul.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                appendText("*");
            }
        });
        Button buttondiv = findViewById(R.id.btn13);
        buttondiv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                appendText("/");
            }
        });
        Button buttoneq = findViewById(R.id.btneq);
        buttoneq.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String value = textView.getText().toString();
                if (value.isEmpty()) {
                    return;
                }
                try {
                    String[] parts = value.split("(?<=[-+*/])|(?=[-+*/])");
                    double result = Double.parseDouble(parts[0]);
                    for(int i=1; i<parts.length; i+=2) {
                        String operator = parts[i];
                        double nextNumber = Double.parseDouble(parts[i+1]);
                        switch (operator) {
                            case "+":
                                result += nextNumber;
                                break;
                            case "-":
                                result -= nextNumber;
                                break;
                            case "*":
                                result *= nextNumber;
                                break;
                            case "/":
                                if (nextNumber == 0) {
                                    throw new ArithmeticException("0으로 나눌 수 없습니다. 다시 입력하세요");
                                }
                                result /= nextNumber;
                                break;
                        }
                    }
                    textView.setText(String.valueOf(result));
                } catch (NumberFormatException e) {
                    textView.setText("잘못된 수식");
                } catch (ArithmeticException e) {
                    textView.setText(e.getMessage());
                }
            }
        });
        Button buttondel = findViewById(R.id.btndel);
        buttondel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                textView.setText("");
            }
        });
        Button mainB = findViewById(R.id.mainB);
        mainB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SubActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });
    }
}