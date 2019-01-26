# HOMEWORK_20190126_FaceDetect
## 1.实现效果

<p align="center">
    <img src="./pics/homework_20190126_facedetect.gif" alt="Sample"  width="300" height="500">
    <p align="center">
        <em>Facedetect Demo</em>
    </p>
</p>


## 2.实现方法

* 1.修改人脸检测回调的返回类型

```cpp
    std::function<void(int, int, int, int, int)> mDetectFaceCallback;
```

* 2.增加回调部分传回的参数，将识别出的人脸的矩形的边界值进行返回。
```cpp
mDetectFaceCallback(item.action, item.rect.top, item.rect.bottom, item.rect.left,
                                item.rect.right);
```

* 3.更改回调函数
```cpp
void setDetectFaceCallback(std::function<void(int, int, int, int, int)> callback) {
        mDetectFaceCallback = callback;
    }
```

* 4.更改native-lib的回调
```cpp
if (faceDetectHelperClass != NULL) {
        //TODO zsf 一个I指的是一个int
        detectFaceCallbackMethod = env->GetStaticMethodID(faceDetectHelperClass,
                                                          "nativeOnFaceDetectedCallback", "(IIIII)V");
        if (detectFaceCallbackMethod == NULL) {
            LOGE("detectFaceCallbackMethod NULL");
        } else {
            LOGE("detectFaceCallbackMethod success");
        }
    }

    mObj = env->NewGlobalRef(faceDetectHelperClass);

    if (mFaceDetectHelper == NULL) {
        mFaceDetectHelper = new FaceDetectHelper();
        //TODO zsf
        mFaceDetectHelper->setDetectFaceCallback([](int ret, int top, int bottom, int left, int right) {
            JNIEnv *_env = Android_JNI_GetEnv();
            if (_env != NULL && detectFaceCallbackMethod && mObj != NULL) {
                LOGD("jni detectFaceCallbackMethod ret : %d", ret);
                //TODO zsf
                _env->CallStaticVoidMethod((jclass) mObj, detectFaceCallbackMethod, ret, top, bottom, left,
                        right);
            }
        });
    }
```
* 5.在java中得到回调的返回数据
```java
public void onFaceDetectedCallback(int ret, int top, int bottom, int left, int right) {
        if (mFaceDetectedCallback != null) {
            //TODO zsf
            mFaceDetectedCallback.onFaceDetected(ret, top, bottom, left, right);
        }
    }

    //TODO: add fact rect points through the params in the callback
    //TODO zsf
    public static void nativeOnFaceDetectedCallback(int ret, int top, int bottom, int left, int right) {
        Log.d("FaceDetectHelper", "JAVA detectFaceCallbackMethod ret : " + ret);
        if (mHelper != null) {
            //TODO zsf
            mHelper.onFaceDetectedCallback(ret, top, bottom, left, right);
        }
    }
```

* 6.更改java端回调函数的类型
```java
public interface OnFaceDetectedCallback {
        //TODO zsf
        void onFaceDetected(int ret, int top, int bottom, int left, int right);
    }
```

* 7.在布局文件中加入ImageView及自定义的DrawImageView.
```xml
    <ImageView
        android:id="@+id/icon"
        android:layout_width="60dp"
        android:layout_height="60dp"
        android:layout_gravity="right|bottom"/>
    <com.bytedance.ies.camerarecorddemoapp.DrawImageView
        android:id="@+id/paint_iv"
        android:layout_width="match_parent"
        android:layout_height="match_parent"
        android:layout_marginStart="1dp"
        android:layout_centerVertical="true" />
```

* 8.在Activity的回调中获得数据，根据返回的动作值，在右下角设置不同的icon.
```java
FaceDetectHelper.getHelper().setFaceDetectedCallback(new FaceDetectHelper.OnFaceDetectedCallback() {
            @Override
            public void onFaceDetected(final int ret, final int top, final int bottom, final int left,
                                       final int right) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        //TODO:
                        // 1 将人脸表情等通过ICON展示在UI上面
                        // 2 增加人脸位置返回值之后，通过方框的图在UI上面现实人脸区域
//                        tv.setText(ret + " " + top + " " + bottom + " " + " " + left + " " + right);
                        icon.setVisibility(View.VISIBLE);
                        switch(ret){
                            case 2:icon.setImageResource(R.drawable.zhayan);break;
                            case 4:icon.setImageResource(R.drawable.zhangzui);break;
                            case 8:icon.setImageResource(R.drawable.yaotou);break;
                            case 16:icon.setImageResource(R.drawable.diantou);break;
                            case 32:icon.setImageResource(R.drawable.meimaotiaodong);break;
                            case 64:icon.setImageResource(R.drawable.zuibadudu);break;
                            default:icon.setVisibility(View.INVISIBLE);break;
                        }
//                        System.out.println("Original");
//                        System.out.println("zsf" + "_top" + Integer.toString(top));
//                        System.out.println("zsf" + "_bottom" + Integer.toString(bottom));
//                        System.out.println("zsf" + "_left" + Integer.toString(left));
//                        System.out.println("zsf" + "_right" + Integer.toString(right));
                        div.set_top(top);
                        div.set_bottom(bottom);
                        div.set_left(left);
                        div.set_right(right);
                        div.invalidate();
                    }
                });
            }
        });
```

* 9.根据返回的矩形值，进行坐标变换后绘制矩形。
```java
@Override protected void onDraw(Canvas canvas)
    {
        super.onDraw(canvas);
        int height = getHeight();
        int width = getWidth();
        //left, top, right, bottom
        canvas.drawRect(new Rect((int) (width-bottom*1.5), (int) (height-left*1.5), (int) (width-top*1.5), (int) (height-right*1.5)),paint);
        //绘制矩形，并设置矩形框显示的位置
    }
```
