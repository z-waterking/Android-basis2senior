package com.bytedance.ies.camerarecorddemoapp;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.pm.PackageManager;
import android.graphics.Canvas;
import android.graphics.ImageFormat;
import android.hardware.Camera;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.PermissionChecker;
import android.widget.Toast;

import java.io.IOException;
import java.util.List;

import static android.R.attr.targetSdkVersion;

public class MainActivity extends Activity {

    private static final String TAG = "GLMainActivity";

    private TextView tv;
    private Camera mCamera;
    private int mPreviewImgTime;
    private SurfaceHolder.Callback mSurfaceCallback;
    private Camera.PreviewCallback mPreviewCallback;
    private Camera.Parameters mParameters;
    private SurfaceView mSurfaceView;
    private SurfaceHolder mSurfaceHolder;
    private ProgressDialog dialog;
    private Button mCopyModeButton;
    private Button mWriteBMPButton;
    private ImageView icon;
    private DrawImageView div;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (!selfPermissionGranted(Manifest.permission.CAMERA) || !selfPermissionGranted(Manifest.permission.WRITE_EXTERNAL_STORAGE) /*|| !selfPermissionGranted(Manifest.permission.RECORD_AUDIO) */) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE/*, Manifest.permission.RECORD_AUDIO*/}, 0);
        }

        // Example of a call to a native method
        tv = (TextView) findViewById(R.id.sample_text);
        icon = (ImageView) findViewById(R.id.icon);
        div = (DrawImageView) findViewById(R.id.paint_iv);
//        icon.setImageResource(R.drawable.diantou);
//        tv.setText("ooo");

        FaceDetectHelper.getHelper().setLicense("TvEbeOPnOCXa62ql1AgSpWADbsODeYUfAz5eo8P+KJPxmD42PeH+UDg1kweybbeXzb3Yj0IHcOtNXMkijk7uJ0n9QS4FnB4Kvp2iKnFDEJ+/wdqGfasiA/3vbvpSakJ79sZG/zt8pMESgPrmaBh59OoMZMpfwAFcibdc/b38KNU=");
        FaceDetectHelper.getHelper().setFaceDetectedCallback(new FaceDetectHelper.OnFaceDetectedCallback() {
            @Override
            public void onFaceDetected(final int ret, final int top, final int bottom, final int left,
                                       final int right) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        //TODO:
                        // 1 将人脸表情等通过ICON展示在UI上面
                        // 2 增加人脸位置返回值之后，通过方框的图在UI上面现实人脸区域
//                        tv.setText(ret + " " + top + " " + bottom + " " + " " + left + " " + right);
                        icon.setVisibility(View.VISIBLE);
                        switch(ret){
                            case 2:icon.setImageResource(R.drawable.zhayan);break;
                            case 4:icon.setImageResource(R.drawable.zhangzui);break;
                            case 8:icon.setImageResource(R.drawable.yaotou);break;
                            case 16:icon.setImageResource(R.drawable.diantou);break;
                            case 32:icon.setImageResource(R.drawable.meimaotiaodong);break;
                            case 64:icon.setImageResource(R.drawable.zuibadudu);break;
                            default:icon.setVisibility(View.INVISIBLE);break;
                        }
//                        System.out.println("Original");
//                        System.out.println("zsf" + "_top" + Integer.toString(top));
//                        System.out.println("zsf" + "_bottom" + Integer.toString(bottom));
//                        System.out.println("zsf" + "_left" + Integer.toString(left));
//                        System.out.println("zsf" + "_right" + Integer.toString(right));
                        div.set_top(top);
                        div.set_bottom(bottom);
                        div.set_left(left);
                        div.set_right(right);
                        div.invalidate();
                    }
                });
            }
        });

        initData();
        initView();
    }

    public boolean selfPermissionGranted(String permission) {
        // For Android < Android M, self permissions are always granted.
        boolean result = true;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            if (targetSdkVersion >= Build.VERSION_CODES.M) {
                // targetSdkVersion >= Android M, we can
                // use Context#checkSelfPermission
                result = this.checkSelfPermission(permission)
                        == PackageManager.PERMISSION_GRANTED;
            } else {
                // targetSdkVersion < Android M, we have to use PermissionChecker
                result = PermissionChecker.checkSelfPermission(this, permission)
                        == PermissionChecker.PERMISSION_GRANTED;
            }
        }

        return result;
    }

    private void initResource() {
        dialog = new ProgressDialog(MainActivity.this);
        dialog.setMessage("正在初始化");
        dialog.setCancelable(false);
        dialog.show();
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    FileUtils.delDir(FileUtils.ROOT_DIR);
                    FileUtils.copyFileIfNeed(MainActivity.this, FileUtils.MODELS_DIR, FileUtils.FACE_DETECT_MODEL);
                } catch (final Exception e) {
                    e.printStackTrace();
                    Log.e("fileException", e.getMessage());
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                } finally {
                    if (dialog != null) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                dialog.dismiss();
                                dialog = null;
                                FaceDetectHelper.getHelper().setFaceDetectModelPath(FileUtils.FACE_DETECT_MODEL_PATH);
                            }
                        });
                    }
                }
            }
        }).start();
    }

    private void initView() {
        mSurfaceView = (SurfaceView) findViewById(R.id.preview_view);
        mSurfaceHolder = mSurfaceView.getHolder();
        mSurfaceHolder.addCallback(mSurfaceCallback);
        mCopyModeButton = findViewById(R.id.copy_model_btn);
        mCopyModeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initResource();
            }
        });

        mWriteBMPButton = findViewById(R.id.write_bmp_btn);
        mWriteBMPButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FaceDetectHelper.getHelper().writeBMP();
            }
        });
    }

    private void initData() {
        mSurfaceCallback = new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder surfaceHolder) {
                boolean isInit = true;
                if (mCamera == null) {
                    isInit = initCamera();
                    Log.d(TAG, "surfaceCreated format: " + mCamera.getParameters().getPreviewFormat());
                }
                if (isInit) {
                    startPreview();
                }
            }

            @Override
            public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {

            }

            @Override
            public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
                stopPreview();
            }
        };
        mPreviewImgTime = 0;
        mPreviewCallback = new Camera.PreviewCallback() {
            @Override
            public void onPreviewFrame(byte[] bytes, Camera camera) {
                if (mCamera == null) {
                    return;
                }
                CameraBufferManager.getCameraBufferManager().addCameraBuffer(bytes);
            }
        };
    }

    //初始化摄像头
    private boolean initCamera() {
        int num = Camera.getNumberOfCameras();
        if (num <= 0) {
            return false;
        }
        boolean open = true;
        int cameraId;
        try {
            if (num == 1) {
                cameraId = Camera.CameraInfo.CAMERA_FACING_BACK;
                mCamera = Camera.open(cameraId);
            } else {
                cameraId = Camera.CameraInfo.CAMERA_FACING_FRONT;
                mCamera = Camera.open(cameraId);
            }
            mCamera.setPreviewCallback(mPreviewCallback);
            mParameters = mCamera.getParameters();
            mParameters.setRotation(90);
            mParameters.setPreviewFormat(ImageFormat.NV21); // 设置NV21预览格式
            List<Camera.Size> list = mCamera.getParameters().getSupportedPreviewSizes();
            if (list != null && !list.isEmpty()) {
                for (Camera.Size size : list) {
                    Log.d(TAG, "camera support size=" + size.width + " " + size.height);
                }
                for (Camera.Size size : list) {
                    if (size.height == Config.VIDEO_WIDTH && size.width == Config.VIDEO_HEIGHT) {
                        mParameters.setPreviewSize(size.width, size.height);//预览带下
                        mCamera.setParameters(mParameters);
                        mCamera.setDisplayOrientation(getCameraDisplayOrientation(cameraId));//预览方向
                        return true;
                    }
                }
            }
        } catch (Exception e) {

        }
        return false;
    }

    private int getCameraDisplayOrientation(int cameraId) {
        //通过相机ID获得相机信息
        android.hardware.Camera.CameraInfo info = new android.hardware.Camera.CameraInfo();
        android.hardware.Camera.getCameraInfo(cameraId, info);

        //获得当前屏幕方向
        int rotation = getWindowManager().getDefaultDisplay().getRotation();
        int degrees = 0;
        switch (rotation) {
            //若屏幕方向与水平轴负方向的夹角为0度
            case Surface.ROTATION_0:
                degrees = 0;
                break;
            //若屏幕方向与水平轴负方向的夹角为90度
            case Surface.ROTATION_90:
                degrees = 90;
                break;
            //若屏幕方向与水平轴负方向的夹角为180度
            case Surface.ROTATION_180:
                degrees = 180;
                break;
            //若屏幕方向与水平轴负方向的夹角为270度
            case Surface.ROTATION_270:
                degrees = 270;
                break;
        }
        int result;
        if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
            //前置摄像头作镜像翻转
            result = (info.orientation + degrees) % 360;
            result = (360 - result) % 360;  // compensate the mirror
        } else {  // back-facing
            result = (info.orientation - degrees + 360) % 360;
        }
        return result;
    }

    private void startPreview() {
        try {
            //绑定surfaceview
            mCamera.setPreviewDisplay(mSurfaceHolder);
            mCamera.startPreview();//开始预览
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void stopPreview() {
        if (mCamera != null) {
            mCamera.setPreviewCallback(null);
            mCamera.stopPreview();
            mCamera.release();
            mCamera = null;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        CameraBufferManager.getCameraBufferManager().release();
        FaceDetectHelper.getHelper().destroy();
    }
}
