package com.bytedance.ies.camerarecorddemoapp;

import android.text.TextUtils;
import android.util.Log;

public class FaceDetectHelper {

    // Used to load the 'native-lib' library on application startup.
    static {
        System.loadLibrary("native-lib");
    }

    private static FaceDetectHelper mHelper = null;
    private OnFaceDetectedCallback mFaceDetectedCallback = null;

    private FaceDetectHelper() {
        nativeInit();
    }

    public static FaceDetectHelper getHelper() {
        synchronized (FaceDetectHelper.class) {
            if (mHelper == null) {
                synchronized (FaceDetectHelper.class) {
                    if (mHelper == null) {
                        mHelper = new FaceDetectHelper();
                    }
                }
            }
        }
        return mHelper;
    }

    public void destroy() {
        nativeUnInit();
        mHelper = null;
    }

    public void setFaceDetectModelPath(String modelPath) {
        if (!TextUtils.isEmpty(modelPath)) {
            nativeSetFaceDetectModelPath(modelPath);
        }
    }

    public void setLicense(String license) {
        nativeSetLicense(license);
    }

    public void setFaceDetectedCallback(OnFaceDetectedCallback callback) {
        mFaceDetectedCallback = callback;
    }

    public void writeBMP() {
        nativeTestWriteBmp();
    }

    public interface OnFaceDetectedCallback {
        //TODO zsf
        void onFaceDetected(int ret, int top, int bottom, int left, int right);
    }

    public void detectFace(byte[] image, int pixelFormat, int width, int height, int stride) {
        nativeDetectFace(image, pixelFormat, width, height, stride);
    }

    public void onFaceDetectedCallback(int ret, int top, int bottom, int left, int right) {
        if (mFaceDetectedCallback != null) {
            //TODO zsf4:得到回调返回的数据
            mFaceDetectedCallback.onFaceDetected(ret, top, bottom, left, right);
        }
    }

    //TODO: add fact rect points through the params in the callback
    //TODO zsf
    public static void nativeOnFaceDetectedCallback(int ret, int top, int bottom, int left, int right) {
        Log.d("FaceDetectHelper", "JAVA detectFaceCallbackMethod ret : " + ret);
        if (mHelper != null) {
            //TODO zsf3:得到后端得到的数据
            mHelper.onFaceDetectedCallback(ret, top, bottom, left, right);
        }
    }

    private native void nativeSetLicense(String license);

    private native void nativeSetFaceDetectModelPath(String effectModePath);

    private native void nativeInit();

    private native void nativeUnInit();

    private native void nativeDetectFace(byte[] image, int pixelFormat, int width, int height, int stride);

    private native void nativeTestWriteBmp();
}
