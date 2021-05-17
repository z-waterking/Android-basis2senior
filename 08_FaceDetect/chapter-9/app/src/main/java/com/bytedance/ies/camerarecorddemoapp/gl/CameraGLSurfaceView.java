package com.bytedance.ies.camerarecorddemoapp.gl;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Display;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.WindowManager;

import com.bytedance.ies.camerarecorddemoapp.Config;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * 继承GLSurfaceView
 */
public class CameraGLSurfaceView extends GLSurfaceView {
    private static final int CAMERA_ID = 0;
    private Context mContext;
    private static final String TAG = "Steven-" + CameraGLSurfaceView.class.getSimpleName();
    private SurfaceRenderer mRenderer;//OpenGL渲染器
    private Camera mCamera;
    private int mRotation;
    private boolean mIsFrontFace;
    private int mVideoWidth = Config.VIDEO_WIDTH, mVideoHeight = Config.VIDEO_HEIGHT;

    public CameraGLSurfaceView(Context context) {
        super(context);
        init(context);
    }

    public CameraGLSurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    @TargetApi(8)
    private void init(Context context) {
        mContext = context;
        mRenderer = new SurfaceRenderer(this);
        // GLES 2.0, API >= 8
        setEGLContextClientVersion(2);
        setRenderer(mRenderer);
/*      // 设置RENDERMODE_WHEN_DIRTY可以减少性能消耗
        setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY); */
    }

    /**
     * @param holder
     */
    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        stopPreview();
        mRenderer.onSurfaceDestroy();
        super.surfaceDestroyed(holder);
    }

    public void startPreview(int width, int height) {
        width = Config.VIDEO_HEIGHT;
        height = Config.VIDEO_WIDTH;
        initCamera(width, height);
        if (mCamera == null) {
            return;
        }
        try {
            final Camera.Size previewSize = mCamera.getParameters().getPreviewSize();
            Log.i(TAG, String.format("previewSize(%d, %d)", previewSize.width, previewSize.height));
            setVideoSize(previewSize.width, previewSize.height);
            final SurfaceTexture st = mRenderer.getSurfaceTexture();
            st.setDefaultBufferSize(previewSize.width, previewSize.height);
            mCamera.setPreviewTexture(st);//相机和opengl纹理绑定
            if (mCamera != null) {
                //开启摄像头预览
                mCamera.startPreview();
            }
        } catch (Exception e) {
            Log.e(TAG, "startPreview:", e);
            if (mCamera != null) {
                mCamera.release();
                mCamera = null;
            }
        }

    }

    /**
     * 初始化相机
     *
     * @param width
     * @param height
     */
    private void initCamera(int width, int height) {
        Log.d(TAG, "initCamera:");
        if (mCamera == null) {
            try {
                mCamera = Camera.open(CAMERA_ID);
                final Camera.Parameters params = mCamera.getParameters();
                final List<String> focusModes = params.getSupportedFocusModes();
                if (focusModes.contains(Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO)) {
                    params.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO);
                } else if (focusModes
                        .contains(Camera.Parameters.FOCUS_MODE_AUTO)) {
                    params.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO);
                } else {
                    Log.i(TAG, "Camera does not support autofocus");
                }
                final List<int[]> supportedFpsRange = params.getSupportedPreviewFpsRange();
                final int[] max_fps = supportedFpsRange.get(supportedFpsRange.size() - 1);
                params.setPreviewFpsRange(max_fps[0], max_fps[1]);
                params.setRecordingHint(true);
                final Camera.Size closestSize = getClosestSupportedSize(params.getSupportedPreviewSizes(), width, height);
                params.setPreviewSize(closestSize.width, closestSize.height);
                final Camera.Size pictureSize = getClosestSupportedSize(params.getSupportedPictureSizes(), width, height);
                params.setPictureSize(pictureSize.width, pictureSize.height);
                //调整相机角度
                setRotation(params);
                mCamera.setParameters(params);
            } catch (Exception e) {
                Log.e(TAG, "initCamera:", e);
                if (mCamera != null) {
                    mCamera.release();
                    mCamera = null;
                }
            }
        }
    }

    public void stopPreview() {
        Log.v(TAG, "stopPreview:");
        if (mCamera != null) {
            mCamera.stopPreview();
            mCamera.release();
            mCamera = null;
        }
    }


    public void setVideoSize(final int width, final int height) {
        if ((mRotation % 180) == 0) {
            mVideoWidth = width;
            mVideoHeight = height;
        } else {
            mVideoWidth = height;
            mVideoHeight = width;
        }
        //调整OpenGL视口
        queueEvent(new Runnable() {
            @Override
            public void run() {
                mRenderer.updateViewport();
            }
        });
        Log.d(TAG, "setVideoSize: width x height=" + width + " x " + height);
    }

    private static Camera.Size getClosestSupportedSize(List<Camera.Size> supportedSizes, final int requestedWidth, final int requestedHeight) {
        return (Camera.Size) Collections.min(supportedSizes, new Comparator<Camera.Size>() {

            private int diff(final Camera.Size size) {
                return Math.abs(requestedWidth - size.width) + Math.abs(requestedHeight - size.height);
            }

            @Override
            public int compare(final Camera.Size lhs, final Camera.Size rhs) {
                return diff(lhs) - diff(rhs);
            }
        });
    }

    /**
     * 设置摄像头角度
     *
     * @param params
     */
    private final void setRotation(final Camera.Parameters params) {
        final Display display = ((WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
        final int rotation = display.getRotation();
        int degrees = 0;
        switch (rotation) {
            case Surface.ROTATION_0:
                degrees = 0;
                break;
            case Surface.ROTATION_90:
                degrees = 90;
                break;
            case Surface.ROTATION_180:
                degrees = 180;
                break;
            case Surface.ROTATION_270:
                degrees = 270;
                break;
        }
        final Camera.CameraInfo info = new android.hardware.Camera.CameraInfo();
        android.hardware.Camera.getCameraInfo(CAMERA_ID, info);
        mIsFrontFace = (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT);
        if (mIsFrontFace) { // 前置摄像头
            degrees = (info.orientation + degrees) % 360;
            degrees = (360 - degrees) % 360;  // reverse
        } else {  // 后置摄像头
            degrees = (info.orientation - degrees + 360) % 360;
        }
        mCamera.setDisplayOrientation(degrees);
        mRotation = degrees;
        Log.d(TAG, "setRotation:" + degrees);
    }

    public int getVideoHeight() {
        return mVideoHeight;
    }

    public int getVideoWidth() {
        return mVideoWidth;
    }

    public void startRecord() {
        mRenderer.setNeedRecord(true);
    }


    public void stopRecord() {
        mRenderer.setStopRecorder(true);
    }
}
