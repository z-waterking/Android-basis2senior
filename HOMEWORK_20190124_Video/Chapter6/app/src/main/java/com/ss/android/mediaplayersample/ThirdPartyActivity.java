package com.ss.android.mediaplayersample;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

/**
 * @author tianye.xy@bytedance.com
 * 2019/1/9
 */
public class ThirdPartyActivity extends AppCompatActivity {
    private Button simpleModeBtn;
    private Button detailModeBtn;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutId());
        simpleModeBtn = findViewById(R.id.btn_simple_mode);
        simpleModeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ThirdPartyActivity.this, SimplePlayerActivity.class));
            }
        });
        detailModeBtn = findViewById(R.id.btn_detail_mode);
        detailModeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ThirdPartyActivity.this, DetailPlayerActivity.class));
            }
        });
    }

    private int getLayoutId() {
        return R.layout.activity_third_party;
    }
}
