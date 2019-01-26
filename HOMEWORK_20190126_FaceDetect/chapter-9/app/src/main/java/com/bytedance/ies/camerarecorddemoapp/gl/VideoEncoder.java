package com.bytedance.ies.camerarecorddemoapp.gl;

import android.annotation.TargetApi;
import android.media.MediaCodec;
import android.os.Bundle;

import com.bytedance.ies.camerarecorddemoapp.Config;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 视频编码器
 */

public abstract class VideoEncoder extends MediaEncoder {
    protected static final String MIME_TYPE = "video/avc";
    protected static final int FRAME_RATE = 30;
    protected static final int BIT_RATE = 4 * 1024 * 1024;
    protected static final int IFRAME_INTERVAL = 1;//1秒
    protected int mWidth = Config.VIDEO_WIDTH;
    protected int mHeight = Config.VIDEO_HEIGHT;
    protected Lock mLock;
    protected Condition mOutputCondition;
    protected boolean mIsAllKeyFrame = false;

    protected VideoEncoder(MMuxer muxer) {
        super(muxer, MediaType.VIDEO);
        TAG = "Steven-VideoEncoder";
    }

    @Override
    protected void init() {
        mLock = new ReentrantLock();
        mOutputCondition = mLock.newCondition();
        super.init();
    }

    @TargetApi(19)
    public void setAllKeyFrame(boolean allKeyFrame) {
        mIsAllKeyFrame = allKeyFrame;
    }

    public boolean isAllKeyFrame() {
        return mIsAllKeyFrame;
    }

    @Override
    public void input(Frame frame) {
    }

    @Override
    protected boolean isEos() {
        return mIsEos;
    }

    @Override
    public void eos() {
        mIsEos = true;
    }

    @TargetApi(19)
    protected void requestKeyFrame() {
        if (isRecording()) {
            try {
                Bundle reqKeyCmd = new Bundle();
                reqKeyCmd.putInt(MediaCodec.PARAMETER_KEY_REQUEST_SYNC_FRAME, 0);
                mMediaCodec.setParameters(reqKeyCmd);
                //Log.v("meeee", " reqi");
            } catch (Exception e) {
            }
        }
    }

}
