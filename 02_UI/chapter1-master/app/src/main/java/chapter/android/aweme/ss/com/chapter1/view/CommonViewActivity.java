package chapter.android.aweme.ss.com.chapter1.view;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import chapter.android.aweme.ss.com.chapter1.R;

public class CommonViewActivity extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_commonview);
        findViewById(R.id.btn_linearlayout).setOnClickListener(this);
        findViewById(R.id.btn_releatelayout).setOnClickListener(this);
        findViewById(R.id.btn_framelayout).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_linearlayout:
                startActivity(new Intent(this, LinearLayoutActivity.class));
                break;
            case R.id.btn_releatelayout:
                startActivity(new Intent(this, RelativeLayoutActivity.class));
                break;
            case R.id.btn_framelayout:
                startActivity(new Intent(this, FrameLayoutActivity.class));
                break;
        }
    }
}
