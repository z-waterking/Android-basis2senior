package com.bytedance.ies.camerarecorddemoapp.gl;

import android.graphics.SurfaceTexture;
import android.opengl.EGL14;
import android.opengl.EGLConfig;
import android.opengl.EGLContext;
import android.opengl.EGLDisplay;
import android.opengl.EGLSurface;
import android.os.Build;
import android.util.Log;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

/**
 * EGL的绘图的一般步骤：
 * <p>
 * 1，获取EGLDisplay对象
 * 2，初始化与EGLDisplay 之间的连接。
 * 3，获取EGLConfig对象
 * 4，创建EGLContext 实例
 * 5，创建EGLSurface实例
 * <p>
 * 6，连接EGLContext和EGLSurface.
 * 7，使用GL指令绘制图形:GLDrawer2D.draw 在encoder中调用绘制
 * <p>
 * 8，断开并释放与EGLSurface关联的EGLContext对象
 * 9，删除EGLSurface对象
 * 10，删除EGLContext对象
 * 11，终止与EGLDisplay之间的连接。
 * <p>
 * 每次绘制一帧都需要6->7过程吗，7绘制完就通知编码器输出数据，否则下次绘制会把上次绘制的数据冲掉
 */
public class MEgl {
    private static final String TAG = "Steven-MEgl";
    private static final int EGL_RECORDABLE_ANDROID = 0x3142;
    private EGLDisplay mEglDisplay = EGL14.EGL_NO_DISPLAY;
    private EGLContext mEglContext = EGL14.EGL_NO_CONTEXT;
    private EGLConfig mEglConfig;
    private EGLSurface mEGLSurface;
    private EGLContext mDefaultContext = EGL14.EGL_NO_CONTEXT;

    public void init(EGLContext eglContext, final boolean isDepthBuffer, final boolean isRecordable, Object surface) {
        Log.v(TAG, "init:");
        if (mEglDisplay != EGL14.EGL_NO_DISPLAY) {
            throw new RuntimeException("EGL already set up");
        }
        //1，获取EGLDisplay对象
        mEglDisplay = EGL14.eglGetDisplay(EGL14.EGL_DEFAULT_DISPLAY);
        if (mEglDisplay == EGL14.EGL_NO_DISPLAY) {
            Log.e(TAG, "eglGetDisplay failed");
        }

        final int[] version = new int[2];
        // 2，初始化与EGLDisplay 之间的连接。
        if (!EGL14.eglInitialize(mEglDisplay, version, 0, version, 1)) {
            mEglDisplay = null;
            Log.e(TAG, "eglInitialize failed");
        }

        eglContext = eglContext != null ? eglContext : EGL14.EGL_NO_CONTEXT;

        if (mEglContext == EGL14.EGL_NO_CONTEXT) {
            //3，获取EGLConfig对象
            mEglConfig = getConfig(isDepthBuffer, isRecordable);
            if (mEglConfig == null) {
                Log.e(TAG, "chooseConfig failed");
            }
            //  4，创建EGLContext 实例
            mEglContext = createContext(eglContext);
        }
        // confirm whether the EGL rendering context is successfully created
        final int[] values = new int[1];
        EGL14.eglQueryContext(mEglDisplay, mEglContext, EGL14.EGL_CONTEXT_CLIENT_VERSION, values, 0);
        Log.d(TAG, "EGLContext created, client version " + values[0]);
        makeDefault();  // makeCurrent(EGL14.EGL_NO_SURFACE);

        if (!(surface instanceof SurfaceView) && !(surface instanceof Surface) && !(surface instanceof SurfaceHolder) && !(surface instanceof SurfaceTexture)) {
            Log.e(TAG, "unsupported surface");
        }
        //5，创建EGLSurface实例
        mEGLSurface = createWindowSurface(surface);
        makeCurrent();
    }

    /**
     * 6，连接EGLContext和EGLSurface.
     *
     * @return
     */
    public boolean makeCurrent() {
//      if (DEBUG) Log.v(TAG, "makeCurrent:");
        if (mEglDisplay == null) {
            Log.d(TAG, "makeCurrent:eglDisplay not initialized");
        }
        if (mEGLSurface == null || mEGLSurface == EGL14.EGL_NO_SURFACE) {
            final int error = EGL14.eglGetError();
            if (error == EGL14.EGL_BAD_NATIVE_WINDOW) {
                Log.e(TAG, "makeCurrent:returned EGL_BAD_NATIVE_WINDOW.");
            }
            return false;
        }
        // attach EGL renderring context to specific EGL window surface
        if (!EGL14.eglMakeCurrent(mEglDisplay, mEGLSurface, mEGLSurface, mEglContext)) {
            Log.w(TAG, "eglMakeCurrent:" + EGL14.eglGetError());
            return false;
        }
        return true;
    }

    private EGLSurface createWindowSurface(final Object nativeWindow) {
        Log.v(TAG, "createWindowSurface:nativeWindow=" + nativeWindow);
        final int[] surfaceAttribs = {EGL14.EGL_NONE};
        EGLSurface result = null;
        try {
            result = EGL14.eglCreateWindowSurface(mEglDisplay, mEglConfig, nativeWindow, surfaceAttribs, 0);
        } catch (final IllegalArgumentException e) {
            Log.e(TAG, "eglCreateWindowSurface", e);
        }
        return result;
    }

    //绘制完毕后使用eglSwapBuffers()交换前后缓冲，用户即看到在后缓冲中的内容
    public int swapBuffers() {
        if (!EGL14.eglSwapBuffers(mEglDisplay, mEGLSurface)) {
            final int err = EGL14.eglGetError();
            Log.w(TAG, "swap:err=" + err);
            return err;
        }
        return EGL14.EGL_SUCCESS;
    }

    private void makeDefault() {
        Log.v(TAG, "makeDefault:");
        if (!EGL14.eglMakeCurrent(mEglDisplay, EGL14.EGL_NO_SURFACE, EGL14.EGL_NO_SURFACE, EGL14.EGL_NO_CONTEXT)) {
            Log.w("TAG", "makeDefault" + EGL14.eglGetError());
        }
    }

    private EGLContext createContext(final EGLContext shared_context) {
//      if (DEBUG) Log.v(TAG, "createContext:");

        final int[] attrib_list = {
                EGL14.EGL_CONTEXT_CLIENT_VERSION, 2,
                EGL14.EGL_NONE
        };
        final EGLContext context = EGL14.eglCreateContext(mEglDisplay, mEglConfig, shared_context, attrib_list, 0);
        checkEglError("eglCreateContext");
        return context;
    }

    private EGLConfig getConfig(final boolean with_depth_buffer, final boolean isRecordable) {
        final int[] attribList = {
                EGL14.EGL_RENDERABLE_TYPE, EGL14.EGL_OPENGL_ES2_BIT,
                EGL14.EGL_RED_SIZE, 8,//指定RGB中R的大小
                EGL14.EGL_GREEN_SIZE, 8,//指定G大小
                EGL14.EGL_BLUE_SIZE, 8,//指定B大小
                EGL14.EGL_ALPHA_SIZE, 8,//指定Alpha大小
                EGL14.EGL_NONE, EGL14.EGL_NONE, //EGL14.EGL_STENCIL_SIZE, 8,
                EGL14.EGL_NONE, EGL14.EGL_NONE, //EGL_RECORDABLE_ANDROID, 1,    // this flag need to recording of MediaCodec
                EGL14.EGL_NONE, EGL14.EGL_NONE, //  with_depth_buffer ? EGL14.EGL_DEPTH_SIZE : EGL14.EGL_NONE,
                // with_depth_buffer ? 16 : 0,
                EGL14.EGL_NONE
        };
        int offset = 10;
        if (false) {
            attribList[offset++] = EGL14.EGL_STENCIL_SIZE;
            attribList[offset++] = 8;
        }
        if (with_depth_buffer) {
            attribList[offset++] = EGL14.EGL_DEPTH_SIZE;
            attribList[offset++] = 16;
        }
        if (isRecordable && (Build.VERSION.SDK_INT >= 18)) {
            attribList[offset++] = EGL_RECORDABLE_ANDROID;
            attribList[offset++] = 1;
        }
        for (int i = attribList.length - 1; i >= offset; i--) {
            attribList[i] = EGL14.EGL_NONE;
        }
        final EGLConfig[] configs = new EGLConfig[1];
        final int[] numConfigs = new int[1];
        if (!EGL14.eglChooseConfig(mEglDisplay, attribList, 0, configs, 0, configs.length, numConfigs, 0)) {
            // XXX it will be better to fallback to RGB565
            Log.w(TAG, "unable to find RGBA8888 / " + " EGLConfig");
            return null;
        }
        return configs[0];
    }

    private void checkEglError(final String msg) {
        int error;
        if ((error = EGL14.eglGetError()) != EGL14.EGL_SUCCESS) {
            throw new RuntimeException(msg + ": EGL error: 0x" + Integer.toHexString(error));
        }
    }

    public void release() {
        Log.v(TAG, "release:");
        if (mEglDisplay != EGL14.EGL_NO_DISPLAY) {
            destroyContext();
            EGL14.eglTerminate(mEglDisplay);
            EGL14.eglReleaseThread();
        }
        mEglDisplay = EGL14.EGL_NO_DISPLAY;
        mEglContext = EGL14.EGL_NO_CONTEXT;
    }

    private void destroyContext() {
        Log.v(TAG, "destroyContext:");

        if (!EGL14.eglDestroyContext(mEglDisplay, mEglContext)) {
            Log.e("destroyContext", "display:" + mEglDisplay + " context: " + mEglContext);
            Log.e(TAG, "eglDestroyContex:" + EGL14.eglGetError());
        }
        mEglContext = EGL14.EGL_NO_CONTEXT;
        if (mDefaultContext != EGL14.EGL_NO_CONTEXT) {
            if (!EGL14.eglDestroyContext(mEglDisplay, mDefaultContext)) {
                Log.e("destroyContext", "display:" + mEglDisplay + " context: " + mDefaultContext);
                Log.e(TAG, "eglDestroyContex:" + EGL14.eglGetError());
            }
            mDefaultContext = EGL14.EGL_NO_CONTEXT;
        }
    }
}
