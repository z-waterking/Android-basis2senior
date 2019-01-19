package com.example.a41626.helloworld2;


import android.os.Message;

import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.TextView;


import java.util.Stack;
import java.util.Timer;
import java.util.TimerTask;


public class MainActivity extends AppCompatActivity {
    //利用handler处理进度条更新
    private ProgressBar pb;
    private TextView pt;
    private Handler handler;
    private Button startButton;
    private ImageView shade;
    private int progress = 0;

    private ImageView chatView;
    private TextView chatText;
    private RadioButton caculateRadio;
    private RadioButton questionRadio;
    //取得问题输入框
    private EditText questionText;
    //取得提问按钮
    private Button questionButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //取得进度条
        pb = findViewById(R.id.progressBar);
        //取得进度文本
        pt = findViewById(R.id.progressText);
        //取得启动按钮
        startButton = findViewById(R.id.startButton);
        //取得头像框
        chatView = findViewById(R.id.chatView);
        //取得展示框
        chatText = findViewById(R.id.chatText);
        //取得问题选项
        caculateRadio = findViewById(R.id.caculateRadio);
        questionRadio = findViewById(R.id.questionRadio);
        //取得问题输入框
        questionText = findViewById(R.id.questionText);
        //取得提问按钮
        questionButton = findViewById(R.id.questionButton);

        //开始时界面只有启动按钮
        //启动按钮点击后，加载进度条，完成后，启动按钮消失，其余组件显示
        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //显示聊天对话框
                show_chat();
                // 利用Handler处理进度条更新
                handler = new Handler() {
                    @Override
                    public void handleMessage(Message msg) {
                        super.handleMessage(msg);
                        if (msg.what == 0x123) {
                            if (progress <= 100) {
                                //随机给一个进度进行更新
                                int randomnum = (int) (Math.random() * 30);
                                progress += randomnum;
                                if(progress > 100)
                                {
                                    progress = 100;
                                }
                                pb.setProgress(progress);
                                pt.setText("加载中..." + Integer.toString(progress) + "%");
                            }
                        }
                    }
                };
                //建立定时器
                new Timer().schedule(new TimerTask() {
                    @Override
                    public void run() {
                        // TODO Auto-generated method stub
                        if(progress < 100)
                        {
                            handler.sendEmptyMessage(0x123);
                        }
                        else
                        {
                            //隐藏
                            pb.setVisibility(View.INVISIBLE);
                            pt.setVisibility(View.INVISIBLE);
                            startButton.setVisibility(View.INVISIBLE);
                            cancel();
                        }
                    }
                }, 0, 500);
            }
        });


//        try {
//            Thread.currentThread().sleep(2000);//阻断2秒
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }


        //提问按钮的点击
        questionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //取得输入问题，分情况判断
                String question = questionText.getText().toString();
                if(question == "")
                {
                    chatText.setText("请输入问题");
                }
                if(caculateRadio.isChecked())
                {
                    try{
                        double result = evaluate(question);
                        chatText.setText("你需要计算的结果为" + Double.toString(result));
                    }catch(Exception e){
                        chatText.setText("你输入的表达式不对哦，请检查");
                    }

                }
                else if(questionRadio.isChecked())
                {
                    chatText.setText("此功能待完善");
                }
                else
                {
                    chatText.setText("请先选中你想向我提问的问题类型哦。");
                }
            }
        });
    }
    //显示聊天对话框
    public void show_chat()
    {
        //展示
        chatView.setVisibility(View.VISIBLE);
        chatText.setVisibility(View.VISIBLE);
        questionText.setVisibility(View.VISIBLE);
        questionButton.setVisibility(View.VISIBLE);
        caculateRadio.setVisibility(View.VISIBLE);
        questionRadio.setVisibility(View.VISIBLE);
        //机器人自我介绍
        chatText.setText("你好，我是哆啦A梦，目前可以帮助你计算数学表达式");
    }
    //计算表达式的值
    public static double evaluate(String expression) {
        char[] tokens = expression.toCharArray();

        Stack<Float> stackOfNum = new Stack<Float>();

        Stack<Character> stackOfOps = new Stack<Character>();

        for (int i = 0; i < tokens.length; i++) {

            if (tokens[i] == ' ')
                continue;

            if (tokens[i] >= '0' && tokens[i] <= '9') {
                StringBuffer sbuf = new StringBuffer();

                while (i < tokens.length && tokens[i] >= '0' && tokens[i] <= '9') {
                    sbuf.append(tokens[i++]);
                }
                i--; // 回退一位
                stackOfNum.push(Float.parseFloat(sbuf.toString()));
            }

            else if (tokens[i] == '(')
                stackOfOps.push(tokens[i]);

            else if (tokens[i] == ')') {
                while (stackOfOps.peek() != '(')
                    stackOfNum.push(caculate(stackOfOps.pop(), stackOfNum.pop(), stackOfNum.pop()));
                stackOfOps.pop();
            }

            else if (tokens[i] == '+' || tokens[i] == '-' || tokens[i] == '*' || tokens[i] == '/') {

                while (!stackOfOps.empty() && hasPrecedence(tokens[i], stackOfOps.peek()))
                    stackOfNum.push(caculate(stackOfOps.pop(), stackOfNum.pop(), stackOfNum.pop()));

                stackOfOps.push(tokens[i]);
            }
        }

        while (!stackOfOps.empty())
            stackOfNum.push(caculate(stackOfOps.pop(), stackOfNum.pop(), stackOfNum.pop()));

        return stackOfNum.pop();
    }

    public static boolean hasPrecedence(char op1, char op2) {
        if (op2 == '(' || op2 == ')')
            return false;
        if ((op1 == '*' || op1 == '/') && (op2 == '+' || op2 == '-'))
            return false;
        else
            return true;
    }

    public static float caculate(char op, float b, float a) {
        switch (op) {
            case '+':
                return a + b;
            case '-':
                return a - b;
            case '*':
                return a * b;
            case '/':
                if (b == 0) {
                    throw new UnsupportedOperationException("Cannot divide by zero");
                }
                return a / b;
        }
        return 0;
    }
}