package com.example.mycalculator;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.EmptyStackException;
import java.util.List;
import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class MainActivity extends AppCompatActivity {
    private int[] number = {R.id.zero, R.id.one, R.id.two, R.id.three,
            R.id.four, R.id.five, R.id.six, R.id.seven, R.id.eight, R.id.nine};  //数字0-9
    private int[] operator = {R.id.add, R.id.sub, R.id.mul, R.id.div,R.id.LeftParentheses,R.id.RightParentheses,R.id.point};  //+ - * / ( ) .
    private Button[] buttonOpt = new Button[operator.length];   //存输入的运算符
    private Button[] buttonsNum = new Button[number.length];    //存输入的数字
    private Button buttonEqu; Button buttonClear;  Button buttonDel;   // = 清零 退格
    private EditText input; TextView output;    //输入 输出
    private static String Text;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        input = (EditText)findViewById(R.id.input);
        input.setText("");
        input.setEnabled(false);
        output = (TextView) findViewById(R.id.output);
        output.setText("");
        
        buttonEqu = (Button)findViewById(R.id.equal);
        buttonEqu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                output.setText(new Calculate(input.getText().toString()).str);
            }
        });

        buttonClear = (Button) findViewById(R.id.clean);
        buttonClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                input.setText("");
                output.setText("");
            }
        });

        buttonDel = (Button) findViewById(R.id.delete);
        buttonDel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!input.getText().toString().isEmpty() ) {
                    Text = input.getText().toString();
                    Text = Text.substring(0, Text.length() - 1);
                    input.setText(Text);
                }
            }
        });

        for (int i = 0; i < operator.length; i++) {
            buttonOpt[i] = (Button) findViewById(operator[i]);
            buttonOpt[i].setOnClickListener(new OptOnClick(buttonOpt[i].getText().toString()));

        }
        for (int j = 0; j < number.length; j++) {
            buttonsNum[j] = (Button) findViewById(number[j]);
            buttonsNum[j].setOnClickListener(new NumberOnClick(buttonsNum[j].getText().toString()));
        }

    }
    //继承OnClick接口
    class NumberOnClick implements View.OnClickListener {
        String Msg;
        //点击按钮传入字符
        public NumberOnClick(String msg) {
            Msg = msg;
        }

        @Override
        public void onClick(View v) {
            if (!output.getText().toString().equals("")) {
                input.setText("");
                output.setText("");
            }
            input.append(Msg);
        }
    }
    class OptOnClick implements  View.OnClickListener{
        String Msg;
        String[] opt_symbol = {"+", "-", "*", "/","."};
        public OptOnClick(String msg) {
            Msg = msg;
        }
        @Override
        public void onClick(View v) {
            if (!output.getText().toString().equals("")) {
                input.setText("");
                output.setText("");
            }
            // 检查是否运算符重复输入
            for (int i = 0; i < opt_symbol.length; i++) {
                if (Msg.equals(opt_symbol[i])) {
                    if (input.getText().toString().split("")
                            [input.getText().toString().split("").length - 1].equals(opt_symbol[i])) {
                        Msg = "";
                    }
                }
            }
            input.append(Msg);
        }
    }

    /**
     * 运算类返回一个String结果
     */
    public class Calculate {
        public  String s1;
        StringBuilder str;

        public Calculate(String m) {
            this.s1 = m;
            try {
                eval();
            } catch (Exception e) {
                str.delete(0, str.length());
                str.append("ERROR");
            }
        }


        public List<String> midToAfter(List<String> midList)throws EmptyStackException{
            List<String> afterList=new ArrayList<String>();
            Stack<String> stack=new Stack<String>();
            for(String str:midList){
                int flag=this.matchWitch(str);
                switch (flag) {
                    case 7:
                        afterList.add(str);
                        break;
                    case 1:
                        stack.push(str);
                        break;
                    case 2:
                        String pop=stack.pop();
                        while(!pop.equals("(")){
                            afterList.add(pop);
                            pop=stack.pop();
                        }
                        break;
                    default:
                        if(stack.isEmpty()){
                            stack.push(str);
                            break;
                        }
                        else{
                            if(stack.peek().equals("(")){
                                stack.push(str);
                                break;
                            }
                            else{
                                int ji1=this.priority(str);
                                int ji2=this.priority(stack.peek());
                                if(ji1>ji2){
                                    stack.push(str);
                                }
                                else{
                                    while(!stack.isEmpty()){
                                        String f=stack.peek();
                                        if(f.equals("(")){
                                            stack.push(str);
                                            break;
                                        }
                                        else{
                                            if(this.priority(str)<=this.priority(f)){
                                                afterList.add(f);
                                                stack.pop();
                                            }
                                            else{
                                                stack.push(str);
                                                break;
                                            }
                                        }
                                    }
                                    if(stack.isEmpty()){
                                        stack.push(str);
                                    }
                                }
                                break;
                            }
                        }
                }
            }
            while(!stack.isEmpty()){
                afterList.add(stack.pop());
            }
            StringBuffer sb=new StringBuffer();
            for(String s:afterList){
                sb.append(s+" ");
            }
            //System.out.println(sb.toString());
            return afterList;
        }

        //判断运算符的优先级
        public int priority(String str){
            int result=0;
            if(str.equals("+")||str.equals("-")){
                result=1;
            }
            else{
                result =2;
            }
            return result;
        }
        //判断字符串是数字、运算符还是括号

        public int matchWitch(String s){
            if(s.equals("(")){
                return 1;
            }
            else if(s.equals(")")){
                return 2;
            }
            else if(s.equals("+")){
                return 3;
            }
            else if(s.equals("-")){
                return 4;
            }
            else if(s.equals("*")){
                return 5;
            }
            else if(s.equals("/")){
                return 6;
            }
            else{
                return 7;
            }
        }
        /**
         *计算a@b的简单方法
         */
        public Double singleEval(Double pop2,Double pop1,String str){
            Double value=0.0;
            if(str.equals("+")){
                value=pop2+pop1;
            }
            else if(str.equals("-")){
                value=pop2-pop1;
            }
            else if(str.equals("*")){
                value=pop2*pop1;
            }
            else {
                value=pop2/pop1;
            }
            return value;
        }
        private double result;

        public double getResult() {
            return result;
        }
        public void setResult(double result) {
            this.result = result;
        }
        private int state;

        public int getState() {
            return state;
        }
        public void setState(int state) {
            this.state = state;
        }

        public void countHouzhui(List<String> list){
            str = new StringBuilder("");
            state=0;
            result=0;
            Stack<Double> stack=new Stack<Double>();
            for(String str:list){
                int flag=this.matchWitch(str);
                switch (flag) {
                    case 3:
                    case 4:
                    case 5:
                    case 6:
                        Double pop1=stack.pop();
                        Double pop2=stack.pop();
                        Double value=this.singleEval(pop2, pop1, str);
                        stack.push(value);
                        break;
                    default:
                        Double push=Double.parseDouble(str);
                        stack.push(push);
                        break;
                }
            }
            if(stack.isEmpty()){
                state=1;
            }
            else{
                result=stack.peek();
                str.append(stack.pop());
            }
        }

        public void eval()throws Exception{
            List<String> list=new ArrayList<String>();
            //匹配运算符、括号、整数、小数， -和*要加\\
            Pattern p = Pattern.compile("[+\\-/\\*()]|\\d+\\.?\\d*");
            Matcher m = p.matcher(s1);
            while(m.find()){
                list.add(m.group());
            }
            List<String> afterList=this.midToAfter(list);
            this.countHouzhui(afterList);
        }
    }
}
