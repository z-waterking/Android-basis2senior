package chapter.android.aweme.ss.com.homework;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import chapter.android.aweme.ss.com.homework.Exercises1.Exercises1;
import chapter.android.aweme.ss.com.homework.Exercises2.Exercises2;
import chapter.android.aweme.ss.com.homework.Exercises3.Exercises3;
import chapter.android.aweme.ss.com.homework.widget.CircleImageView;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        CircleImageView circleImageView = findViewById(R.id.circle_view);
        circleImageView.setImageResource(R.drawable.icon_micro_game_comment);
        findViewById(R.id.btn_exercises1).setOnClickListener(this);
        findViewById(R.id.btn_exercises2).setOnClickListener(this);
        findViewById(R.id.btn_exercises3).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_exercises1:
                startActivity(new Intent(this, Exercises1.class));
                break;
            case R.id.btn_exercises2:
                startActivity(new Intent(this, Exercises2.class));
                break;
            case R.id.btn_exercises3:
                startActivity(new Intent(this, Exercises3.class));
                break;
        }
    }
}
