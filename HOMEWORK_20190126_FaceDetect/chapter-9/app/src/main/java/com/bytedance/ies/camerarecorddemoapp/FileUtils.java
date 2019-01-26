package com.bytedance.ies.camerarecorddemoapp;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.channels.FileChannel;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;


/**
 * Created by bytedance on 16-11-16.
 */

public class FileUtils {

    public final static String ROOT_DIR = Environment.getExternalStorageDirectory().getPath() + "/CameraRecorderDemoApp/";
    public final static String PHONE_PARAM_DIR = Environment.getExternalStorageDirectory().getPath() + "/CameraRecorderDemoApp/slam/";
    public final static String RESOURCE_DIR = ROOT_DIR + "resource/";
    public final static String STICKER_RESOURCE_DIR = RESOURCE_DIR + "stickers/";
    public final static String MODELS_DIR = ROOT_DIR + "models/";
    public final static String VIDEO_TMP = ROOT_DIR + "tmp/";
    public final static String CONCAT_VIDEO_DIR = ROOT_DIR + "video/";
    public final static String CACHE = ROOT_DIR + "/cache/";
    public final static String MUSIC = ROOT_DIR + "music/";

    public final static String MUSIC_EFFECT_DIR = RESOURCE_DIR;
    public final static String FILTER_DIR = RESOURCE_DIR + "filters/";
    public final static String BEAUTY_12_DIR = RESOURCE_DIR + "Beauty_12/";

    public final static String RESHAPE_DIR_NAME = RESOURCE_DIR;

    public final static String RESHAPE_FILENAME = "FaceReshape_V2";

    public final static String FILTER_ASSETT_FILENAME = "filter.zip";
    public final static String BEAUTY_12_FILENAME = "Beauty_12.zip";
    public static final String FACE_TRACK_MODEL_NAME = "face_track.model";
    public static final String FACE_ATTRIBUTE_NAME = "face_attribute.model";
    public static final String SENSEME_NAME = "senseme.lic";
    public static final String SKELETON_CONFIG_NAME = "template.config";
    public static final String SKELETON_DATA_NAME = "skeleton.data";
    public final static String SUFFIX = ".zip";
    public final static String PHONEPARAM = "phoneParam.txt";
    public final static String MODEL = "models";

    public final static String FACE_DETECT_MODEL = "tt_face_v2.0.model";
    public final static String FACE_DETECT_MODEL_PATH = MODELS_DIR + FACE_DETECT_MODEL;

    public final static float VibePresetVolume[] = {0.4429963f, 0.2397063f, 0.0992903f, 0.347241f, 0.5882259f, 0.5687069f, 0.2541387f,
            0.6528268f, 0.3379034f, 0.1778982f, 0.05381421f, 0.0162788f, 0.004924338f, 0.0004506077f,
            0.1621538f, 0.04905154f, 0.01483809f, 0.7255028f, 0.5484459f, 0.2451727f, 0.07416476f,
            0.02243484f, 0.00678654f, 0.002052928f, 0.0006210108f, 0.0001878558f, 0f, 0f, 0f, 0f, 0f, 0f, 0f,
            0f, 0.8526676f, 0.1160548f, 0.03510658f, 0.01061974f, 0.6121632f, 0.3366898f, 0.05601677f,
            0.01694507f, 0.005125884f, 0.00155058f, 0.0004690505f, 0.0001418878f, 0f, 0f, 0f, 0f, 0f,
            0.8480413f, 0.385128f, 0.1165012f, 0.03524162f, 0.003224829f, 0.0009755107f, 0.000295092f,
            0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0.7919002f, 0.357477f, 0.1081368f, 0f};


    public static List<String> ResourcesList = new ArrayList<>();

    static {
        //  ResourcesList.add("baozi");
        ResourcesList.add("2D_angel");
        ResourcesList.add("2D_bubble");
        ResourcesList.add("2D_glass");
        ResourcesList.add("2D_joker");
        ResourcesList.add("2D_zhangyixing");
        ResourcesList.add("2D_rabbiteating");
        ResourcesList.add("E12_3D_Glass_Crystal_a02");
        ResourcesList.add("E12_3D_hat_fj_a01_155");
        ResourcesList.add("E12_3D_hat_laser_a01");
        ResourcesList.add("E12_3D_hat_universive_a01");
        ResourcesList.add("E12_3D_Headwear_Jingu_a01");
        ResourcesList.add("E12_3d_rabat_ww_a01_am");
        ResourcesList.add("E12_3D_suanglasses_heart_a01_155");
        ResourcesList.add("E12_3D_sunglass_love_a01");
        ResourcesList.add("E12_3D_sunglasses_blue_a01");
        ResourcesList.add("E12_3D_sunglasses_universe_a01");
        ResourcesList.add("E12_3D_sunglasses_yingguang_a02");
        ResourcesList.add("E12_D_Glass_Diamond_a01");
        ResourcesList.add("E12_D_glasses_cobain_a02");
        ResourcesList.add("E12_D_sunglasses_gold_ear_a01");
        ResourcesList.add("E12_D_sunglasses_open_a01");
        ResourcesList.add("E12_D_sunglasses_pin_a01");
        ResourcesList.add("E12_D_sunglasses_ray_a01");
        ResourcesList.add("E12_D_sunglasses_wyf_a01");
        ResourcesList.add("E12_Glass_Douyin_a02");

        ResourcesList.add("Matting_SkyBox_Happy");
        ResourcesList.add("Matting_agh");

        ResourcesList.add("FaceDistortion_Abianpang");
        ResourcesList.add("FaceDistortion_Honest");
        ResourcesList.add("FaceMakeup_002");
        ResourcesList.add("FaceMakeup_0023");
        ResourcesList.add("FaceMakeup_0024");
        ResourcesList.add("HairColor_Pure");
        //2.1newfeature
        ResourcesList.add("FP_2x2");
        ResourcesList.add("FP_2x2_diff");
        ResourcesList.add("FP_3x3");
        ResourcesList.add("KL");
        ResourcesList.add("LZ");
        ResourcesList.add("YS+BX+00");
        ResourcesList.add("YS+3D+10");
        ResourcesList.add("Multiview_test");
        ResourcesList.add("Multiview_test_UV");
        ResourcesList.add("Matting_chongwuxiaojingling");
        ResourcesList.add("dance_demo_1");
        ResourcesList.add("redpackets_demo");
        ResourcesList.add("2DSticker_hongbao");
        ResourcesList.add("AR_Andy");
        ResourcesList.add("AR_Fz_ph");
        ResourcesList.add("AR_hanbao");
        ResourcesList.add("AR_Panda");

//        ResourcesList.add("Signal");
    }

    public static List<String> FilterList = new ArrayList<>();

    static {
        for (int i = 1; i <= 19; i++) {
            if (i < 10) {
                FilterList.add("Filter_0" + i);
            } else {
                FilterList.add("Filter_" + i);
            }
        }
    }

    public final static List<String> MusicEffectList = new ArrayList<>();

    static {
        MusicEffectList.add("electro");
        MusicEffectList.add("boombox");
        MusicEffectList.add("rave");
    }

    public final static List<String> musicList = new ArrayList<>();

    static {
        musicList.add("music00001.mp3");
        musicList.add("music00002.mp3");
        musicList.add("music00003.mp3");
        musicList.add("music00004.mp3");
    }

    public static void copyFile(File src, File dst) throws IOException {
        FileInputStream inStream = new FileInputStream(src);
        FileOutputStream outStream = new FileOutputStream(dst);
        FileChannel inChannel = inStream.getChannel();
        FileChannel outChannel = outStream.getChannel();
        inChannel.transferTo(0, inChannel.size(), outChannel);
        inStream.close();
        outStream.close();
    }

    public static boolean copyFileIfNeed(Context context, String dir, String fileName) {
        File dirF = new File(dir);
        if (!dirF.exists()) {
            dirF.mkdirs();
        }
        String path = dir + fileName;

        if (!path.isEmpty()) {
            File file = new File(path);
            if (!file.exists()) {
                try {

                    file.createNewFile();
                    InputStream in = context.getApplicationContext().getAssets().open(fileName);
                    if (in == null) {
                        return false;
                    }
                    OutputStream out = new FileOutputStream(file);
                    byte[] buffer = new byte[4096];
                    int n;
                    while ((n = in.read(buffer)) > 0) {
                        out.write(buffer, 0, n);
                    }
                    in.close();
                    out.close();
                } catch (IOException e) {
                    file.delete();
                    return false;
                }
            }
        }
        return true;
    }

    public static File getOutputMediaFile() {
        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_DCIM), "Camera");
        // This location works best if you want the created images to be shared
        // between applications and persist after your app has been uninstalled.

        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                Log.e("FileUtil", "failed to create directory");
                return null;
            }
        }

        // Create a media file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.CHINESE).format(new Date());
        File mediaFile = new File(mediaStorageDir.getPath() + File.separator +
                "IMG_" + timeStamp + ".jpg");

        return mediaFile;
    }

    public static boolean makeDir(String dirPath) {
        if (dirPath.isEmpty()) {
            return false;
        }
        File dir = new File(dirPath);
        if (!dir.exists()) {
            return dir.mkdirs();
        }
        return false;
    }

    public static boolean delDir(String dirPath) {
        if (dirPath.isEmpty()) {
            return false;
        }
        File dir = new File(dirPath);
        return !dir.isFile() && dir.exists() && deleteDir(dir);
    }


    /**
     * 删除空目录
     *
     * @param dir 将要删除的目录路径
     */
    private static void doDeleteEmptyDir(String dir) {
        boolean success = (new File(dir)).delete();
        if (success) {
            System.out.println("Successfully deleted empty directory: " + dir);
        } else {
            System.out.println("Failed to delete empty directory: " + dir);
        }
    }

    /**
     * 递归删除目录下的所有文件及子目录下所有文件
     *
     * @param dir 将要删除的文件目录
     * @return boolean Returns "true" if all deletions were successful.
     * If a deletion fails, the method stops attempting to
     * delete and returns "false".
     */
    private static boolean deleteDir(File dir) {
        if (dir.isDirectory()) {
            String[] children = dir.list();
            //递归删除目录中的子目录下
            for (int i = 0; i < children.length; i++) {
                boolean success = deleteDir(new File(dir, children[i]));
                if (!success) {
                    return false;
                }
            }
        }
        // 目录此时为空，可以删除
        return dir.delete();
    }

    private static boolean hasParentDir(InputStream inputStream) throws IOException {
        java.util.zip.ZipInputStream inZip = new java.util.zip.ZipInputStream(inputStream);
        java.util.zip.ZipEntry zipEntry;
        String szName;
        boolean hasParentDir = true;
        while ((zipEntry = inZip.getNextEntry()) != null) {
            szName = zipEntry.getName();
            if (szName.equals("config.json"))
                hasParentDir = false;
        }
        inZip.close();
        return hasParentDir;
    }

    private static void UnZipFolder(InputStream inputStream, String outDirName) throws Exception {

        java.util.zip.ZipEntry zipEntry;
        String szName = "";


        java.util.zip.ZipInputStream inZip = new java.util.zip.ZipInputStream(inputStream);

        while ((zipEntry = inZip.getNextEntry()) != null) {
            szName = zipEntry.getName();

            if (zipEntry.isDirectory()) {

                szName = szName.substring(0, szName.length() - 1);
                java.io.File folder = new java.io.File(outDirName + java.io.File.separator + szName);
                if (folder.exists()) {
                    continue;
                }
                folder.mkdirs();
            } else {
                java.io.File file = new java.io.File(outDirName + java.io.File.separator + szName);
                if (file.exists()) {
                    break;
                }
                file.createNewFile();
                // get the output stream of the file
                java.io.FileOutputStream out = new java.io.FileOutputStream(file);
                int len;
                byte[] buffer = new byte[1024];
                // read (len) bytes into buffer
                while ((len = inZip.read(buffer)) != -1) {
                    // write (len) byte from buffer at the position 0
                    out.write(buffer, 0, len);
                    out.flush();
                }
                out.close();
            }
        }//end of while

        inZip.close();
    }

    public static void UnZipAssetFolder(Context context, String assetFileName, String outDirName) throws Exception {
        InputStream in = context.getAssets().open(assetFileName);
        File dirFile = new File(outDirName);
        if (dirFile.exists()) {
            if (dirFile.isFile()) {
                dirFile.delete();
                dirFile.mkdirs();
            }
        } else {
            dirFile.mkdirs();
        }
        if (true) {
            java.io.File folder = new java.io.File(outDirName + java.io.File.separator + GetFileName(assetFileName));
            if (folder.exists()) {
                deleteDir(folder);
            }

            folder.mkdirs();
            outDirName += java.io.File.separator + GetFileName(assetFileName);
        }
        in.close();
        in = context.getAssets().open(assetFileName);

        UnZipFolder(in, outDirName);
    }

    public static int UnZipFileFolder(String filePath, String outDirName) throws Exception {
        InputStream in = new FileInputStream(new File(filePath));
        File dirFile = new File(outDirName);
        if (dirFile.exists()) {
            if (dirFile.isFile()) {
                dirFile.delete();
                dirFile.mkdirs();
            }
        } else {
            dirFile.mkdirs();
        }
        if (true) {
            java.io.File folder = new java.io.File(outDirName + java.io.File.separator + GetFileName(filePath));
            if (folder.exists()) {
                deleteDir(folder);
            }

            folder.mkdirs();
            outDirName += java.io.File.separator + GetFileName(filePath);
        }
        in.close();
        in = new FileInputStream(new File(filePath));
        UnZipFolder(in, outDirName);
        return 0;
    }


    public static String GetFileName(String pathandname) {

        int start = pathandname.lastIndexOf("/");
        int end = pathandname.lastIndexOf(".");

        if (end != -1) {
            return pathandname.substring(start + 1, end);
        } else {
            return null;
        }

    }

    public static void makeSureNoMedia(String dir) {
        try {
            new File(dir, ".nomedia").createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
