package com.bytedance.ies.camerarecorddemoapp.gl;

import android.media.AudioFormat;
import android.media.MediaCodec;
import android.media.MediaCodecInfo;
import android.media.MediaFormat;
import android.util.Log;

import com.bytedance.ies.camerarecorddemoapp.ThreadPool;

import java.io.IOException;
import java.nio.ByteBuffer;

public class AudioEncoder extends MediaEncoder {

    private static final String MIME_TYPE = "audio/mp4a-latm";
    private static final int SAMPLE_RATE = 44100;   // 44.1[KHz] is only setting guaranteed to be available on all devices.
    private static final int BIT_RATE = 64000;
    private AudioThread mAudioThread;

    public AudioEncoder(MMuxer muxer) {
        super(muxer, MediaType.AUDIO);
        TAG = "Steven-AudioEncoder";
    }

    @Override
    protected boolean isEos() {
        return mIsEos;
    }

    @Override
    protected void init() {
        super.init();

        ThreadPool.getInstance().run(mAudioThread);
    }

    @Override
    public Object call() throws Exception {
        init();
        return null;

    }

    @Override
    public void prepare() {
        Log.v(TAG, "audio encoder prepare:");
        mTrackIndex = -1;
        // prepare MediaCodec for AAC encoding of audio data from inernal mic.
        final MediaCodecInfo audioCodecInfo = selectCodec(MIME_TYPE);
        if (audioCodecInfo == null) {
            Log.e(TAG, "Unable to find an appropriate codec for " + MIME_TYPE);
            return;
        }
        Log.i(TAG, "selected codec: " + audioCodecInfo.getName());

        final MediaFormat audioFormat = MediaFormat.createAudioFormat(MIME_TYPE, SAMPLE_RATE, 1);
        audioFormat.setInteger(MediaFormat.KEY_AAC_PROFILE, MediaCodecInfo.CodecProfileLevel.AACObjectLC);
        audioFormat.setInteger(MediaFormat.KEY_CHANNEL_MASK, AudioFormat.CHANNEL_IN_MONO);
        audioFormat.setInteger(MediaFormat.KEY_BIT_RATE, BIT_RATE);
        audioFormat.setInteger(MediaFormat.KEY_CHANNEL_COUNT, 1);
//      audioFormat.setLong(MediaFormat.KEY_MAX_INPUT_SIZE, inputFile.length());
//      audioFormat.setLong(MediaFormat.KEY_DURATION, (long)durationInMs );
        Log.i(TAG, "format: " + audioFormat);
        try {
            mMediaCodec = MediaCodec.createEncoderByType(MIME_TYPE);
            mMediaCodec.configure(audioFormat, null, null, MediaCodec.CONFIGURE_FLAG_ENCODE);
            mMediaCodec.start();
            mAudioThread = new AudioThread(this);
            Log.i(TAG, "audio prepare finishing");
        } catch (IOException e) {
            Log.e(TAG, "audio prepare fail " + e);
            e.printStackTrace();
        }

    }

    @Override
    public void input(Frame frame) {

    }

    @Override
    public void eos() {
        mIsEos = true;
//        encodeAudioBuffer(null, 0, true);
    }

    /**
     * 音频采集得到的数据传进来编码
     *
     * @param buffer
     * @param length
     */
    public void encodeAudioBuffer(ByteBuffer buffer, int length) {
        if (isEos()) {
            encodeAudioBuffer(null, 0, true);
            return;
        } else {
            encodeAudioBuffer(buffer, length, false);
        }
    }

    /**
     * 编码数据
     *
     * @param buffer
     * @param length
     * @param isEos
     */
    public void encodeAudioBuffer(ByteBuffer buffer, int length, boolean isEos) {
        if (!isRecording()) {
            return;
        }
        final ByteBuffer[] inputBuffers = mMediaCodec.getInputBuffers();
        while (isRecording()) {
            final int inputBufferIndex = mMediaCodec.dequeueInputBuffer(TIMEOUT_USEC);
            if (inputBufferIndex >= 0) {
                final ByteBuffer inputBuffer = inputBuffers[inputBufferIndex];
                inputBuffer.clear();
                if (buffer != null) {
                    inputBuffer.put(buffer);
                }
                if (isEos) {
                    mMediaCodec.queueInputBuffer(inputBufferIndex, 0, 0, getPTS(), MediaCodec.BUFFER_FLAG_END_OF_STREAM);
                } else {
                    mMediaCodec.queueInputBuffer(inputBufferIndex, 0, length, getPTS(), 0);
                }
                output(isEos);
                break;
            } else if (inputBufferIndex == MediaCodec.INFO_TRY_AGAIN_LATER) {
                // wait for MediaCodec encoder is ready to encode
                // nothing to do here because MediaCodec#dequeueInputBuffer(TIMEOUT_USEC)
                // will wait for maximum TIMEOUT_USEC(10msec) on each call
            }
        }
    }

    private long getPTS() {
        return System.nanoTime() / 1000;
    }

    public void start() {
        ThreadPool.getInstance().run(this);
    }
}
