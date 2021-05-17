package chapter.android.aweme.ss.com.homework.Exercises2;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.LinkedList;
import java.util.Queue;
import java.util.Stack;

import chapter.android.aweme.ss.com.homework.R;

/**
 * 作业2：一个抖音笔试题：统计页面所有view的个数
 * Tips：ViewGroup有两个API
 * {@link android.view.ViewGroup #getChildAt(int) #getChildCount()}
 * 用一个TextView展示出来
 */
public class Exercises2 extends AppCompatActivity {
    private TextView show_count;
    private LinearLayout out;
    private int count;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_linearlayout);

        show_count = findViewById(R.id.show_view);
        out = findViewById(R.id.root_frame);
        count = getAllChildViewCount(out);
        //展示出View的总数量
        show_count.setText("页面中加上自身，共有" + Integer.toString(count) + "个View");
    }
    //将其看作树结构，进行层序遍历
    public int getAllChildViewCount(View view) {
        int count = 0;
        if(view == null) {
            return count;
        }
        Queue<View> queue = new LinkedList<View>();
        //根节点入队列
        queue.offer(view);
        while(!queue.isEmpty()) {
            View node = queue.poll();
            //如果是一个ViewGroup，将其子节点全部加入
            if(node instanceof ViewGroup)
            {
                int c =  ((ViewGroup)node).getChildCount();
                for(int i = 0; i < c; i++)
                {
                    queue.offer(((ViewGroup) node).getChildAt(i));
                }
            } else if (node instanceof View) {
                Log.d("zhangsifan", ((TextView)node).getText().toString());
                count++;
            } else {
                //出错
                return -1;
            }
        }
        return count;
    }
}
