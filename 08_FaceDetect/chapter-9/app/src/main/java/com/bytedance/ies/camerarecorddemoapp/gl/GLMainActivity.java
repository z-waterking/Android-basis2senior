package com.bytedance.ies.camerarecorddemoapp.gl;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.PermissionChecker;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.bytedance.ies.camerarecorddemoapp.R;

import static android.R.attr.targetSdkVersion;

public class GLMainActivity extends Activity {

    private CameraGLSurfaceView mCameraSurfaceView;
    private Button mRecordCtrlBtn;
    private boolean mIsRecording = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.gl_main_activity);

        if (!selfPermissionGranted(Manifest.permission.CAMERA) || !selfPermissionGranted(Manifest.permission.WRITE_EXTERNAL_STORAGE) || !selfPermissionGranted(Manifest.permission.RECORD_AUDIO)) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.RECORD_AUDIO}, 0);
        }

        mCameraSurfaceView = (CameraGLSurfaceView) findViewById(R.id.camera_surfaceview);
        mRecordCtrlBtn = (Button) findViewById(R.id.record_ctrl_btn);
        mRecordCtrlBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mIsRecording) {
                    Log.i("GLMainActivity", "stop recording");
                    mCameraSurfaceView.stopRecord();
                    mRecordCtrlBtn.setText("开始录制");
                } else {
                    Log.i("GLMainActivity", "start recording");
                    mCameraSurfaceView.startRecord();
                    mRecordCtrlBtn.setText("停止录制");
                }
                mIsRecording = !mIsRecording;
            }
        });
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

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        mCameraSurfaceView.stopPreview();
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

}
