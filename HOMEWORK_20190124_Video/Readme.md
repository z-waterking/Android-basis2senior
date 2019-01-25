# HOMEWORK_20190124_Video
## 1.实现效果
* 视频列表，插入封面，点击跳转详情页
<p align="center">
    <img src="./pics/homework_20190125_ShowVideos_And_Details.gif" alt="Sample"  width="300" height="500">
    <p align="center">
        <em>带封面视频及详情跳转</em>
    </p>
</p>
* 视频列表自动播放
<p align="center">
    <img src="./pics/homework_20190125_AutoDisplay.gif.gif" alt="Sample"  width="300" height="500">
    <p align="center">
        <em>视频列表自动播放</em>
    </p>
</p>
## 2.实现方法

* 1.实现网络数据的抓取
```java
public static void getResponseWithRetrofitAsync_Feed(Callback<FeedResponse> callback) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://10.108.10.39:8080/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        retrofit.create(IMiniDouyinService.class).allFeed().
                enqueue(callback);
    }
```
```java
public interface IMiniDouyinService {
    // TODO-C2 (7) Implement your MiniDouyin PostVideo Request here, url: (POST) http://10.108.10.39:8080/minidouyin/video
    // TODO-C2 (8) Implement your MiniDouyin Feed Request here, url: http://10.108.10.39:8080/minidouyin/feed
    @GET("minidouyin/feed") Call<FeedResponse> allFeed();
}
```
* 2.实现抓取到的数据JSON格式的解析
```java
public class FeedResponse {

    // TODO-C2 (2) Implement your FeedResponse Bean here according to the response json
    @SerializedName("success") private boolean success;
    @SerializedName("feeds") private List<Feed> feeds;
    public boolean isSuccess(){
        return success;
    }

    public List<Feed> getFeeds(){
        return feeds;
    }

    @Override public String toString() {
        return "Feeds{" +
                "success='" + success +'\'' +
                ", feeds=" + feeds.toString() +
                '}';
    }
}
```
```java
public class Feed {

    // TODO-C2 (1) Implement your Feed Bean here according to the response json
    //{ "student_id": "3220180826", "user_name": "lq", "image_url": "www", "video_url":"www"}
    @SerializedName("student_id") private String student_id;
    @SerializedName("user_name") private String user_name;
    @SerializedName("image_url") private String image_url;
    @SerializedName("video_url") private String video_url;
    public String getStudent_id(){
        return student_id;
    }

    public String getUser_name(){
        return user_name;
    }

    public String getImage_url(){
        return image_url;
    }

    public String getVideo_url(){
        return video_url;
    }

    @Override public String toString() {
        return "Feed{" +
                "student_id='" + student_id +'\'' +
                ", user_name='" + user_name + '\'' +
                ", image_url=" + image_url + '\'' +
                ", video_url=" + video_url +
                '}';
    }
}
```
* 3.实现RecyclerView
```java
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
```
* 4.实现与RecyclerView绑定的Adapter与ViewHolder
```java

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
```
* 5.实现每个item的布局文件,注意：此处有一个TextView用来进行点击跳转详情页面。此处由于ImageView放在了文件中，因此在进行封面设置时，需要先从其父节点中将其移除。
```xml
<?xml version="1.0" encoding="utf-8"?>
<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
    android:id="@+id/linearlayout"
    android:orientation="vertical"
    android:layout_width="match_parent"
    android:layout_height="match_parent">
    <TextView
        android:id="@+id/detail"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:textSize="30sp"
        android:gravity="center"
        android:text="详情页面"/>
    <com.shuyu.gsyvideoplayer.video.StandardGSYVideoPlayer
        android:id="@+id/video_player"
        android:layout_width="match_parent"
        android:layout_height="match_parent"
        android:layout_centerInParent="true" />
    <ImageView
        android:id="@+id/image_cover"
        android:layout_width="match_parent"
        android:layout_height="match_parent" />
</LinearLayout>
```
* 6.绑定事件的具体实现
```java
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
```
* 7.通过ItemClickListener将点击每个Item的事件带着数据传给了详情页面，在详情页面进行视频的播放。
```java
public class DetailPlayerActivity extends GSYBaseActivityDetail<StandardGSYVideoPlayer> {
    StandardGSYVideoPlayer detailPlayer;

    private String url;
    private String title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_simple_detail_player);

        Intent it = getIntent();
        url = it.getStringExtra("video_url");
        title = it.getStringExtra("user_name");

        detailPlayer = (StandardGSYVideoPlayer) findViewById(R.id.detail_player);
        //增加title
        detailPlayer.getTitleTextView().setVisibility(View.GONE);
        detailPlayer.getBackButton().setVisibility(View.GONE);

        initVideoBuilderMode();
    }

    @Override
    public StandardGSYVideoPlayer getGSYVideoPlayer() {
        return detailPlayer;
    }

    @Override
    public GSYVideoOptionBuilder getGSYVideoOptionBuilder() {
        //内置封面可参考SampleCoverVideo
        ImageView imageView = new ImageView(this);
        //loadCover(imageView, url);
        return new GSYVideoOptionBuilder()
                .setThumbImageView(imageView)
                .setUrl(url)
                .setCacheWithPlay(true)
                .setVideoTitle(title)
                .setIsTouchWiget(true)
                .setRotateViewAuto(false)
                .setLockLand(false)
                .setShowFullAnimation(false)//打开动画
                .setNeedLockFull(true)
                .setSeekRatio(1);
    }

    @Override
    public void clickForFullScreen() {

    }


    /**
     * 是否启动旋转横屏，true表示启动
     */
    @Override
    public boolean getDetailOrientationRotateAuto() {
        return true;
    }

    private void loadCover(ImageView imageView, String url) {
        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        imageView.setImageResource(R.mipmap.xxx1);
        Glide.with(this.getApplicationContext())
                .setDefaultRequestOptions(
                        new RequestOptions()
                                .frame(3000000)
                                .centerCrop()
                                .error(R.mipmap.xxx2)
                                .placeholder(R.mipmap.xxx1))
                .load(url)
                .into(imageView);
    }
}
```
