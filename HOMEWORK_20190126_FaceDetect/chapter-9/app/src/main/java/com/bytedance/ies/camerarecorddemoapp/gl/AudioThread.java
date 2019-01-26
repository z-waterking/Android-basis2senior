package com.bytedance.ies.camerarecorddemoapp.gl;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.util.Log;

import java.nio.ByteBuffer;
import java.util.concurrent.Callable;

/**
 * 音频采集线程
 */
public class AudioThread implements Callable<Object> {
    public static final String TAG = "Steven-AudioThread";
    public static final int SAMPLES_PER_FRAME = 1024;   // AAC, bytes/frame/channel
    public static final int FRAMES_PER_BUFFER = 25;     // AAC, frame/buffer/sec
    private static final int SAMPLE_RATE = 44100;   // 44.1[KHz] is only setting guaranteed to be available on all devices.
    private static final int[] AUDIO_SOURCES = new int[]{
            MediaRecorder.AudioSource.MIC,
            MediaRecorder.AudioSource.DEFAULT,
            MediaRecorder.AudioSource.CAMCORDER,
            MediaRecorder.AudioSource.VOICE_COMMUNICATION,
            MediaRecorder.AudioSource.VOICE_RECOGNITION,
    };
    private AudioEncoder mAudioEncoder;

    public AudioThread(AudioEncoder audioEncoder) {
        mAudioEncoder = audioEncoder;
    }

    @Override
    public Object call() throws Exception {
        android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_URGENT_AUDIO);
        try {
            final int min_buffer_size = AudioRecord.getMinBufferSize(
                    SAMPLE_RATE, AudioFormat.CHANNEL_IN_MONO,
                    AudioFormat.ENCODING_PCM_16BIT);
            int buffer_size = SAMPLES_PER_FRAME * FRAMES_PER_BUFFER;
            if (buffer_size < min_buffer_size)
                buffer_size = ((min_buffer_size / SAMPLES_PER_FRAME) + 1) * SAMPLES_PER_FRAME * 2;

            AudioRecord audioRecord = null;
            for (final int source : AUDIO_SOURCES) {
                try {
                    audioRecord = new AudioRecord(
                            source, SAMPLE_RATE,
                            AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT, buffer_size);
                    if (audioRecord.getState() != AudioRecord.STATE_INITIALIZED)
                        audioRecord = null;
                } catch (final Exception e) {
                    audioRecord = null;
                }
                if (audioRecord != null) break;
            }
            if (audioRecord != null) {
                try {
                    if (mAudioEncoder.isRecording()) {
                        Log.v(TAG, "AudioThread:start audio recording");
                        final ByteBuffer buf = ByteBuffer.allocateDirect(SAMPLES_PER_FRAME);
                        int readBytes;
                        audioRecord.startRecording();
                        try {
                            while (mAudioEncoder.isRecording()) {
                                // read audio data from internal mic
                                buf.clear();
                                readBytes = audioRecord.read(buf, SAMPLES_PER_FRAME);
                                if (readBytes > 0) {
                                    // set audio data to encoder
                                    buf.position(readBytes);
                                    buf.flip();
                                    //将数据传给编码器编码
                                    mAudioEncoder.encodeAudioBuffer(buf, readBytes);
                                }
                            }
                        } finally {
                            audioRecord.stop();
                        }
                    }
                } finally {
                    audioRecord.release();
                }
            } else {
                Log.e(TAG, "failed to initialize AudioRecord");
            }
        } catch (final Exception e) {
            Log.e(TAG, "AudioThread#run", e);
        }
        Log.v(TAG, "AudioThread:finished");
        return null;
    }
}