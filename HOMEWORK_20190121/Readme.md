
# HOMEWORK_20190121
## 一、Exercise1

1.实现方法：

（1）	在build.gradle中：implementation "com.airbnb.android:lottie:2.7.0"。

（2）	在activity_main中，加入lottie的定义，设置ID，rawRes,循环等属性。

（3）	在MainActivity中，根据seekBar的progress，对其比例进行计算，然后设置动画播放的进度。


## 二、Exercise2
1.实现方法

（1）第一种方法：

利用ObjectAnimator，同时定义ScaleX，ScaleY，Alpha属性，然后将其加入animatorSet的playTogether方法中进行同时播放。

（2）第二种方法：

利用ValueAnimator，定义0,1之间的数值发生器，并将其映射为背景、ScaleX，ScaleY，Alpha的值，然后进行展示。
