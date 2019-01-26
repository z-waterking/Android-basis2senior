package com.bytedance.ies.camerarecorddemoapp.gl;

import android.media.MediaCodec;
import android.media.MediaFormat;
import android.media.MediaMuxer;
import android.util.Log;

import java.io.IOException;
import java.nio.ByteBuffer;

/**
 * Muxer实现
 */
public class MMuxer implements IMuxer {

    String TAG = "Steven-" + MMuxer.class.getSimpleName();
    private MediaMuxer mMuxer;
    private int mVideoIndex = -1;
    private boolean mIsStarted = false;
    private boolean mIsVideoTrackAdd = false;
    private int mTrackCount = 0;
    private boolean mIsAudioTrackAdd = false;
    private int mAudioIndex = -1;
    private boolean mVideoEos = false;
    private boolean mAudioEos = false;

    public MMuxer(String path) {
        try {
            mMuxer = new MediaMuxer(path, MediaMuxer.OutputFormat.MUXER_OUTPUT_MPEG_4);
        } catch (IOException e) {
            Log.i(TAG, "init mmuxer error " + e);
        }
    }

    /**
     * 加入媒体轨道
     *
     * @param format
     * @param type
     * @return
     */
    public int addTrack(MediaFormat format, MediaEncoder.MediaType type) {
        mTrackCount++;
        if (type == MediaEncoder.MediaType.VIDEO) {
            mVideoIndex = mMuxer.addTrack(format);
            mIsVideoTrackAdd = true;
            Log.d(TAG, "addTrack: video====");
            return mVideoIndex;
        } else {
            mAudioIndex = mMuxer.addTrack(format);
            mIsAudioTrackAdd = true;
            Log.d(TAG, "addTrack: audio =====");
            return mAudioIndex;
        }
    }

    public boolean isVideoTrackAdd() {
        return mIsVideoTrackAdd;
    }

    @Override
    public void stop() {
        mIsStarted = false;
        mMuxer.stop();
        Log.d(TAG, "muxer stop---");
    }

    @Override
    public void release() {
        stop();
        mMuxer.release();
        Log.d(TAG, "muxer release---");

    }

    /**
     * 写入媒体数据
     *
     * @param trackIndex
     * @param byteBuf
     * @param bufferInfo
     */
    @Override
    public void writeSampleData(int trackIndex, ByteBuffer byteBuf, MediaCodec.BufferInfo bufferInfo) {
        try {
            if (mTrackCount > 0 && mIsStarted) {
                mMuxer.writeSampleData(trackIndex, byteBuf, bufferInfo);
                Log.d(TAG, "wrateSampleData-" + trackIndex + " pts=" + bufferInfo.presentationTimeUs);
            } else {

            }
        } catch (Exception e) {
            Log.e(TAG, "writeSampleData Error=" + e);
        }
    }

    @Override
    public void start() {
        mIsStarted = true;
        mMuxer.start();
        Log.i(TAG, "start_muxer");
    }

    public boolean isStarted() {
        return mIsStarted;
    }

    public boolean isPrepared() {
        return mIsAudioTrackAdd && mIsVideoTrackAdd;
    }

    /**
     * eos非常重要，一定要等到音频和视频都结束录制才能释放
     *
     * @param trackIndex
     */
    public void eos(int trackIndex) {
        if (trackIndex == mVideoIndex) {
            mVideoEos = true;
            Log.d(TAG, "eos video in muxer");
        }
        if (trackIndex == mAudioIndex) {
            mAudioEos = true;
            Log.d(TAG, "eos audio in muxer");
        }
        if (mAudioEos && mVideoEos) {
            release();
        }
    }
}
