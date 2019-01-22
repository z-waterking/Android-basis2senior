# HOMEWORK_20190118

* 1.实现效果

<p align="center">
    <img src="./pics/homework_20190118.gif" alt="Sample"  width="300" height="500">
    <p align="center">
        <em>exercise1 demo</em>
    </p>
</p>

* 2.使用的组件： 

** TextVeiw **
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
** Button **
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

** EditText **
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
** ImageView **
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
** RadioButton **
```xml
<RadioButton
            android:id="@+id/caculateRadio"
            android:layout_width="54dp"
            android:layout_height="match_parent"
            android:layout_weight="1"
            android:text="计算表达式"
            android:visibility="invisible" />
```
** RadioGroup **
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

** progressBar(Horizontal) **
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
* 3.APP功能：

编写了一个对话机器人，可以用来与用户进行简单的交互，为用户计算出输入的表达式的值。并使用progressBar模拟程序加载。

