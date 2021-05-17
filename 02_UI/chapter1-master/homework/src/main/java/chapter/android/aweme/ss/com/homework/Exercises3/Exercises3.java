package chapter.android.aweme.ss.com.homework.Exercises3;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;


import java.io.InputStream;
import java.util.List;

import chapter.android.aweme.ss.com.homework.R;
import chapter.android.aweme.ss.com.homework.model.PullParser;
import chapter.android.aweme.ss.com.homework.model.Message;
/**
 * 大作业:实现一个抖音消息页面,所需资源已放在res下面
 */
public class Exercises3 extends AppCompatActivity implements MyAdapter.ListItemClickListener {

    private MyAdapter mAdapter;
    private List<Message> messages;
    private static final String TAG = "ItemViews";
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tips);
        //取得需要展示的数据
        try {
            InputStream assetInput = getAssets().open("data.xml");
            messages = PullParser.pull2xml(assetInput);
        } catch (Exception exception) {
            exception.printStackTrace();
        }

        //定义recycleView
        RecyclerView recycleView = findViewById(R.id.rv_list);

        //设置Manager，即设置其样式
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recycleView.setLayoutManager(layoutManager);

        recycleView.setHasFixedSize(true);

        //创建Adapter,将数据传入
        mAdapter = new MyAdapter(messages, this);

        //设置Adapter
        recycleView.setAdapter(mAdapter);
    }

    @Override
    public void onListItemClick(String chat_target, String image_id) {
        System.out.println(chat_target);
        Intent it = new Intent(this, ChatRoom.class);
        it.putExtra("chat_target", chat_target);
        it.putExtra("chat_icon", image_id);
        startActivity(it);
    }
}
