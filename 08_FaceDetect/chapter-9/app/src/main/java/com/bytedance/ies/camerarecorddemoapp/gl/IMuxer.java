package com.bytedance.ies.camerarecorddemoapp.gl;

import android.media.MediaCodec;

import java.nio.ByteBuffer;

/**
 * Muxer接口
 */

public interface IMuxer {
    void stop();

    void release();

    void writeSampleData(int trackIndex, ByteBuffer byteBuf, MediaCodec.BufferInfo bufferInfo);

    void start();
}
