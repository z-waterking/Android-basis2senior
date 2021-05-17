package com.bytedance.ies.camerarecorddemoapp.gl;

import android.graphics.SurfaceTexture;
import android.opengl.EGL14;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;
import android.os.Environment;
import android.util.Log;

import com.bytedance.ies.camerarecorddemoapp.Config;

import java.io.File;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * 摄像头预览opengl渲染器
 */
public class SurfaceRenderer implements GLSurfaceView.Renderer, SurfaceTexture.OnFrameAvailableListener {
    private static String TAG = "Steven-" + SurfaceRenderer.class.getSimpleName();
    private CameraGLSurfaceView mSurfaceView;//GLSurfaceView
    private SurfaceTexture mSurfaceTexture;//渲染纹理
    private int mTextureId;
    private GLDrawer2D mDrawer;//OpenGL绘制
    private float[] mSurfaceTextureMatrix = new float[16];//纹理变换矩阵
    //投影变换矩阵（注意，opengl坐标系和手机屏幕坐标系不同，为了正常显示，opengl坐标需要左乘投影变换矩阵左）
    private float[] mMvpMatrix = new float[16];
    private boolean mIsNeedUpdateTexture = false;
    private boolean mIsNeedRecord = false;
    private VideoSurfaceEncoder mVideoEncoder;//视频编码器
    private boolean mIsRecordCurrFrame = true;
    private boolean mIsStopRecorder = false;
    private AudioEncoder mAudioEncoder;//音频编码器

    public SurfaceRenderer(CameraGLSurfaceView surfaceView) {
        mSurfaceView = surfaceView;
        Matrix.setIdentityM(mMvpMatrix, 0);
    }

    /**
     * Renderer
     *
     * @param gl
     * @param config
     */
    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        Log.v(TAG, "onSurfaceCreated:");
        // 摄像头渲染需要 OES_EGL_image_external extension
        final String extensions = GLES20.glGetString(GLES20.GL_EXTENSIONS); // API >= 8
        if (!extensions.contains("OES_EGL_image_external"))
            throw new RuntimeException("This system does not support OES_EGL_image_external.");
        // 创建纹理ID
        mTextureId = GLDrawer2D.initTextureId();
        // 创建渲染纹理
        mSurfaceTexture = new SurfaceTexture(mTextureId);
        mSurfaceTexture.setOnFrameAvailableListener(this);
        // 黄色清屏
        GLES20.glClearColor(1.0f, 1.0f, 0.0f, 1.0f);
        mDrawer = new GLDrawer2D();
        mDrawer.setMatrix(mMvpMatrix, 0);
    }

    public boolean isNeedRecord() {
        return mIsNeedRecord;
    }

    public void setNeedRecord(boolean isNeedRecord) {
        mIsNeedRecord = isNeedRecord;
    }

    /**
     * Renderer
     *
     * @param gl
     * @param width
     * @param height
     */
    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        Log.v(TAG, String.format("onSurfaceChanged:(%d,%d)", width, height));
        // if at least with or height is zero, initialization of this view is still progress.
        if ((width == 0) || (height == 0)) return;
        updateViewport();
        mSurfaceView.startPreview(width, height);
    }

    /**
     * opengl绘制函数
     *
     * @param gl
     */
    @Override
    public void onDrawFrame(GL10 gl) {
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);
        if (mIsNeedUpdateTexture) {
            mIsNeedUpdateTexture = false;
            //更新纹理（摄像头已经绑定该SurfaceTexture）
            mSurfaceTexture.updateTexImage();
            // 获取纹理变换矩阵
            mSurfaceTexture.getTransformMatrix(mSurfaceTextureMatrix);
        }
//        // draw to preview screen
        if (mIsNeedRecord) {
            if (mVideoEncoder == null) {
                MMuxer mMuxer = new MMuxer(Config.getSavePath()/*getSaveVideoPath()*/);
                mVideoEncoder = new VideoSurfaceEncoder(mMuxer, mSurfaceView.getVideoWidth(), mSurfaceView.getVideoHeight());
                mAudioEncoder = new AudioEncoder(mMuxer);
                mVideoEncoder.setAllKeyFrame(true);
                mVideoEncoder.setEglAndStart(EGL14.eglGetCurrentContext(), mTextureId);
                mAudioEncoder.start();
                Log.d(TAG, "init encoder");
            }
//            Log.d(TAG, "encoderprepared=" + mEncoder.isPrepared() + " isRecordCurrFrame=" + mIsRecordCurrFrame);
            if (mVideoEncoder != null && mVideoEncoder.isPrepared() && mIsRecordCurrFrame) {
                long curr = System.currentTimeMillis();
                Log.d(TAG, "======drawTime========" + (curr - mDrawTime));
                mDrawTime = curr;
                mVideoEncoder.render(mSurfaceTextureMatrix, mMvpMatrix);
            }
            mIsRecordCurrFrame = !mIsRecordCurrFrame;
            if (mIsStopRecorder) {
                mVideoEncoder.eos();
                mAudioEncoder.eos();
                mIsNeedRecord = false;
                mVideoEncoder = null;
            }
        }
        mDrawer.draw(mTextureId, mSurfaceTextureMatrix);
    }

    public long mDrawTime = 0;

    public void setStopRecorder(boolean stopRecorder) {
        mIsStopRecorder = stopRecorder;
    }

    private String getSaveVideoPath() {
        File dir = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "00recorder" + File.separator);
        if (!dir.exists() || !dir.isDirectory()) {
            dir.mkdirs();
        }
        File file = new File(dir, "surface.mp4");
        return file.getAbsolutePath();
    }

    public void onSurfaceDestroy() {
        Log.v(TAG, "onSurfaceDestroyed:");
        if (mDrawer != null) {
            mDrawer.release();
            mDrawer = null;
        }
        if (mSurfaceTexture != null) {
            mSurfaceTexture.release();
            mSurfaceTexture = null;
        }
        GLDrawer2D.deleteTex(mTextureId);
    }


    /**
     * OnFrameAvailableListener
     *
     * @param surfaceTexture
     */
    @Override
    public void onFrameAvailable(SurfaceTexture surfaceTexture) {
        mIsNeedUpdateTexture = true;
    }

    public SurfaceTexture getSurfaceTexture() {
        return mSurfaceTexture;
    }

    /**
     * 获取视窗，即需要从opengl的画布中截出一片区域用于显示内容
     */
    public void updateViewport() {
        final int view_width = mSurfaceView.getWidth();
        final int view_height = mSurfaceView.getHeight();
        GLES20.glViewport(0, 0, view_width, view_height);
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);
        final double video_width = mSurfaceView.getVideoWidth();
        final double video_height = mSurfaceView.getVideoHeight();
        if (video_width == 0 || video_height == 0) return;
        Matrix.setIdentityM(mMvpMatrix, 0);
        final double view_aspect = view_width / (double) view_height;
        Log.i(TAG, String.format("view(%d,%d)%f,video(%1.0f,%1.0f)", view_width, view_height, view_aspect, video_width, video_height));
        if (mDrawer != null)
            mDrawer.setMatrix(mMvpMatrix, 0);
    }
}
