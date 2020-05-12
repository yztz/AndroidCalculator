package top.yzzblog.calculator;


import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.hardware.input.InputManager;
import android.os.Build;
import android.os.Bundle;
import android.renderscript.Script;
import android.text.Editable;
import android.text.InputType;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethod;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import java.util.HashMap;
import java.util.Scanner;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

/**
 * @author 杨宗振
 * @date 2020-5-7
 */

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    public static final int MAX_FONT_SIZE = 46;
    public static final int MIN_FONT_SIZE = 34;

    private TextView num0, num1, num2, num3, num4, num5, num6, num7, num8, num9,
            equal, multiply, sub, divide, plus, dot, percent, backspace, clear;
    private EditText mEtInput;
    private TextView mTvOutput;

    private String content = "";
    private int currentFontSize = MAX_FONT_SIZE;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        init();
    }

    public void init() {
        //findView
        num0 = findViewById(R.id.num0);
        num1 = findViewById(R.id.num1);
        num2 = findViewById(R.id.num2);
        num3 = findViewById(R.id.num3);
        num4 = findViewById(R.id.num4);
        num5 = findViewById(R.id.num5);
        num6 = findViewById(R.id.num6);
        num7 = findViewById(R.id.num7);
        num8 = findViewById(R.id.num8);
        num9 = findViewById(R.id.num9);

        equal = findViewById(R.id.equal);
        dot = findViewById(R.id.dot);
        percent = findViewById(R.id.percent);
        backspace = findViewById(R.id.del);
        clear = findViewById(R.id.clear);

        sub = findViewById(R.id.sub);
        plus = findViewById(R.id.plus);
        multiply = findViewById(R.id.multiply);
        divide = findViewById(R.id.divide);

        mEtInput = findViewById(R.id.et_input);
        mTvOutput = findViewById(R.id.tv_result);

        //set Listener
        num0.setOnClickListener(this);
        num1.setOnClickListener(this);
        num2.setOnClickListener(this);
        num3.setOnClickListener(this);
        num4.setOnClickListener(this);
        num5.setOnClickListener(this);
        num6.setOnClickListener(this);
        num7.setOnClickListener(this);
        num8.setOnClickListener(this);
        num9.setOnClickListener(this);
        plus.setOnClickListener(this);
        sub.setOnClickListener(this);
        multiply.setOnClickListener(this);
        divide.setOnClickListener(this);
        equal.setOnClickListener(this);
        dot.setOnClickListener(this);
        percent.setOnClickListener(this);
        backspace.setOnClickListener(this);
        clear.setOnClickListener(this);


        //禁用键盘
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mEtInput.setShowSoftInputOnFocus(false);
        }
        //获取焦点，显示光标
        mEtInput.requestFocus();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.plus:
                if (!content.equals("")) content = operatorClear(content) + "+";
                break;
            case R.id.sub:
                if (!content.equals("")) content = operatorClear(content) + "-";
                break;
            case R.id.multiply:
                if (!content.equals("")) content = operatorClear(content) + "*";
                break;
            case R.id.divide:
                if (!content.equals("")) content = operatorClear(content) + "/";
                break;
            case R.id.num0:
                content = ZeroClear(content) + "0";
                break;
            case R.id.num1:
                content = ZeroClear(content) + "1";
                break;
            case R.id.num2:
                content = ZeroClear(content) + "2";
                break;
            case R.id.num3:
                content = ZeroClear(content) + "3";
                break;
            case R.id.num4:
                content = ZeroClear(content) + "4";
                break;
            case R.id.num5:
                content = ZeroClear(content) + "5";
                break;
            case R.id.num6:
                content = ZeroClear(content) + "6";
                break;
            case R.id.num7:
                content = ZeroClear(content) + "7";
                break;
            case R.id.num8:
                content = ZeroClear(content) + "8";
                break;
            case R.id.num9:
                content = ZeroClear(content) + "9";
                break;
            case R.id.dot:
                if (!hasPreDot(content)) content += ".";
                break;
            case R.id.percent:
                content = percentClear(content);
                break;
            case R.id.clear:
                content = "";
                mTvOutput.setText("");
                break;
            case R.id.del:
                if (content.length() > 0) content = content.substring(0, content.length() - 1);
                break;
            case R.id.equal:
                if(!content.equals("")) mTvOutput.setText(calculate(content));
                break;
        }
        formatDisplay(content);
    }

    /**
     * 计算部分
     *
     * @param str 算式
     * @return 结果
     */
    public String calculate(String str) {
        ScriptEngine engine = new ScriptEngineManager().getEngineByName("rhino");
        try {
            double result = (double)engine.eval(str);
            return String.valueOf(result);
        } catch (Exception e) {
            return "错误";
        }
    }


    /**
     * 输入整数时调用，以去除开头(后不为运算符或小数点)的0，或运算符后的0
     * Example:0123，123+01
     *
     * @param str 算式
     * @return 清除0后的式子
     */
    public String ZeroClear(String str) {
        if (str.startsWith("0")
                && !str.startsWith(".", 1)
                && !str.startsWith("+", 1)
                && !str.startsWith("-", 1)
                && !str.startsWith("*", 1)
                && !str.startsWith("/", 1)) {
            return str.substring(1);
        } else if (str.endsWith("+0") || str.endsWith("-0") || str.endsWith("/0") || str.endsWith("*0")) {
            return str.substring(0, str.length() - 1);
        } else {
            return str;
        }
    }

    /**
     * 末尾若有运算符，则清除之
     *
     * @param str 算式
     * @return 清除后的式子
     */
    public String operatorClear(String str) {
        if (str.endsWith("+") || str.endsWith("-") || str.endsWith("*") || str.endsWith("/"))
            return str.substring(0, str.length() - 1);
        else
            return str;
    }

    /**
     * 用于判断一个算子中是否已经存在小数点
     *
     * @param str 算式
     * @return exit?true:false
     */
    public boolean hasPreDot(String str) {
        if (str.matches("[\\d\\D]{0,}[\\+\\-\\*/]?\\d{0,}\\.\\d{0,}$"))
            return true;
        else
            return false;
    }

    /**
     * 按下“%”时调用,清除%以便计算,例如“27 + 1”，返回“0.27 + 0.01”
     *
     * @param str 算式
     * @return 消除百分号的算式
     */
    public String percentClear(String str) {
        if (!str.endsWith("+")
                && !str.endsWith("-")
                && !str.endsWith("*")
                && !str.endsWith("/")) {
            String[] operands = getOperand(str);
            StringBuffer str1 = new StringBuffer(str);
            StringBuffer target = new StringBuffer(getOperand(str)[operands.length - 1]);

            int dotPos = target.length();
            //找到小数点位置
            for (int i = 0; i < target.length(); i++) {
                if (target.charAt(i) == '.') {
                    dotPos = i;
                    break;
                }
            }
            //删除原先的操作数
            str1.delete(str.length() - target.length(), str.length());
            //删除原有小数点
            target.replace(dotPos, dotPos + 1, "");

            //小数点前移两位
            dotPos -= 2;

            if (dotPos > 0) {
                target.insert(dotPos, ".");
            } else {
                for (int i = 0; i < -dotPos; i++) {
                    target.insert(0, "0");
                }
                target.insert(0, "0.");
            }
            str1.append(target);
            return str1.toString();
        }

        return str;
    }

    /**
     * 将内容格式化显示
     * 例如123456 -> 123,456
     *
     * @param str
     */
    public void formatDisplay(String str) {
        //根据字数设置字体大小
        HashMap<Integer, Integer> map = new HashMap<>();
        /*字符数-字体大小对照表
        CharNum fontSize
           13      46(MAX)
           14      43
           15      40
           16      37
           17      34(MIN)
         */
        map.put(13, 46);
        map.put(14, 43);
        map.put(15, 40);
        map.put(16, 37);
        map.put(17, 34);
        if (str.length() < 13) {
            mEtInput.setTextSize(TypedValue.COMPLEX_UNIT_SP, MAX_FONT_SIZE);
        } else if (str.length() > 17) {
            mEtInput.setTextSize(TypedValue.COMPLEX_UNIT_SP, MIN_FONT_SIZE);
        } else {
            mEtInput.setTextSize(TypedValue.COMPLEX_UNIT_SP, map.get(str.length()));
        }
        mEtInput.setText(str);
        mEtInput.setSelection(str.length());

    }

    /**
     * 用于获取算式中的操作数例如“1+2”，返回“1”，“2”
     *
     * @param str 算式
     * @return 操作数字符串数组
     */
    public String[] getOperand(String str) {
        return str.split("[\\+\\-\\*/]");
    }
}
