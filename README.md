# 此为北理工寒假培训安卓作业
# 各个ReadMe在各自的文件夹中,且其中都有各自的gif演示图。
# 请各位分管不同主题的老师移步到各文件夹中查看。

# HOMEWORK_20190118_Foundation

## 1.实现效果

<p align="center">
    <img src="./HOMEWORK_20190118_Foundation/pics/homework_20190118.gif" alt="Sample"  width="300" height="500">
    <p align="center">
        <em>exercise1 demo</em>
    </p>
</p>

## 2.使用的组件： 

**TextVeiw
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
**Button
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

**EditText
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
**ImageView
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
**RadioButton
```xml
<RadioButton
            android:id="@+id/caculateRadio"
            android:layout_width="54dp"
            android:layout_height="match_parent"
            android:layout_weight="1"
            android:text="计算表达式"
            android:visibility="invisible" />
```
**RadioGroup
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

**progressBar(Horizontal)
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

*   1.取得各个元素
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
*   2.点击启动按钮加载进度条，同时在TextView中动态更新进度
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
    




# HOMEWORK_20190119_UI

## 1、Exercise1

* 实现效果

进入页面时，页面显示从onCreate()到onResume()方法的日志。
	
当旋转手机时，页面显示上一次从onCreate()到onDestroy()的所有周期记录的日志。其中，缓存开始与缓存结束是我手动加入以进行区别的分界线。
若进行多次旋转，会出现如下界面，为正常现象，因为存储时直接将TextView中的内容完全进行存储，最外层的缓存开始与缓存结束中的内容为上一次保存的内容。：

<p align="center">
    <img src="./HOMEWORK_20190119_UI/pics/homework_exercise1_20190119.gif" alt="Sample"  width="300" height="500">
    <p align="center">
        <em>Exercise1 demo</em>
    </p>
</p>

* 实现方法

**编写MyApplication类继承自Application，在AndroidManifest文件中将此Application注册，实现getname()与setname()方法。
```java
public class MyApplication extends Application {
    public String name;
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public void onCreate() {
        // TODO Auto-generated method stub
        super.onCreate();
    }
}
```
**在Exercise1.java文件中，在此Activity的onCreate()方法中，利用getApplication()方法取得当前所在的Application, 强制转化为MyApplication对象，判断getname()不为空时，将值显示在TextView中，否则，跳过；在此Activity的onDestroy()方法中，利用setname()方法将TextView中的文本值进行存储。
```java
 @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lifecycle);
        mLifecycleDisplay = findViewById(R.id.tv_loglifecycle);
        //取得MyApplication,若取得到数据，则展示
        app = (MyApplication) getApplication();
        if(app.getName() == null){
            System.out.println("无缓存内容");
        } else {
            mLifecycleDisplay.append("缓存开始\n");
            mLifecycleDisplay.append(app.getName());
            mLifecycleDisplay.append("缓存结束\n");
        }
//        if (savedInstanceState != null) {
//            if (savedInstanceState.containsKey(LIFECYCLE_CALLBACKS_TEXT_KEY)) {
//                mLifecycleDisplay.append("缓存开始\n");
//                String savedContent = (String) savedInstanceState.get(LIFECYCLE_CALLBACKS_TEXT_KEY);
//                mLifecycleDisplay.append(savedContent);
//                mLifecycleDisplay.append("缓存结束\n");
//            }
//        }
        logAndAppend(ON_CREATE);
    }
```

## 2、Exercise2
* 实现效果：
<p align="center">
    <img src="./HOMEWORK_20190119_UI/pics/homework_exercise2_20190119.gif" alt="Sample"  width="300" height="500">
    <p align="center">
        <em>Exercise2 demo</em>
    </p>
</p>
* 实现方法：
**文件中的View结构如图所示。
<p align="center">
    <img src="./HOMEWORK_20190119_UI/pics/trees.png" alt="Sample"  width="300" height="500">
    <p align="center">
        <em>Exercise2 Tree</em>
    </p>
</p>
**利用ViewGroup的getChildCount()取得所有子孩子的数量，getChildAt()根据id（从0开始到孩子数量-1）取得孩子对象。

**再利用队列对其进行层序遍历。遍历时判断是否是VierGroup的实例，若是，则将其孩子节点加入队列；否则，若是View，则数量加1.考虑空View与其中存在非View对象的情况下，前者返回0，后者返回-1.
```java
//将其看作树结构，进行层序遍历
    public int getAllChildViewCount(View view) {
        int count = 0;
        if(view == null) {
            return count;
        }
        Queue<View> queue = new LinkedList<View>();
        //根节点入队列
        queue.offer(view);
        while(!queue.isEmpty()) {
            View node = queue.poll();
            //如果是一个ViewGroup，将其子节点全部加入
            if(node instanceof ViewGroup)
            {
                int c =  ((ViewGroup)node).getChildCount();
                for(int i = 0; i < c; i++)
                {
                    queue.offer(((ViewGroup) node).getChildAt(i));
                }
            } else if (node instanceof View) {
                Log.d("zhangsifan", ((TextView)node).getText().toString());
                count++;
            } else {
                //出错
                return -1;
            }
        }
        return count;
    }
```
## 三、Exercise3
* 实现效果：
**（1）消息页面:实现了每个item中的控件设置，包括姓名、描述、头像等的设置。

**（2）聊天室页面:实现了名称与头像参数传递、聊天气泡效果、最大长度控制等内容。

<p align="center">
    <img src="./HOMEWORK_20190119_UI/pics/homework_exercise3_20190119.gif" alt="Sample"  width="300" height="500">
    <p align="center">
        <em>Exercise3 demo</em>
    </p>
</p>

* 实现方法：

**（1）完成recycleView中的单个item的xml文件。**
```xml
<?xml version="1.0" encoding="utf-8"?>
<RelativeLayout xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:tools="http://schemas.android.com/tools"
    android:layout_width="match_parent"
    android:layout_height="wrap_content"
    android:paddingLeft="16dp"
    android:paddingTop="12dp"
    android:paddingRight="16dp"
    android:paddingBottom="10dp"
    tools:background="@color/colorBackground"
    android:background="@drawable/recycler_item_selector">

    <FrameLayout
        android:id="@+id/iv_avatar_header"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:layout_gravity="center_vertical"
        android:layout_marginEnd="12dp"
        android:layout_marginRight="12dp">

        <!--放在默认位置-->
        <chapter.android.aweme.ss.com.homework.widget.CircleImageView
            android:id="@+id/iv_avatar"
            android:layout_width="48dp"
            android:layout_height="48dp"
            android:scaleType="centerCrop"
            android:src="@drawable/icon_girl" />

        <!--放在右下角-->
        <ImageView
            android:id="@+id/robot_notice"
            style="@style/IMVerifyBdage"
            tools:visibility="visible" />
    </FrameLayout>

    <!-- toRightOf 在X的右边-->
    <TextView
        android:id="@+id/tv_title"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:layout_toRightOf="@id/iv_avatar_header"
        android:textColor="#ffffff"
        android:textSize="15sp"
        tools:text="AABBCC" />

    <TextView
        android:id="@+id/tv_description"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:layout_below="@id/tv_title"
        android:layout_alignLeft="@id/tv_title"
        android:layout_marginTop="4dp"
        android:singleLine="true"
        android:textColor="#4cffffff"
        android:textSize="13sp"
        tools:text="123321123" />

    <TextView
        android:id="@+id/tv_time"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:layout_alignParentRight="true"
        android:textColor="#4cffffff"
        android:textSize="12sp"
        tools:text="5分钟前" />

</RelativeLayout>
```

**（2）在Activity中建立item之后，完成Adapter。并在Adapter中进行ViewHolder的建立及相应的方法实现过程，实现了点击Item时跳转页面；同时利用Intent向另一个Activity传递参数。
```java

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder>{

    List<Message> mData;
    //点击事件监听
    private final ListItemClickListener mOnClickListener;

    public MyAdapter(List<Message> data, ListItemClickListener listener){
        mData = data;
        mOnClickListener = listener;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        Context context = viewGroup.getContext();
        int layoutIdForListItem = R.layout.im_list_item;
        LayoutInflater inflater = LayoutInflater.from(context);
        boolean shouldAttachToParentImmediately = false;

        View view = inflater.inflate(layoutIdForListItem, viewGroup, shouldAttachToParentImmediately);
        MyViewHolder viewHolder = new MyViewHolder(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder myViewHolder, int position) {
        Message message = mData.get(position);
        myViewHolder.updateUI(message);
    }

    @Override
    public int getItemCount() {

        return mData.size();
    }
    //实现了消息页面
    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        //title
        private final TextView view_tv_title;
        private final TextView view_tv_description;
        private final TextView view_tv_time;
        private final ImageView view_iv_avator;
        private final ImageView view_robot_notice;
        private String icon_str;
        public MyViewHolder(@NonNull View itemView){
            super(itemView);
            //名称，描述，时间
            view_tv_title = (TextView) itemView.findViewById(R.id.tv_title);
            view_tv_description = (TextView) itemView.findViewById(R.id.tv_description);
            view_tv_time = (TextView) itemView.findViewById(R.id.tv_time);
            //头像，是否官方
            view_iv_avator = (ImageView) itemView.findViewById(R.id.iv_avatar);
            view_robot_notice = (ImageView) itemView.findViewById(R.id.robot_notice);
            itemView.setOnClickListener(this);
        }

        public void updateUI(Message message){
            //设置名称，描述，时间
            view_tv_title.setText(message.getTitle());
            view_tv_description.setText(message.getDescription());
            view_tv_time.setText(message.getTime());
            //设置头像
            if(message.getIcon().equals("TYPE_ROBOT")) {
                view_iv_avator.setTag(R.drawable.session_robot);
            } else if(message.getIcon().equals("TYPE_GAME")){
                view_iv_avator.setImageResource(R.drawable.icon_micro_game_comment);
            } else if(message.getIcon().equals("TYPE_SYSTEM")){
                view_iv_avator.setImageResource(R.drawable.session_system_notice);
            } else if(message.getIcon().equals("TYPE_USER")){
                view_iv_avator.setImageResource(R.drawable.icon_girl);
            } else if(message.getIcon().equals("TYPE_STRANGER")){
                view_iv_avator.setImageResource(R.drawable.session_stranger);
            }
            icon_str = message.getIcon();

//            view_robot_notice.setImageResource(R.drawable.im_icon_notice_official);
            //设置是否官方账号
            if(message.isOfficial() == true) {
                view_robot_notice.setImageResource(R.drawable.im_icon_notice_official);
            }
        }

        @Override
        public void onClick(View v) {
            System.out.println();
            if (mOnClickListener != null) {
                mOnClickListener.onListItemClick(view_tv_title.getText().toString(), icon_str);
            }
        }

    }
    //------------消息页面

    public interface ListItemClickListener {
        void onListItemClick(String chat_target, String image_id);
    }
}

```
**（3）在聊天室页面建立另外一个recycleView，完成对应的聊天框xml，实现了气泡效果以及对方的聊天内容放在左边，自己的聊天内容放在右边。
```java
//建立Recyclelist
        //定义recycleView
        recycleView = findViewById(R.id.chat_list);

        //设置Manager，即设置其样式
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recycleView.setLayoutManager(layoutManager);

        recycleView.setHasFixedSize(true);

        //创建Adapter,将数据传入
        mAdapter = new ChatAdapter(messages);
//
//        //取得文本框和按钮
        edit_view = findViewById(R.id.ed_say);

        send_button = findViewById(R.id.btn_send_info);
        send_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //如果有内容了
                String content = edit_view.getText().toString();
                if(content.equals("") == false)
                {
                    ChatMessage myMessage = new ChatMessage();
                    myMessage.setIsmyself(true);
                    myMessage.setIcon("TYPE_USER");
                    myMessage.setContent(content);
                    mAdapter.Add_Chat_Message(myMessage);
                    ChatMessage targetMessage = new ChatMessage();
                    targetMessage.setIsmyself(false);
                    targetMessage.setIcon(Icon);
                    targetMessage.setContent(content);
                    mAdapter.Add_Chat_Message(targetMessage);
                    recycleView.setAdapter(mAdapter);
                    //清空编辑框
                    edit_view.setText("");
                    //定位recycle到底部
                    recycleView.smoothScrollToPosition(mAdapter.getItemCount()-1);
                }

            }
        });
```


# HOMEWORK_20190121_Network
## 1、Exercise1
* 实现效果
<p align="center">
    <img src="./HOMEWORK_20190121_Network/pics/homework_erercise1_20190121.gif" alt="Sample"  width="300" height="500">
    <p align="center">
        <em>exercise1 demo</em>
    </p>
</p>
* 实现方法：

（1）	在build.gradle中：implementation "com.airbnb.android:lottie:2.7.0"。

```gradle
dependencies {
    implementation fileTree(dir: 'libs', include: ['*.jar'])
    implementation 'com.android.support:appcompat-v7:28.0.0'
    implementation "com.airbnb.android:lottie:2.7.0"
    // TODO 1: 添加对 lottie android 库的依赖. 注意版本使用 2.7.0
    // lottie Android 官网：https://airbnb.io/lottie/android/android.html
}
```
（2）	在activity_main中，加入lottie的定义，设置ID，rawRes,循环等属性。
```xml
<com.airbnb.lottie.LottieAnimationView
        android:id="@+id/animation_view"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        app:lottie_rawRes="@raw/material_wave_loading"
        app:lottie_loop="true"
        app:lottie_autoPlay="false" />

```
（3）	在MainActivity中，根据seekBar的progress，对其比例进行计算，然后设置动画播放的进度。
```xml
@Override
            public void onProgressChanged(final SeekBar seekBar, final int progress, boolean fromUser) {
                // TODO 3: 这里应该调用哪个函数呢
                // 提示1：可以参考 https://airbnb.io/lottie/android/android.html#custom-animators
                // 提示2：SeekBar 的文档可以把鼠标放在 OnProgressChanged 中间，并点击 F1 查看，
                // 或者到官网查询 https://developer.android.com/reference/android/widget/SeekBar.OnSeekBarChangeListener.html#onProgressChanged(android.widget.SeekBar,%20int,%20boolean)
                //直接设置动画进度为Progeress/seekBar的最大值
                animationView.setProgress((float)progress/seekBar.getMax());
            }
```

## 二、Exercise2
* 实现效果
<p align="center">
    <img src="./HOMEWORK_20190121_Network/pics/homework_exercise2_20190121.gif" alt="Sample"  width="300" height="500">
    <p align="center">
        <em>exercise1 demo</em>
    </p>
</p>
* 实现方法

**第一种方法：

利用ObjectAnimator，同时定义ScaleX，ScaleY，Alpha属性，然后将其加入animatorSet的playTogether方法中进行同时播放。
```java
//TODO：第一种方法，利用ObjectAnimator进行实现
        // 在这里实现了一个 ObjectAnimator，对 target 控件的背景色进行修改
        // 可以思考下，这里为什么要使用 ofArgb，而不是 ofInt 呢？
        ObjectAnimator animator1 = ObjectAnimator.ofArgb(target,
                "backgroundColor",
                getBackgroundColor(startColorPicker),
                getBackgroundColor(endColorPicker));
        animator1.setDuration(Integer.parseInt(durationSelector.getText().toString()));
        animator1.setRepeatCount(ObjectAnimator.INFINITE);
        animator1.setRepeatMode(ObjectAnimator.REVERSE);

        // TODO 1：在这里实现另一个 ObjectAnimator，对 target 控件的大小进行缩放，从 1 到 2 循环
        ObjectAnimator animator_scaleX = ObjectAnimator.ofFloat(target,
                "scaleX",
                1,
                        2);
        ObjectAnimator animator_scaleY = ObjectAnimator.ofFloat(target,
                "scaleY",
                1,
                2);
        animator_scaleX.setDuration(Integer.parseInt(durationSelector.getText().toString()));
        animator_scaleX.setRepeatCount(ObjectAnimator.INFINITE);
        animator_scaleX.setRepeatMode(ObjectAnimator.REVERSE);
        animator_scaleY.setDuration(Integer.parseInt(durationSelector.getText().toString()));
        animator_scaleY.setRepeatCount(ObjectAnimator.INFINITE);
        animator_scaleY.setRepeatMode(ObjectAnimator.REVERSE);

        // TODO 2：在这里实现另一个 ObjectAnimator，对 target 控件的透明度进行修改，从 1 到 0.5f 循环
        ObjectAnimator animator_alpha = ObjectAnimator.ofFloat(target,
                "alpha",
                1f,
                        0.5f);
        animator_alpha.setDuration(Integer.parseInt(durationSelector.getText().toString()));
        animator_alpha.setRepeatCount(ObjectAnimator.INFINITE);
        animator_alpha.setRepeatMode(ObjectAnimator.REVERSE);
        // TODO 3: 将上面创建的其他 ObjectAnimator 都添加到 AnimatorSet 中
        animatorSet = new AnimatorSet();
        animatorSet.playTogether(animator1, animator_scaleX, animator_scaleY, animator_alpha);
        animatorSet.start();
```
**第二种方法：

利用ValueAnimator，定义0,1之间的数值发生器，并将其映射为背景、ScaleX，ScaleY，Alpha的值，然后进行展示。
```java
//建立一个从0到1的ValueAnimator
        ValueAnimator anim = ValueAnimator.ofFloat(0f, 1f);
        anim.setDuration(Integer.parseInt(durationSelector.getText().toString()));
        anim.setRepeatCount(ObjectAnimator.INFINITE);
        anim.setRepeatMode(ValueAnimator.REVERSE);
        anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float currentValue = (float)animation.getAnimatedValue();
                System.out.println(currentValue);
                //将当前值映射为对应的需要改变的值
                //对于背景颜色
                int start_color = getBackgroundColor(startColorPicker);
                int end_color = getBackgroundColor(endColorPicker);
                int back_color = start_color;
                //对于颜色的调控不够准确
                //TODO：需要进行更多的文档查阅
                if(start_color != end_color){
                    back_color = (int)(currentValue*(float)(end_color-start_color)) + start_color;
                }
                target.setBackgroundColor(back_color);

                //对于大小
                double target_scaleX = currentValue + 1.0;
                double target_scaleY = currentValue + 1.0;
                target.setScaleX((float)target_scaleX);
                target.setScaleY((float)target_scaleY);
                //对于透明度
                double target_alpha = -0.5*(currentValue-2.0);
                target.setAlpha(1f);
            }
        });
        anim.start();
```


#HOMEWORK_20190122_Animation

## 1.Exercise1

* 实现效果
<p align="center">
    <img src="./HOMEWORK_20190122_Animation/pics/homework_exercise1_20190122.gif" alt="Sample"  width="300" height="500">
    <p align="center">
        <em>exercise1 demo</em>
    </p>
</p>
* 实现方法

**1.实现Cat类的解析
```java
public class Cat {
    // TODO-C1 (1) Implement your Cat Bean here according to the response json
    // 单条内容
    // {"breeds": [],"categories": [{"id": 2,"name": "space"}],"id": "5o","url": "https://cdn2.thecatapi.com/images/5o.gif"}
    @SerializedName("id") private String id;
    @SerializedName("url") private String url;
    @SerializedName("categories") private List<Category> categories;
    //取得ID
    public String getId(){
        return id;
    }
    //设置ID
    public void setId(String id){
        this.id = id;
    }
    //取得url
    public String getUrl(){
        return url;
    }
    //设置url
    public void setUrl(String url){
        this.url = url;
    }

    public List<Category> getCategories() {
        return categories;
    }

    //设置Categorie类
    public static class Category {
        /**
         * [0]
         * id : 2
         * name : space
         */
        @SerializedName("id") private int id;
        @SerializedName("name") private String name;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getName(){
            return name;
        }

        public void setName(String name){
            this.name = name;
        }

        @Override public String toString() {
            return "Categorie{" +
                    "id=" + id +
                    ", name='" + name +
                    "}";
        }
    }

    @Override public String toString() {
        return "Cat{" +
                "id='" + id +'\'' +
                ", url='" + url + '\'' +
                ", categories=" + categories +
                '}';
    }

}
```
**2.实现ICatService的interface,注意参数为List
```java
public interface ICatService {
    // TODO-C1 (2) Implement your Cat Request here, url: https://api.thecatapi.com/v1/images/search?limit=5
    @GET("v1/images/search?limit=5") Call<Cat[]> randomCat();
}
```
**3.实现requestData方法，
***在NetworkUtils中实现getResponseWithRetrofitAsync_Feed方法
```java
//实现getResponseWithRetrofitAsync方法用来请求Feed数据
    public static void getResponseWithRetrofitAsync_Feed(Callback<FeedResponse> callback) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://10.108.10.39:8080/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        retrofit.create(IMiniDouyinService.class).allFeed().
                enqueue(callback);
    }
```
***调用它，并定义回调函数
```java
NetworkUtils.getResponseWithRetrofitAsync_Cat(new Callback<Cat[]>() {
            @Override public void onResponse(Call<Cat[]> call, Response<Cat[]> response) {
                //接收到返回值，开始进行处理。
                List<Cat> cats = new ArrayList<>(Arrays.asList(response.body()));
                loadPics(cats);
                restoreBtn();
            }

            @Override public void onFailure(Call<Cat[]> call, Throwable t) {
                Toast.makeText(Solution2C1Activity.this.getApplicationContext(), t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
```
## 2.Exercise2

* 实现效果
<p align="center">
    <img src="./HOMEWORK_20190122_Animation/pics/homework_exercise2_20190122.gif" alt="Sample"  width="300" height="500">
    <p align="center">
        <em>exercise2 demo</em>
    </p>
</p>
* 实现方法
**1.实现Feed的解析
```java
public class Feed {

    // TODO-C2 (1) Implement your Feed Bean here according to the response json
    //{ "student_id": "3220180826", "user_name": "lq", "image_url": "www", "video_url":"www"}
    @SerializedName("student_id") private String student_id;
    @SerializedName("user_name") private String user_name;
    @SerializedName("image_url") private String image_url;
    @SerializedName("video_url") private String video_url;
    public String getStudent_id(){
        return student_id;
    }

    public String getUser_name(){
        return user_name;
    }

    public String getImage_url(){
        return image_url;
    }

    public String getVideo_url(){
        return video_url;
    }

    @Override public String toString() {
        return "Feed{" +
                "student_id='" + student_id +'\'' +
                ", user_name='" + user_name + '\'' +
                ", image_url=" + image_url + '\'' +
                ", video_url=" + video_url +
                '}';
    }
}
```
**2.实现FeedResponse的解析
```java
public class FeedResponse {

    // TODO-C2 (2) Implement your FeedResponse Bean here according to the response json
    @SerializedName("success") private boolean success;
    @SerializedName("feeds") private List<Feed> feeds;
    public boolean isSuccess(){
        return success;
    }

    public List<Feed> getFeeds(){
        return feeds;
    }

    @Override public String toString() {
        return "Feeds{" +
                "success='" + success +'\'' +
                ", feeds=" + feeds.toString() +
                '}';
    }
}
```
**3.实现PostVideoResponse的解析
```java
public class PostVideoResponse {

    // TODO-C2 (3) Implement your PostVideoResponse Bean here according to the response json
    //{"success":true, "item"{"student_id":"123", "user_name":"123", "image_url":"www", "video_url":"www"}}
    @SerializedName("success") boolean success;
    @SerializedName("item") Item item;

    public boolean IsSuccess(){
        return success;
    }

    public Item getItem(){
        return item;
    }

    public static class Item{
        @SerializedName("student_id") String student_id;
        @SerializedName("user_name") String user_name;
        @SerializedName("image_url") String image_url;
        @SerializedName("video_url") String video_url;
        public String getStudent_id(){
            return student_id;
        }

        public String getUser_name(){
            return user_name;
        }

        public String getImage_url(){
            return image_url;
        }

        public String getVideo_url(){
            return video_url;
        }


        @Override public String toString() {
            return "Item{" +
                    "student_id'" + student_id +
                    ", user_name=" + user_name +
                    ", image_url=" + image_url +
                    ", video_url=" + video_url +
                    '}';
        }
    }

    @Override public String toString() {
        return "PostVideoResponse{" +
                "success'" + success +
                ", item=" + item.toString() +
                '}';
    }
}

```
**4.实现调用选择图片和视频的系统接口
```java
public void chooseImage() {
        // TODO-C2 (4) Start Activity to select an image
        //选择图片
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"),
                PICK_IMAGE);
    }


    public void chooseVideo() {
        // TODO-C2 (5) Start Activity to select a video
        //选择视频
        Intent intent = new Intent();
        intent.setType("video/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Video"),
                PICK_VIDEO);
    }
```
**5.进行视频上传，将所有需要的信息进行处理后上传，注意文件的处理。
```java
private void postVideo() {
        mBtn.setText("POSTING...");
        mBtn.setEnabled(false);

        // TODO-C2 (6) Send Request to post a video with its cover image
        // if success, make a text Toast and show
        //将所有需要的信息打包进行传递
        String student_id = "2120171098";
        String user_name = "zsf";
        MultipartBody.Part upload_image = getMultipartFromUri("cover_image", mSelectedImage);
        MultipartBody.Part upload_video = getMultipartFromUri("video", mSelectedVideo);

        Callback<PostVideoResponse> callback = new Callback<PostVideoResponse>() {
            @Override public void onResponse(Call<PostVideoResponse> call, Response<PostVideoResponse> response) {
                //接收到返回值，开始进行处理。
                PostVideoResponse rs = response.body();
                if(rs.IsSuccess()){
                    //发送成功
                    Toast.makeText(Solution2C2Activity.this, "发送成功！", Toast.LENGTH_SHORT).show();
                    resetSendBtn();
                }
            }
            @Override public void onFailure(Call<PostVideoResponse> call, Throwable t) {
                Toast.makeText(Solution2C2Activity.this.getApplicationContext(), t.getMessage(), Toast.LENGTH_LONG).show();
            }
        };
        Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl("http://10.108.10.39:8080/")
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        retrofit.create(IMiniDouyinService.class).postVideo(student_id, user_name, upload_image, upload_video).
                enqueue(callback);
    }
```
**6.实现IMiniDouyinService的服务。
```java
public interface IMiniDouyinService {
    // TODO-C2 (7) Implement your MiniDouyin PostVideo Request here, url: (POST) http://10.108.10.39:8080/minidouyin/video
    // TODO-C2 (8) Implement your MiniDouyin Feed Request here, url: http://10.108.10.39:8080/minidouyin/feed
    @Multipart
    @POST("minidouyin/video") Call<PostVideoResponse> postVideo(
            @Query("student_id") String student_id,
            @Query("user_name") String username,
            @Part MultipartBody.Part file1,
            @Part MultipartBody.Part file2
            );
    @GET("minidouyin/feed") Call<FeedResponse> allFeed();
}
```
**7.实现请求刷新图片的接口
```java
public void fetchFeed(View view) {
        mBtnRefresh.setText("requesting...");
        mBtnRefresh.setEnabled(false);

        // TODO-C2 (9) Send Request to fetch feed
        // if success, assign data to mFeeds and call mRv.getAdapter().notifyDataSetChanged()
        // don't forget to call resetRefreshBtn() after response received
        NetworkUtils.getResponseWithRetrofitAsync_Feed(new Callback<FeedResponse>() {
            @Override public void onResponse(Call<FeedResponse> call, Response<FeedResponse> response) {
                //接收到返回值，开始进行处理。
                FeedResponse feeds = response.body();
                mFeeds = feeds.getFeeds();
                mRv.getAdapter().notifyDataSetChanged();
                resetRefreshBtn();
            }

            @Override public void onFailure(Call<FeedResponse> call, Throwable t) {
                Toast.makeText(Solution2C2Activity.this.getApplicationContext(), t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }
```
**8.将图片展示在屏幕中
```java
@Override
            public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
                ImageView iv = (ImageView) viewHolder.itemView;

                // TODO-C2 (10) Uncomment these 2 lines, assign image url of Feed to this url variable
                String url = mFeeds.get(i).getImage_url();
                Glide.with(iv.getContext()).load(url).into(iv);
            }
```
