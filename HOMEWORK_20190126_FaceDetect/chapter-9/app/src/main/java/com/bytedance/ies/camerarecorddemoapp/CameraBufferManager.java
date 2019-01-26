package com.bytedance.ies.camerarecorddemoapp;

import java.util.NoSuchElementException;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class CameraBufferManager {

    private static CameraBufferManager mManager = null;

    private Queue<byte[]> mCameraBufferQueue;
    private volatile boolean mStartThread = false;
    private Object mObj = new Object();
    private Thread mThread = new Thread() {
        public void run() {
            while (mStartThread) {
                if (mCameraBufferQueue == null) {
                    return;
                }
                try {
                    byte[] buffer = mCameraBufferQueue.remove();
                    if (buffer != null) {
                        FaceDetectHelper.getHelper().detectFace(buffer, 0, Config.VIDEO_HEIGHT, Config.VIDEO_WIDTH, Config.VIDEO_HEIGHT * 4);
                    }
                } catch (NoSuchElementException e) {
                    e.printStackTrace();
                }
            }
        }
    };

    private CameraBufferManager() {
        mCameraBufferQueue = new ConcurrentLinkedQueue<>();
    }

    public static CameraBufferManager getCameraBufferManager() {
        synchronized (CameraBufferManager.class) {
            if (mManager == null) {
                synchronized (CameraBufferManager.class) {
                    if (mManager == null) {
                        mManager = new CameraBufferManager();
                    }
                }
            }
        }
        return mManager;
    }

    public void addCameraBuffer(byte[] buffer) {
        if (mCameraBufferQueue == null) {
            return;
        }
        mCameraBufferQueue.add(buffer);
        if (!mStartThread) {
            mStartThread = true;
            mThread.start();
        }
    }

    public void release() {
        mStartThread = false;
        try {
            mThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        if (mCameraBufferQueue != null) {
            mCameraBufferQueue.clear();
            mCameraBufferQueue = null;
        }
        mManager = null;
    }
}
