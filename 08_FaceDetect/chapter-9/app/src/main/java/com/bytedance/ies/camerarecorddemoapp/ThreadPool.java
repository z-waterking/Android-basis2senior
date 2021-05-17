package com.bytedance.ies.camerarecorddemoapp;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ThreadPool {

    private static ThreadPool mInstance = null;
    private ExecutorService cachedThreadPool;

    private ThreadPool() {
        cachedThreadPool = Executors.newCachedThreadPool();
    }

    public static ThreadPool getInstance() {
        synchronized (ThreadPool.class) {
            if (mInstance == null) {
                mInstance = new ThreadPool();
            }
        }
        return mInstance;
    }

    public void run(Callable callable) {
        if (cachedThreadPool != null) {
            cachedThreadPool.submit(callable);
        }
    }

    public void release() {
        if (cachedThreadPool != null) {
            cachedThreadPool.shutdown();
            cachedThreadPool = null;
        }
        mInstance = null;
    }
}
