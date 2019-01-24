package chapter.android.aweme.ss.com.chapter1;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import chapter.android.aweme.ss.com.chapter1.intent.IntentActivity;
import chapter.android.aweme.ss.com.chapter1.lifecycle.LifeCycleActivity;
import chapter.android.aweme.ss.com.chapter1.recycleview.RecycleViewActivity;
import chapter.android.aweme.ss.com.chapter1.view.CommonViewActivity;
import chapter.android.aweme.ss.com.chapter1.xmlparser.XmlActivity;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.btn_lifecycle).setOnClickListener(this);
        findViewById(R.id.btn_xml).setOnClickListener(this);
        findViewById(R.id.btn_intent).setOnClickListener(this);
        findViewById(R.id.btn_recycleview).setOnClickListener(this);
        findViewById(R.id.btn_commonview).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_xml:
                startActivity(new Intent(this, XmlActivity.class));
                break;
            case R.id.btn_lifecycle:
                startActivity(new Intent(this, LifeCycleActivity.class));
                break;
            case R.id.btn_intent:
                startActivity(new Intent(this, IntentActivity.class));
                break;
            case R.id.btn_recycleview:
                startActivity(new Intent(this, RecycleViewActivity.class));
                break;
            case R.id.btn_commonview:
                startActivity(new Intent(this, CommonViewActivity.class));
                break;
        }
    }
}
