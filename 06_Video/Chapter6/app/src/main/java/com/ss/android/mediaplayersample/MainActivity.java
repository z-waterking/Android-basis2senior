package com.ss.android.mediaplayersample;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.shuyu.gsyvideoplayer.utils.OrientationUtils;
import com.shuyu.gsyvideoplayer.video.StandardGSYVideoPlayer;
import com.ss.android.mediaplayersample.adapter.MyAdapter;
import com.ss.android.mediaplayersample.bean.Feed;
import com.ss.android.mediaplayersample.bean.FeedResponse;
import com.ss.android.mediaplayersample.utils.NetworkUtils;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.support.v7.widget.RecyclerView.ViewHolder;


/**
 * @author bytedance
 */
public class MainActivity extends AppCompatActivity implements MyAdapter.ListItemClickListener{
    //定义recycleView
    private RecyclerView mRv;
    private List<Feed> mFeeds = new ArrayList<>();
    private MyAdapter mAdapter;
    OrientationUtils orientationUtils;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_simple_play);
        initRecyclerView();
        System.out.println("Recycler Success!");
        fetchFeed();
        System.out.println("Feed Fetch Success!");
    }

    //建立RecyclerView
    private void initRecyclerView() {
        mRv = findViewById(R.id.rv);
        //设置Manager，即设置其样式
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRv.setLayoutManager(layoutManager);

        mRv.setHasFixedSize(true);

        //创建Adapter,将数据传入
        mAdapter = new MyAdapter(mFeeds, this);

        //设置Adapter
        mRv.setAdapter(mAdapter);
    }

    public void fetchFeed() {
        // if success, assign data to mFeeds and call mRv.getAdapter().notifyDataSetChanged()
        // don't forget to call resetRefreshBtn() after response received
        NetworkUtils.getResponseWithRetrofitAsync_Feed(new Callback<FeedResponse>() {
            @Override public void onResponse(Call<FeedResponse> call, Response<FeedResponse> response) {
                //接收到返回值，开始进行处理。
                FeedResponse feeds = response.body();
                mFeeds = feeds.getFeeds();
                mAdapter.updateFeeds(mFeeds);
                mRv.setAdapter(mAdapter);
            }

            @Override public void onFailure(Call<FeedResponse> call, Throwable t) {
                Toast.makeText(MainActivity.this.getApplicationContext(), t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private int getLayoutId() {
        return R.layout.activity_simple_play;
    }

    @Override
    public void onListItemClick(String video_url, String user_name) {
        //点击视频后跳入详情页面
        System.out.println("details!");
        Intent it = new Intent(this, DetailPlayerActivity.class);
        it.putExtra("video_url", video_url);
        it.putExtra("user_name", user_name);
        startActivity(it);
    }
}
