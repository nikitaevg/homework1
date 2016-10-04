package ru.ifmo.android_2016.calc;

import android.app.Activity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.Button;
import android.widget.TextView;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Stack;


public final class CalculatorActivity extends Activity implements View.OnClickListener, View.OnLongClickListener {

    Button b_arr[];
    int b_id[];
    String ch[];
    char[] op;
    TextView tv;
    String eq;
    String KEY = "my_key";
    private AlphaAnimation buttonClick = new AlphaAnimation(1F, 0.8F);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calculator);
        tv = (TextView) findViewById(R.id.textView);
        ch = new String[]{"1", "2", "3", "c", "4", "5", "6", "=", "7", "8", "9", "+", "0", "*", "/", "-"};
        b_id = new int[]{R.id.d1, R.id.d2, R.id.d3, R.id.clear, R.id.d4, R.id.d5, R.id.d6, R.id.eqv,
                R.id.d7, R.id.d8, R.id.d9, R.id.add, R.id.d0, R.id.mul, R.id.div, R.id.sub};
        b_arr = new Button[16];
        for (int i = 0; i < 16; i++) {
            b_arr[i] = (Button) findViewById(b_id[i]);
            b_arr[i].setOnClickListener(this);
        }
        b_arr[3].setOnLongClickListener(this);
        if (savedInstanceState == null) {
            eq = "";
            tv.setText("");
        } else {
            tv.setText((eq = savedInstanceState.getCharSequence(KEY).toString()));
        }
        tv.setMovementMethod(new ScrollingMovementMethod());
        op = new char[]{'+', '-', '*', '/'};
    }

    @Override
    public void onClick(View v) {
        v.startAnimation(buttonClick);
        String s;
        for (int i = 0; i < 16; i++) {
            if (v == b_arr[i]) {
                if (i == 3) {
                    s = tv.getText().toString();
                    if (s.equals(""))
                        break;
                    tv.setText(s.subSequence(0, s.length() - 1));
                }
                else if (i == 7) {
                    try {
                        tv.setText(calc(tv.getText().toString()));
                        tv.scrollTo(0, 0);
                    } catch (Exception e) {
                        tv.setText("");
                    }
                }
                else {
                    tv.append(ch[i]);
                }
            }
        }
        eq = tv.getText().toString();
    }

    boolean isSymb(char ch) {
        for (char c : op)
            if (ch == c)
                return true;
        return false;
    }

    BigDecimal operate(BigDecimal a, BigDecimal b, String c) {
        switch (c) {
            case "+":
                return a.add(b);
            case "-":
                return a.subtract(b);
            case "/":
                return a.divide(b, 5, RoundingMode.HALF_UP);
            default:
                return a.multiply(b);
        }
    }

    String calc(String s) {
        if (s.equals(""))
            return "";
        String[] tokens;
        if (s.charAt(0) == '-')
            s = "0" + s;
        for (char c : op)
            s = s.replace(c + "", " " + c + " ");
        tokens = s.split(" ");
        BigDecimal a, b;
        Stack<BigDecimal> st = new Stack<>();
        Stack<String> ops = new Stack<>();
        for (String t : tokens) {
            if (!isSymb(t.charAt(0))) {
                st.push(new BigDecimal(t));
            }
            else if (t.equals("+") || t.equals("-")) {
                while (!ops.isEmpty()) {
                    String c = ops.pop();
                    b = st.pop();
                    a = st.pop();
                    st.push(operate(a, b, c));
                }
                ops.push(t);
            }
            else {
                while (!ops.isEmpty() && (ops.peek().equals("*") || ops.peek().equals("/"))) {
                    b = st.pop();
                    a = st.pop();
                    if (ops.peek().equals("*"))
                        st.push(a.multiply(b));
                    else
                        st.push(a.divide(b, 5, RoundingMode.HALF_UP));
                    ops.pop();
                }
                ops.push(t);
            }
        }
        while (!ops.empty()) {
            String c = ops.pop();
            b = st.pop();
            a = st.pop();
            st.push(operate(a, b, c));
        }
        return st.peek().toString();
    }

    @Override
    public boolean onLongClick(View v) {
        if (v == b_arr[3]) {
            tv.setText("");
        }
        eq = tv.getText().toString();
        return true;
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putCharSequence(KEY, eq);
    }
}
