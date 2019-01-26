package com.bytedance.ies.camerarecorddemoapp.gl;

import android.media.MediaCodec;
import android.media.MediaCodecInfo;
import android.media.MediaFormat;
import android.opengl.EGLContext;
import android.util.Log;
import android.view.Surface;

import com.bytedance.ies.camerarecorddemoapp.ThreadPool;

import java.io.IOException;


/**
 * 视频Surface编码器
 */

public class VideoSurfaceEncoder extends VideoEncoder {
    public static final int SURFACE_COLOR_FORMAT = MediaCodecInfo.CodecCapabilities.COLOR_FormatSurface;
    private Surface mSurface;
    private SurfaceEncoderRenderer mRenderer;

    private static int getSurfaceColorFormat() {
        return SURFACE_COLOR_FORMAT;
    }

    public VideoSurfaceEncoder(MMuxer muxer, int width, int height) {
        super(muxer);
        mRenderer = new SurfaceEncoderRenderer();
        mWidth = width;
        mHeight = height;
        TAG = "Steven-VideoSurfaceEncoder";
    }

    @Override
    public Object call() throws Exception {
        while (!mIsInit) {
            init();
        }
        while (!mIsInit) {
            return null;
        }
        while (mIsRecording) {
            try {
                mLock.lock();
                if (isEos()) {
                    //停止编码器
                    mMediaCodec.signalEndOfInputStream();//signalEndOfInputStream只对surface录制有效
                    Log.d(TAG, "singal eos");
                    output(true);
                    break;
                }
                mOutputCondition.await();//进入await状态
                output(false);
            } finally {
                mLock.unlock();
            }
        }
        return null;
    }

    @Override
    public void output(boolean isEos) {
        if (isAllKeyFrame()) {
            requestKeyFrame();
        }
        super.output(isEos);
    }

    @Override
    public void prepare() {
        Log.i(TAG, "prepare: ");
        mTrackIndex = -1;
        final MediaCodecInfo videoCodecInfo = selectCodec(MIME_TYPE);
        if (videoCodecInfo == null) {
            Log.e(TAG, "Unable to find an appropriate codec for " + MIME_TYPE);
            return;
        }
        final MediaFormat format = MediaFormat.createVideoFormat(MIME_TYPE, mWidth, mHeight);
        format.setInteger(MediaFormat.KEY_COLOR_FORMAT, MediaCodecInfo.CodecCapabilities.COLOR_FormatSurface);  // API >= 18
        format.setInteger(MediaFormat.KEY_BIT_RATE, BIT_RATE);//设置码率
        format.setInteger(MediaFormat.KEY_FRAME_RATE, FRAME_RATE);//设置帧率
        if (!mIsAllKeyFrame) {
            format.setInteger(MediaFormat.KEY_I_FRAME_INTERVAL, IFRAME_INTERVAL);
        } else {
            format.setInteger(MediaFormat.KEY_I_FRAME_INTERVAL, 0);//设置全关键帧
        }
        try {
            mMediaCodec = MediaCodec.createEncoderByType(MIME_TYPE);
            mMediaCodec.configure(format, null, null, MediaCodec.CONFIGURE_FLAG_ENCODE);
            // get Surface for encoder input
            // this method only can call between #configure and #start
            mSurface = mMediaCodec.createInputSurface();    // API >= 18
            mMediaCodec.start();
            mRenderer.setSurface(mSurface);
            mRenderer.start();
            Log.i(TAG, "prepare finishing");
        } catch (IOException e) {
            Log.e(TAG, "" + e);
            e.printStackTrace();
        }
    }

    public boolean isPrepared() {
        return mIsInit && mRenderer.isInitGL();
    }

    public SurfaceEncoderRenderer getRenderer() {
        return mRenderer;
    }

    @Override
    public void release() {
        super.release();
        if (mSurface != null) {
            mSurface.release();
        }
        if (mRenderer != null) {
            mRenderer.release();
        }
    }

    public void setEglAndStart(EGLContext eglContext, int textureId) {
        mRenderer.setEglContext(eglContext, textureId, this);
        ThreadPool.getInstance().run(this);
    }

    //signal此线程
    public void singalOutput() {
        try {
            mLock.lock();
            mOutputCondition.signal();
        } finally {
            mLock.unlock();
        }

    }

    //egl 绘制
    public void render(float[] surfaceTextureMatrix, float[] mvpMatrix) {
        if (isAllKeyFrame()) {
            requestKeyFrame();
        }
        mRenderer.draw(surfaceTextureMatrix, mvpMatrix);
        if (isAllKeyFrame()) {
            requestKeyFrame();
        }
    }
}
