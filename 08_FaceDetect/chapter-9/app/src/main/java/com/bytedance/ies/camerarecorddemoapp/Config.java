package com.bytedance.ies.camerarecorddemoapp;

import android.os.Environment;

import java.io.File;

public class Config {
    public static final int VIDEO_WIDTH = 720;
    public static final int VIDEO_HEIGHT = 1280;

    public static String getSaveDir() {//保存路径
        String path = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "00recorder" + File.separator;
        File f = new File(path);
        if (!f.exists() || !f.isDirectory()) {
            f.mkdirs();
        }
        return path;
    }

    public static String getSavePath() {
        return getSaveDir() + "aa.mp4";
    }
}
