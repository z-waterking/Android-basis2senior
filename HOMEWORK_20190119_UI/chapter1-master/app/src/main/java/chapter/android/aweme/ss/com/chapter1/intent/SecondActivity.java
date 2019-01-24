package chapter.android.aweme.ss.com.chapter1.intent;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import chapter.android.aweme.ss.com.chapter1.R;

public class SecondActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);
        Intent intent = getIntent();
        Uri data = intent.getData();
        String type = intent.getType();
        TextView intentData = findViewById(R.id.tv_intent_data);
        intentData.setText(String.format("uri=%s\ntype=%s", data.toString(), type));
    }
}
