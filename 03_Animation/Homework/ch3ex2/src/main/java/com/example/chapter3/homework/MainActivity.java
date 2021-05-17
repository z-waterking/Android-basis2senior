package com.example.chapter3.homework;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.Button;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.pes.androidmaterialcolorpickerdialog.ColorPicker;
import com.pes.androidmaterialcolorpickerdialog.ColorPickerCallback;

public class MainActivity extends AppCompatActivity {

    private View target;
    private View startColorPicker;
    private View endColorPicker;
    private Button durationSelector;
    private AnimatorSet animatorSet;
    private RainbowTextView rainbowText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //rainbowText
        rainbowText = findViewById(R.id.rainbow);
        rainbowText.setVisibility(View.VISIBLE);

        //-----------rainbowText------------


        target = findViewById(R.id.target);
        startColorPicker = findViewById(R.id.start_color_picker);
        endColorPicker = findViewById(R.id.end_color_picker);
        durationSelector = findViewById(R.id.duration_selector);

        startColorPicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ColorPicker picker = new ColorPicker(MainActivity.this);
                picker.setColor(getBackgroundColor(startColorPicker));
                picker.enableAutoClose();
                picker.setCallback(new ColorPickerCallback() {
                    @Override
                    public void onColorChosen(int color) {
                        onStartColorChanged(color);
                    }
                });
                picker.show();
            }
        });

        endColorPicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ColorPicker picker = new ColorPicker(MainActivity.this);
                picker.setColor(getBackgroundColor(endColorPicker));
                picker.enableAutoClose();
                picker.setCallback(new ColorPickerCallback() {
                    @Override
                    public void onColorChosen(int color) {
                        onEndColorChanged(color);
                    }
                });
                picker.show();
            }
        });

        durationSelector.setText(String.valueOf(1000));
        durationSelector.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new MaterialDialog.Builder(MainActivity.this)
                        .inputType(InputType.TYPE_CLASS_NUMBER)
                        .input(getString(R.string.duration_hint), durationSelector.getText(), new MaterialDialog.InputCallback() {
                            @Override
                            public void onInput(@NonNull MaterialDialog dialog, CharSequence input) {
                                onDurationChanged(input.toString());
                            }
                        })
                        .show();
            }
        });
        resetTargetAnimation();
    }

    private void onStartColorChanged(int color) {
        startColorPicker.setBackgroundColor(color);
        resetTargetAnimation();
    }

    private void onEndColorChanged(int color) {
        endColorPicker.setBackgroundColor(color);
        resetTargetAnimation();
    }

    private void onDurationChanged(String input) {
        boolean isValid = true;
        try {
            int duration = Integer.parseInt(input);
            if (duration < 100 || duration > 10000) {
                isValid = false;
            }
        } catch (Throwable e) {
            isValid = false;
        }
        if (isValid) {
            durationSelector.setText(input);
            resetTargetAnimation();
        } else {
            Toast.makeText(MainActivity.this, R.string.invalid_duration, Toast.LENGTH_LONG).show();
        }
    }

    private int getBackgroundColor(View view) {
        Drawable bg = view.getBackground();
        if (bg instanceof ColorDrawable) {
            return ((ColorDrawable) bg).getColor();
        }
        return Color.WHITE;
    }

    private void resetTargetAnimation() {
        if (animatorSet != null) {
            animatorSet.cancel();
        }
        //TODO：第一种方法，利用ObjectAnimator进行实现
        // 在这里实现了一个 ObjectAnimator，对 target 控件的背景色进行修改
        // 可以思考下，这里为什么要使用 ofArgb，而不是 ofInt 呢？
        ObjectAnimator animator1 = ObjectAnimator.ofArgb(target,
                "backgroundColor",
                getBackgroundColor(startColorPicker),
                getBackgroundColor(endColorPicker));
        animator1.setDuration(Integer.parseInt(durationSelector.getText().toString()));
        animator1.setRepeatCount(ObjectAnimator.INFINITE);
        animator1.setRepeatMode(ObjectAnimator.REVERSE);

        // TODO 1：在这里实现另一个 ObjectAnimator，对 target 控件的大小进行缩放，从 1 到 2 循环
        ObjectAnimator animator_scaleX = ObjectAnimator.ofFloat(target,
                "scaleX",
                1,
                        2);
        ObjectAnimator animator_scaleY = ObjectAnimator.ofFloat(target,
                "scaleY",
                1,
                2);
        animator_scaleX.setDuration(Integer.parseInt(durationSelector.getText().toString()));
        animator_scaleX.setRepeatCount(ObjectAnimator.INFINITE);
        animator_scaleX.setRepeatMode(ObjectAnimator.REVERSE);
        animator_scaleY.setDuration(Integer.parseInt(durationSelector.getText().toString()));
        animator_scaleY.setRepeatCount(ObjectAnimator.INFINITE);
        animator_scaleY.setRepeatMode(ObjectAnimator.REVERSE);

        // TODO 2：在这里实现另一个 ObjectAnimator，对 target 控件的透明度进行修改，从 1 到 0.5f 循环
        ObjectAnimator animator_alpha = ObjectAnimator.ofFloat(target,
                "alpha",
                1f,
                        0.5f);
        animator_alpha.setDuration(Integer.parseInt(durationSelector.getText().toString()));
        animator_alpha.setRepeatCount(ObjectAnimator.INFINITE);
        animator_alpha.setRepeatMode(ObjectAnimator.REVERSE);
        // TODO 3: 将上面创建的其他 ObjectAnimator 都添加到 AnimatorSet 中
        animatorSet = new AnimatorSet();
        animatorSet.playTogether(animator1, animator_scaleX, animator_scaleY, animator_alpha);
        animatorSet.start();
        //---------------------------利用ObjectAnimator进行实现

        //TODO：第二种方法，利用ValueAnimator进行实现
        /*
        //建立一个从0到1的ValueAnimator
        ValueAnimator anim = ValueAnimator.ofFloat(0f, 1f);
        anim.setDuration(Integer.parseInt(durationSelector.getText().toString()));
        anim.setRepeatCount(ObjectAnimator.INFINITE);
        anim.setRepeatMode(ValueAnimator.REVERSE);
        anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float currentValue = (float)animation.getAnimatedValue();
                System.out.println(currentValue);
                //将当前值映射为对应的需要改变的值
                //对于背景颜色
                int start_color = getBackgroundColor(startColorPicker);
                int end_color = getBackgroundColor(endColorPicker);
                int back_color = start_color;
                //对于颜色的调控不够准确
                //TODO：需要进行更多的文档查阅
                if(start_color != end_color){
                    back_color = (int)(currentValue*(float)(end_color-start_color)) + start_color;
                }
                target.setBackgroundColor(back_color);

                //对于大小
                double target_scaleX = currentValue + 1.0;
                double target_scaleY = currentValue + 1.0;
                target.setScaleX((float)target_scaleX);
                target.setScaleY((float)target_scaleY);
                //对于透明度
                double target_alpha = -0.5*(currentValue-2.0);
                target.setAlpha(1f);
            }
        });
        anim.start();
        */
    }
}
