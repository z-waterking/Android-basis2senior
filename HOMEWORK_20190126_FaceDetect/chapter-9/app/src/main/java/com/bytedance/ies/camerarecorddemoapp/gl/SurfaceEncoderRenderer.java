package com.bytedance.ies.camerarecorddemoapp.gl;

import android.graphics.SurfaceTexture;
import android.opengl.EGLContext;
import android.opengl.GLES20;
import android.opengl.Matrix;
import android.util.Log;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.bytedance.ies.camerarecorddemoapp.ThreadPool;

import java.util.concurrent.Callable;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 编码器渲染器
 */
public class SurfaceEncoderRenderer implements Callable {
    private String TAG = "Steven-" + SurfaceEncoderRenderer.class.getSimpleName();
    private Condition mDrawCondition;
    private Lock mLock;
    private EGLContext mEglContext;
    private int mTextureId;
    private float[] mMatrix = new float[32];
    private Object mSurface;
    private VideoSurfaceEncoder mEncoder;
    private MEgl mEgl;
    private GLDrawer2D mDrawer;
    private boolean mIsInitGL = false;
    private boolean mHadSetEglContext = false;

    public SurfaceEncoderRenderer() {
        mLock = new ReentrantLock();
        mDrawCondition = mLock.newCondition();
    }

    @Override
    public Object call() throws Exception {
        Log.i(TAG, "encoder render call-------");
        while (!mIsInitGL) {
            initGL();
        }
        while (!mIsInitGL) {
            return null;
        }
        while (mEncoder.isRecording()) {
            mLock.lock();
            try {
                Log.d(TAG, "await~~~~");
                mDrawCondition.await();
                mEgl.makeCurrent();
                //makeCurrent表明opengl的操作是在egl环境下
                // clear screen with yellow color so that you can see rendering rectangle
                GLES20.glClearColor(1.0f, 1.0f, 0.0f, 1.0f);
                GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);
                mDrawer.setMatrix(mMatrix, 16);
                mDrawer.draw(mTextureId, mMatrix);
                mEgl.swapBuffers();
                mEncoder.singalOutput();//通知编码器线程要输出数据啦
                Log.d(TAG, "draw------------textureId=" + mTextureId);
            } finally {
                mLock.unlock();
            }

        }
        Log.d(TAG, "call: recording finish");
        return null;
    }

    //初始化egl及其opengl
    private void initGL() {
        mEgl = new MEgl();
        mEgl.init(mEglContext, false, true, mSurface);
        mEgl.makeCurrent();//drawer必须要在egl.makeCurrent()后初始化，才能保证mDrawer渲染的是egl对应的surface
        mDrawer = new GLDrawer2D();
        mIsInitGL = true;
        Log.d(TAG, "-----init egl opengl -------------");
    }

    public boolean isInitGL() {
        return mIsInitGL;
    }

    private void releaseGL() {
        Log.i(TAG, "internalRelease:");
        if (mDrawer != null) {
            mDrawer.release();
            mDrawer = null;
        }
        if (mEgl != null) {
            mEgl.release();
            mEgl = null;
        }
    }

    public void release() {
        releaseGL();
    }

    public final void setEglContext(final EGLContext eglContext, final int textureId, VideoSurfaceEncoder encoder) {
        mEncoder = encoder;
        mEglContext = eglContext;
        mTextureId = textureId;
        Matrix.setIdentityM(mMatrix, 0);
        Matrix.setIdentityM(mMatrix, 16);
        Log.i(TAG, "setEglContext--------------");
    }

    public final void draw(final float[] textureMatrix, final float[] mvpMatrix) {
        try {
            mLock.lock();
            if ((textureMatrix != null) && (textureMatrix.length >= 16)) {
                System.arraycopy(textureMatrix, 0, mMatrix, 0, 16);
            } else {
                Matrix.setIdentityM(mMatrix, 0);
            }
            if ((mvpMatrix != null) && (mvpMatrix.length >= 16)) {
                System.arraycopy(mvpMatrix, 0, mMatrix, 16, 16);
            } else {
                Matrix.setIdentityM(mMatrix, 16);
            }
            Log.d(TAG, "signal~~~~~");
            mDrawCondition.signal();//通知绘制
        } finally {
            mLock.unlock();
        }

    }

    public void setSurface(Surface surface) {
        mSurface = surface;
    }

    public void start() {
        if (!(mSurface instanceof SurfaceView) && !(mSurface instanceof Surface) && !(mSurface instanceof SurfaceHolder) && !(mSurface instanceof SurfaceTexture)) {
            Log.e(TAG, "unsupported surface");
        } else {
            ThreadPool.getInstance().run(this);
        }
    }
}
