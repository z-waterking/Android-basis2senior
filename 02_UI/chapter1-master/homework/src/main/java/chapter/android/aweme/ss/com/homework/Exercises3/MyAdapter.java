package chapter.android.aweme.ss.com.homework.Exercises3;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import chapter.android.aweme.ss.com.homework.R;
import chapter.android.aweme.ss.com.homework.model.Message;

/**
 * Created by 41626 on 2019/1/19.
 */

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder>{

    List<Message> mData;
    //点击事件监听
    private final ListItemClickListener mOnClickListener;

    public MyAdapter(List<Message> data, ListItemClickListener listener){
        mData = data;
        mOnClickListener = listener;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        Context context = viewGroup.getContext();
        int layoutIdForListItem = R.layout.im_list_item;
        LayoutInflater inflater = LayoutInflater.from(context);
        boolean shouldAttachToParentImmediately = false;

        View view = inflater.inflate(layoutIdForListItem, viewGroup, shouldAttachToParentImmediately);
        MyViewHolder viewHolder = new MyViewHolder(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder myViewHolder, int position) {
        Message message = mData.get(position);
        myViewHolder.updateUI(message);
    }

    @Override
    public int getItemCount() {

        return mData.size();
    }
    //实现了消息页面
    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        //title
        private final TextView view_tv_title;
        private final TextView view_tv_description;
        private final TextView view_tv_time;
        private final ImageView view_iv_avator;
        private final ImageView view_robot_notice;
        private String icon_str;
        public MyViewHolder(@NonNull View itemView){
            super(itemView);
            //名称，描述，时间
            view_tv_title = (TextView) itemView.findViewById(R.id.tv_title);
            view_tv_description = (TextView) itemView.findViewById(R.id.tv_description);
            view_tv_time = (TextView) itemView.findViewById(R.id.tv_time);
            //头像，是否官方
            view_iv_avator = (ImageView) itemView.findViewById(R.id.iv_avatar);
            view_robot_notice = (ImageView) itemView.findViewById(R.id.robot_notice);
            itemView.setOnClickListener(this);
        }

        public void updateUI(Message message){
            //设置名称，描述，时间
            view_tv_title.setText(message.getTitle());
            view_tv_description.setText(message.getDescription());
            view_tv_time.setText(message.getTime());
            //设置头像
            if(message.getIcon().equals("TYPE_ROBOT")) {
                view_iv_avator.setTag(R.drawable.session_robot);
            } else if(message.getIcon().equals("TYPE_GAME")){
                view_iv_avator.setImageResource(R.drawable.icon_micro_game_comment);
            } else if(message.getIcon().equals("TYPE_SYSTEM")){
                view_iv_avator.setImageResource(R.drawable.session_system_notice);
            } else if(message.getIcon().equals("TYPE_USER")){
                view_iv_avator.setImageResource(R.drawable.icon_girl);
            } else if(message.getIcon().equals("TYPE_STRANGER")){
                view_iv_avator.setImageResource(R.drawable.session_stranger);
            }
            icon_str = message.getIcon();

//            view_robot_notice.setImageResource(R.drawable.im_icon_notice_official);
            //设置是否官方账号
            if(message.isOfficial() == true) {
                view_robot_notice.setImageResource(R.drawable.im_icon_notice_official);
            }
        }

        @Override
        public void onClick(View v) {
            System.out.println();
            if (mOnClickListener != null) {
                mOnClickListener.onListItemClick(view_tv_title.getText().toString(), icon_str);
            }
        }

    }
    //------------消息页面

    public interface ListItemClickListener {
        void onListItemClick(String chat_target, String image_id);
    }
}
