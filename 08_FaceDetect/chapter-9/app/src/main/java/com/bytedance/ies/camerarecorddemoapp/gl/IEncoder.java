package com.bytedance.ies.camerarecorddemoapp.gl;

public interface IEncoder {
    void input(Frame frame);

    void output(boolean isEos);

    void prepare();

    void eos();

    void release();

}
