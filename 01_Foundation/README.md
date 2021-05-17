# 01_Foundation

## 1.实现效果

![img](./01_Foundation/pics/homework_20190118.gif)

## 2.使用的组件： 

* TextVeiw
```xml
<TextView
        android:id="@+id/chatText"
        android:layout_width="0dp"
        android:layout_height="0dp"
        android:layout_marginBottom="19dp"
        android:layout_marginEnd="63dp"
        android:layout_marginStart="63dp"
        android:gravity="center_horizontal"
        android:text="Hello World!"
        android:visibility="invisible"
        app:layout_constraintBottom_toTopOf="@+id/questionText"
        app:layout_constraintEnd_toEndOf="parent"
        app:layout_constraintStart_toStartOf="parent"
        app:layout_constraintTop_toBottomOf="@+id/progressText" />
```
* Button
```xml
    <Button
        android:id="@+id/questionButton"

        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:layout_marginBottom="16dp"
        android:text="提问"
        android:visibility="invisible"
        app:layout_constraintBottom_toBottomOf="parent"
        app:layout_constraintEnd_toEndOf="parent"
        app:layout_constraintStart_toStartOf="parent"
        app:layout_constraintTop_toBottomOf="@+id/radioGroup" />
```

* EditText
```xml
<EditText
        android:id="@+id/questionText"
        android:layout_width="224dp"
        android:layout_height="0dp"
        android:layout_marginBottom="1dp"
        android:gravity="center_horizontal"
        android:hint="请输入你的问题"
        android:maxLines="2"
        android:visibility="invisible"
        app:layout_constraintBottom_toTopOf="@+id/startButton"
        app:layout_constraintEnd_toEndOf="parent"
        app:layout_constraintStart_toStartOf="parent"
        app:layout_constraintTop_toBottomOf="@+id/chatText" />
```
* ImageView
```xml
<ImageView
        android:id="@+id/logoView"
        android:layout_width="0dp"
        android:layout_height="0dp"
        android:layout_marginBottom="24dp"
        app:layout_constraintBottom_toTopOf="@+id/chatView"
        app:layout_constraintEnd_toEndOf="parent"
        app:layout_constraintStart_toStartOf="parent"
        app:layout_constraintTop_toTopOf="parent"
        app:srcCompat="@drawable/logo" />
```
* RadioButton
```xml
<RadioButton
            android:id="@+id/caculateRadio"
            android:layout_width="54dp"
            android:layout_height="match_parent"
            android:layout_weight="1"
            android:text="计算表达式"
            android:visibility="invisible" />
```
* RadioGroup
```xml
<RadioGroup
        android:id="@+id/radioGroup"
        android:layout_width="318dp"
        android:layout_height="0dp"
        android:layout_marginBottom="13dp"
        android:layout_marginEnd="16dp"
        android:orientation="horizontal"
        app:layout_constraintBottom_toTopOf="@+id/questionButton"
        app:layout_constraintEnd_toEndOf="parent"
        app:layout_constraintTop_toBottomOf="@+id/startButton">
```

* progressBar(Horizontal)
```xml
<ProgressBar
        android:id="@+id/progressBar"
        style="?android:attr/progressBarStyleHorizontal"
        android:layout_width="243dp"
        android:layout_height="33dp"
        app:layout_constraintBottom_toTopOf="@+id/progressText"
        app:layout_constraintEnd_toEndOf="parent"
        app:layout_constraintStart_toStartOf="parent"
        app:layout_constraintTop_toBottomOf="@+id/chatView" />
```

## 3.APP功能：

*  1.取得各个元素
```java
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
```
*  2.点击启动按钮加载进度条，同时在TextView中动态更新进度
```java
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
                                if (progress > 100) {
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
                        if(progress < 100) {
                            handler.sendEmptyMessage(0x123);
                        } else {
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
```
*   3.点击提问按钮，将输入框中的内容与RadioGroup中的内容一同取得，若计算表达式RadioButton被选中，则计算中序表达式。
```java
//提问按钮的点击
        questionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //取得输入问题，分情况判断
                String question = questionText.getText().toString();
                System.out.println(question);
                if(question != "") {
                    if(caculateRadio.isChecked()) {
                        try {
                            double result = evaluate(question);
                            chatText.setText("你需要计算的结果为" + Double.toString(result));
                        } catch (Exception e) {
                            chatText.setText("你输入的表达式不对哦，请检查");
                        }
                    } else if (questionRadio.isChecked()) {
                        chatText.setText("此功能待完善");
                    } else {
                        chatText.setText("请先选中你想向我提问的问题类型哦。");
                    }
                } else {
                    chatText.setText("请输入问题");
                }
            }
        });
```

```java
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
            } else if (tokens[i] == '(') {
                stackOfOps.push(tokens[i]);
            } else if (tokens[i] == ')') {
                while (stackOfOps.peek() != '(') {
                    stackOfNum.push(caculate(stackOfOps.pop(), stackOfNum.pop(), stackOfNum.pop()));
                }
                stackOfOps.pop();
            } else if (tokens[i] == '+' || tokens[i] == '-' || tokens[i] == '*' || tokens[i] == '/') {
                while (!stackOfOps.empty() && hasPrecedence(tokens[i], stackOfOps.peek())) {
                    stackOfNum.push(caculate(stackOfOps.pop(), stackOfNum.pop(), stackOfNum.pop()));
                }
                stackOfOps.push(tokens[i]);
            }
        }

        while (!stackOfOps.empty()) {
            stackOfNum.push(caculate(stackOfOps.pop(), stackOfNum.pop(), stackOfNum.pop()));
        }

        return stackOfNum.pop();
    }
```
    

