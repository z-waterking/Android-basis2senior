package com.bytedance.ies.camerarecorddemoapp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.bytedance.ies.camerarecorddemoapp.gl.GLMainActivity;

public class IndexActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_index);

        findViewById(R.id.common_preview).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent it = new Intent(IndexActivity.this, MainActivity.class);
                startActivity(it);
            }
        });

        findViewById(R.id.gl_recorder).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent it = new Intent(IndexActivity.this, GLMainActivity.class);
                startActivity(it);
            }
        });
    }
}
