package chapter.android.aweme.ss.com.homework.Exercises3;

import android.content.Context;
import android.media.Image;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.List;

import chapter.android.aweme.ss.com.homework.R;
import chapter.android.aweme.ss.com.homework.model.ChatMessage;

/**
 * Created by 41626 on 2019/1/20.
 */

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ChatViewHolder> {

    private List<ChatMessage> mdata;
    public ChatAdapter(List<ChatMessage> data){
        mdata = data;
    }
    public void Add_Chat_Message(ChatMessage m){
        mdata.add(m);
    }
    @NonNull
    @Override
    public ChatViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        Context context = viewGroup.getContext();
        int layoutIdForListItem = R.layout.chat_room_list_item;
        LayoutInflater inflater = LayoutInflater.from(context);
        boolean shouldAttachToParentImmediately = false;

        View view = inflater.inflate(layoutIdForListItem, viewGroup, shouldAttachToParentImmediately);
        ChatViewHolder viewHolder = new ChatViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ChatViewHolder chatViewHolder, int i) {
        ChatMessage message = mdata.get(i);
        chatViewHolder.updateUI(message);
    }

    @Override
    public int getItemCount() {
        return mdata.size();
    }

    public class ChatViewHolder extends RecyclerView.ViewHolder{
        //建立对应的更新内容
        private final ImageView view_iv_avator_left;
        private final TextView view_iv_content_left;
        private final LinearLayout BgLeft;
        private final LinearLayout chat_box_left;
        private final ImageView view_iv_avator_right;
        private final TextView view_iv_content_right;
        private final LinearLayout BgRIGHT;
        private final LinearLayout chat_box_right;
        public ChatViewHolder(@NonNull View itemView){
            super(itemView);
            view_iv_avator_left = (ImageView)itemView.findViewById(R.id.iv_avatar_left);
            view_iv_content_left = (TextView)itemView.findViewById(R.id.iv_content_left);
            BgLeft = (LinearLayout)itemView.findViewById(R.id.content_bg_left);
            chat_box_left = (LinearLayout)itemView.findViewById(R.id.chat_box_left);
            view_iv_avator_right = (ImageView)itemView.findViewById(R.id.iv_avatar_right);
            view_iv_content_right = (TextView)itemView.findViewById(R.id.iv_content_right);
            BgRIGHT = (LinearLayout)itemView.findViewById(R.id.content_bg_right);
            chat_box_right = (LinearLayout)itemView.findViewById(R.id.chat_box_right);
        }

        public void updateUI(ChatMessage message){
            //判断是左还是右，来更改具体内容的左右
            if(message.ismyself()){
                view_iv_content_right.setText(message.getContent().toString());
                int icon_id = get_Icon(message.getIcon().toString());
                view_iv_avator_right.setImageResource(icon_id);
//                BgRIGHT.setVisibility(View.VISIBLE);
                chat_box_right.setVisibility(View.VISIBLE);
//                view_iv_content_right.setVisibility(View.VISIBLE);
                view_iv_avator_right.setVisibility(View.VISIBLE);
            } else {
                //填入数据
                view_iv_content_left.setText(message.getContent().toString());
                //取得头像ID
                int icon_id = get_Icon(message.getIcon().toString());
                view_iv_avator_left.setImageResource(icon_id);
//                BgLeft.setVisibility(View.VISIBLE);
                chat_box_left.setVisibility(View.VISIBLE);
//                view_iv_content_left.setVisibility(View.VISIBLE);
                view_iv_avator_left.setVisibility(View.VISIBLE);
            }

        }

        public int get_Icon(String icon){
            if(icon.equals("TYPE_ROBOT")) {
                return R.drawable.session_robot;
            } else if(icon.equals("TYPE_GAME")){
                return R.drawable.icon_micro_game_comment;
            } else if(icon.equals("TYPE_SYSTEM")){
                return R.drawable.session_system_notice;
            } else if(icon.equals("TYPE_USER")){
                return R.drawable.icon_girl;
            } else if(icon.equals("TYPE_STRANGER")){
                return R.drawable.session_stranger;
            }
            return R.drawable.icon_girl;
        }

    }

}
