# AndroidStudyCourse_zsf
此为北理工寒假安卓培训的作业。

## HOMEWORD_20190118
1.使用的组件：

TextVeiw, Button, EditText, ImageView, RadioButton, RadioGroup, progressBar(Horizontal)

2.APP功能：

编写了一个对话机器人，可以用来与用户进行简单的交互，为用户计算出输入的表达式的值。并使用progressBar模拟程序加载。

## HOMEWORK_20190119
# 一、Exercise1
实现方法：

编写MyApplication类继承自Application，在AndroidManifest文件中将此Application注册，实现getname()与setname()方法。
	
在Exercise1.java文件中，在此Activity的onCreate()方法中，利用getApplication()方法取得当前所在的Application, 强制转化为MyApplication对象，判断getname()不为空时，将值显示在TextView中，否则，跳过；在此Activity的onDestroy()方法中，利用setname()方法将TextView中的文本值进行存储。
	
实现效果：

（1）进入页面时，页面显示从onCreate()到onResume()方法的日志。

（2）当旋转手机时，页面显示上一次从onCreate()到onDestroy()的所有周期记录的日志。其中，缓存开始与缓存结束是我手动加入以进行区别的分界线。
若进行多次旋转，会出现如下界面，为正常现象，因为存储时直接将TextView中的内容完全进行存储，最外层的缓存开始与缓存结束中的内容为上一次保存的内容。：

# 二、Exercise2
实现方法：

ViewGroup的getChildCount()取得所有子孩子的数量，getChildAt()根据id（从0开始到孩子数量-1）取得孩子对象。

利用队列对其进行层序遍历。遍历时判断是否是VierGroup的实例，若是，则将其孩子节点加入队列；否则，若是View，则数量加1.考虑空View与其中存在非View对象的情况下，前者返回0，后者返回-1.

实现效果：

遍历顺序如日志所示。

# 三、Exercise3
实现方法：

（1）完成recycleView中的单个item的xml文件。

（2）在Activity中建立item之后，完成Adapter。并在Adapter中进行ViewHolder的建立及相应的方法实现过程，实现了点击Item时跳转页面；同时利用Intent向另一个Activity传递参数。

（3）在聊天室页面建立另外一个recycleView，完成对应的聊天框xml，实现了气泡效果以及对方的聊天内容放在左边，自己的聊天内容放在右边。

实现效果：

（1）消息页面:实现了每个item中的控件设置，包括姓名、描述、头像等的设置。

（2）聊天室页面:实现了名称与头像参数传递、聊天气泡效果、最大长度控制等内容。
