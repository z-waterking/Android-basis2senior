package com.bytedance.ies.camerarecorddemoapp;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.Display;

public class DrawImageView extends android.support.v7.widget.AppCompatImageView {
    private int top = 100;
    private int bottom = 100;
    private int left = 100;
    private int right = 100;
    public DrawImageView(Context context, @Nullable AttributeSet attrs)
    {
        super(context, attrs);
    }
    Paint paint = new Paint();
    {
        paint.setAntiAlias(true);
        //用于防止边缘的锯齿
        paint.setColor(Color.BLUE);
        // 设置颜色
        paint.setStyle(Paint.Style.STROKE);
        //设置样式为空心矩形
        paint.setStrokeWidth(2.5f);
        //设置空心矩形边框的宽度
        paint.setAlpha(1000);
        //设置透明度
    }

    public void set_top(int top){
        this.top = top;
    }
    public void set_bottom(int bottom){
        this.bottom = bottom;
    }
    public void set_left(int left){
        this.left = left;
    }
    public void set_right(int right){
        this.right = right;
    }
    @Override protected void onDraw(Canvas canvas)
    {
        super.onDraw(canvas);
        int height = getHeight();
        int width = getWidth();
        //left, top, right, bottom
        canvas.drawRect(new Rect((int) (width-bottom*1.5), (int) (height-left*1.5), (int) (width-top*1.5), (int) (height-right*1.5)),paint);
        //绘制矩形，并设置矩形框显示的位置
    }
}
