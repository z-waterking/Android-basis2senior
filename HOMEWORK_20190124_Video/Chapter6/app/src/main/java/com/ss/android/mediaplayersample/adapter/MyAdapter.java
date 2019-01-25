package com.ss.android.mediaplayersample.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.shuyu.gsyvideoplayer.video.StandardGSYVideoPlayer;
import com.shuyu.gsyvideoplayer.utils.OrientationUtils;
import com.ss.android.mediaplayersample.R;
import com.ss.android.mediaplayersample.bean.Feed;

import java.util.HashMap;
import java.util.List;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder>{

    public List<Feed> mData;
    private final ListItemClickListener mOnClickListener;
    public MyAdapter(List<Feed> data, ListItemClickListener listener){
        mData = data;
        mOnClickListener = listener;
    }
    public void updateFeeds(List<Feed> feeds){
        mData = feeds;
    }
    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        Context context = viewGroup.getContext();
        int layoutIdForListItem = R.layout.player;
        LayoutInflater inflater = LayoutInflater.from(context);
        boolean shouldAttachToParentImmediately = false;

        View view = inflater.inflate(layoutIdForListItem, viewGroup, shouldAttachToParentImmediately);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.video_url = mData.get(position).getVideo_url();
        holder.user_name = mData.get(position).getUser_name();
        holder.image_url = mData.get(position).getImage_url();
        holder.id        = mData.get(position).getStudent_id();
        StandardGSYVideoPlayer videoPlayer = holder.videoPlayer;
        videoPlayer.setUp(holder.video_url, true, holder.user_name);
        //增加封面
        ImageView imageView = holder.imageView;
        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        Glide.with(imageView.getContext()).load(holder.image_url).into(imageView);
        holder.linearLayout.removeView(imageView);
        videoPlayer.setThumbImageView(imageView);
        //用视频第一帧作为封面
        //增加title
//        videoPlayer.getTitleTextView().setVisibility(View.VISIBLE);
        //是否可以滑动调整
        videoPlayer.setIsTouchWiget(true);
        //列表自动播放
        videoPlayer.startPlayLogic();
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        //绑定播放器
        private LinearLayout linearLayout;
        private StandardGSYVideoPlayer videoPlayer;
        private TextView textView;
        private ImageView imageView;
        private String id;
        private String image_url;
        private String user_name;
        private String video_url;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            linearLayout = (LinearLayout) itemView.findViewById(R.id.linearlayout);
            videoPlayer = (StandardGSYVideoPlayer) itemView.findViewById(R.id.video_player);
            imageView = (ImageView) itemView.findViewById(R.id.image_cover);
            textView = (TextView) itemView.findViewById(R.id.detail);
            textView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (mOnClickListener != null) {
                mOnClickListener.onListItemClick(video_url, user_name);
            }
        }
    }

    public interface ListItemClickListener {
        void onListItemClick(String video_url, String user_name);
    }
}


//
//    private void init() {
//        videoPlayer = (StandardGSYVideoPlayer) findViewById(R.id.video_player);
//
//        String source1 = "http://9890.vod.myqcloud.com/9890_4e292f9a3dd011e6b4078980237cc3d3.f20.mp4";
//        videoPlayer.setUp(source1, true, "测试视频");
//
//        //增加封面
//        ImageView imageView = new ImageView(this);
//        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
//        imageView.setImageResource(R.mipmap.xxx1);
//        videoPlayer.setThumbImageView(imageView);
//        //增加title
//        videoPlayer.getTitleTextView().setVisibility(View.VISIBLE);
//        //设置返回键
//        videoPlayer.getBackButton().setVisibility(View.VISIBLE);
//        //设置旋转
//        orientationUtils = new OrientationUtils(this, videoPlayer);
//        //设置全屏按键功能,这是使用的是选择屏幕，而不是全屏
//        videoPlayer.getFullscreenButton().setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                orientationUtils.resolveByClick();
//            }
//        });
//        //是否可以滑动调整
//        videoPlayer.setIsTouchWiget(true);
//        //设置返回按键功能
//        videoPlayer.getBackButton().setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                onBackPressed();
//            }
//        });
//        videoPlayer.startPlayLogic();
//    }
//
//    @Override
//    protected void onPause() {
//        super.onPause();
//        videoPlayer.onVideoPause();
//    }
//
//    @Override
//    protected void onResume() {
//        super.onResume();
//        videoPlayer.onVideoResume();
//    }
//
//    @Override
//    protected void onDestroy() {
//        super.onDestroy();
//        GSYVideoManager.releaseAllVideos();
//        if (orientationUtils != null)
//            orientationUtils.releaseListener();
//    }
//
//    @Override
//    public void onBackPressed() {
//        //先返回正常状态
//        if (orientationUtils.getScreenType() == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
//            videoPlayer.getFullscreenButton().performClick();
//            return;
//        }
//        //释放所有
//        videoPlayer.setVideoAllCallBack(null);
//        super.onBackPressed();
//    }
