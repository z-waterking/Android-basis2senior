package com.bytedance.ies.camerarecorddemoapp.gl;

import android.media.MediaCodec;
import android.media.MediaCodecInfo;
import android.media.MediaCodecList;
import android.media.MediaFormat;
import android.util.Log;

import java.nio.ByteBuffer;
import java.util.concurrent.Callable;

/**
 * 编码器基类
 */
public abstract class MediaEncoder implements Callable, IEncoder {
    protected String TAG = "Steven-MediaEncoder";
    protected static final long TIMEOUT_USEC = 10000;
    protected MMuxer mMuxer;
    protected MediaCodec mMediaCodec;
    protected boolean mIsRecording = false;
    protected boolean mIsInit = false;
    protected MediaType mMediaType;
    protected boolean mIsEos = false;


    protected MediaCodec.BufferInfo mBufferInfo;
    protected MediaFormat mMediaFormat;
    protected int mTrackIndex;

    protected MediaEncoder(MMuxer muxer, MediaType type) {
        mMuxer = muxer;
        mMediaType = type;
    }

    public boolean isRecording() {
        return mIsRecording;
    }

    public void setRecording(boolean recording) {
        mIsRecording = recording;
    }

    protected abstract boolean isEos();

    protected void init() {
        mBufferInfo = new MediaCodec.BufferInfo();
        prepare();
        mIsInit = true;
        setRecording(true);
    }

    @Override
    public void output(boolean isEos) {
        String tag = TAG + "-output";
        ByteBuffer[] outputBuffers = null;
        int count = 0;
        int outputIndex = mMediaCodec.dequeueOutputBuffer(mBufferInfo, TIMEOUT_USEC);
        try {
            outputBuffers = mMediaCodec.getOutputBuffers();
            do {
                if (outputIndex == MediaCodec.INFO_TRY_AGAIN_LATER) {
                    Log.i(tag, "output from encoder not available");
                    if (!isEos) {
                        count++;
                        if (count >= 5) {
                            Log.i(tag, "output from encoder not available and break===========");
                            break;
                        }
                    }
                } else if (outputIndex == MediaCodec.INFO_OUTPUT_BUFFERS_CHANGED) {
                    outputBuffers = mMediaCodec.getOutputBuffers();
                    Log.i(tag, "encoder output buffers changed");
                } else if (outputIndex == MediaCodec.INFO_OUTPUT_FORMAT_CHANGED) {
                    //在音视频混合器中加入视频/音频轨道
                    addTrack();
                    Log.i(tag, "encoder output format change");
                } else if (outputIndex < 0) {
                    Log.e(tag, "output buffer wrong " + outputIndex);
                } else {
                    ByteBuffer outputBuffer = outputBuffers[outputIndex];
                    if (outputBuffer == null) {
                        Log.e(tag, "output buffer null");
                        return;
                    }
                    if ((mBufferInfo.flags & MediaCodec.BUFFER_FLAG_CODEC_CONFIG) != 0) {
                        // The codec config data was pulled out and fed to the muxer when we got
                        // the INFO_OUTPUT_FORMAT_CHANGED status.  Ignore it.
                        mBufferInfo.size = 0;
                    }
                    Log.d(tag, "buffer size=" + mBufferInfo.size + " pts=" + mBufferInfo.presentationTimeUs);
                    if (mBufferInfo.size != 0) {
                        if (!mMuxer.isVideoTrackAdd()) {
                            addTrack();
                        }
                        if (!mMuxer.isStarted() && mMuxer.isPrepared()) {
                            mMuxer.start();
                        }
                        if (mMuxer.isStarted()) {
                            outputBuffer.position(mBufferInfo.offset);
                            outputBuffer.limit(mBufferInfo.offset + mBufferInfo.size);
                            //写入指定轨道的数据
                            mMuxer.writeSampleData(mTrackIndex, outputBuffer, mBufferInfo);
                        }
                    }
                    mMediaCodec.releaseOutputBuffer(outputIndex, false);
                    if ((mBufferInfo.flags & MediaCodec.BUFFER_FLAG_END_OF_STREAM) != 0) {
                        // 停止编码器
                        Log.d(tag, "output: eos coming");
                        mIsRecording = false;
                        release();
                        break;      // out of while
                    }
                }
                outputIndex = mMediaCodec.dequeueOutputBuffer(mBufferInfo, TIMEOUT_USEC);

            } while (outputIndex >= 0);
        } catch (Exception e) {
        }
    }

    //加入轨道
    protected void addTrack() {
        mMediaFormat = mMediaCodec.getOutputFormat();
        mTrackIndex = mMuxer.addTrack(mMediaFormat, mMediaType);
    }

    @Override
    public void release() {
        if (!mIsRecording) {
            mMuxer.eos(mTrackIndex);
            mMediaCodec.release();
        }
    }

    protected MediaCodecInfo selectCodec(String mimeType) {
        int numCodecs = MediaCodecList.getCodecCount();
        for (int i = 0; i < numCodecs; i++) {
            MediaCodecInfo codecInfo = MediaCodecList.getCodecInfoAt(i);
            if (!codecInfo.isEncoder()) {
                continue;
            }
            String[] types = codecInfo.getSupportedTypes();
            for (int j = 0; j < types.length; j++) {
                if (types[j].equalsIgnoreCase(mimeType)) {
                    return codecInfo;
                }
            }
        }
        return null;
    }

    public enum MediaType {
        VIDEO,
        AUDIO
    }
}
