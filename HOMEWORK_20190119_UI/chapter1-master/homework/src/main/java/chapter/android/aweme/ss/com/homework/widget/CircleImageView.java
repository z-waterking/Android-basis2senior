package chapter.android.aweme.ss.com.homework.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Xfermode;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatImageView;
import android.util.AttributeSet;

/**
 * 为了实现都要圆形图片效果，这里直接提供给大家使用该控件
 */
public class CircleImageView extends AppCompatImageView {


    private int width;
    private int height;
    private float radius;
    private Xfermode xfermode;
    private Paint paint;
    private Path path; // 用来裁剪图片的path


    public CircleImageView(Context context) {
        this(context, null);
    }

    public CircleImageView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CircleImageView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);


        paint = new Paint();
        path = new Path();

        xfermode = new PorterDuffXfermode(PorterDuff.Mode.DST_IN);
        setScaleType(ScaleType.CENTER_CROP);
    }


    @Override
    protected void onDraw(Canvas canvas) {
        // 使用离屏缓存，新建一个srcRectF区域大小的图层
        canvas.saveLayer(null, null, Canvas.ALL_SAVE_FLAG);
        // ImageView自身的绘制流程，即绘制图片
        super.onDraw(canvas);
        // 给path添加一个圆形
        path.addCircle(width / 2.0f, height / 2.0f, radius, Path.Direction.CCW);
        paint.setAntiAlias(true);
        // 画笔为填充模式
        paint.setStyle(Paint.Style.FILL);
        // 设置混合模式
        paint.setXfermode(xfermode);
        // 绘制path
        canvas.drawPath(path, paint);
        // 清除Xfermode
        paint.setXfermode(null);
        // 恢复画布状态
        canvas.restore();
    }

    /**
     * 计算图片原始区域的RectF
     */
    private void initSrcRectF() {
        radius = Math.min(width, height) / 2.0f;
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        width = w;
        height = h;

        initSrcRectF();
    }

}
