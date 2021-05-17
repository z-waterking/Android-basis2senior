package com.bytedance.android.lesson.restapi.solution.bean;

import com.google.gson.annotations.SerializedName;

/**
 * @author Xavier.S
 * @date 2019.01.18 17:53
 */
public class PostVideoResponse {

    // TODO-C2 (3) Implement your PostVideoResponse Bean here according to the response json
    //{"success":true, "item"{"student_id":"123", "user_name":"123", "image_url":"www", "video_url":"www"}}
    @SerializedName("success") boolean success;
    @SerializedName("item") Item item;

    public boolean IsSuccess(){
        return success;
    }

    public Item getItem(){
        return item;
    }

    public static class Item{
        @SerializedName("student_id") String student_id;
        @SerializedName("user_name") String user_name;
        @SerializedName("image_url") String image_url;
        @SerializedName("video_url") String video_url;
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
            return "Item{" +
                    "student_id'" + student_id +
                    ", user_name=" + user_name +
                    ", image_url=" + image_url +
                    ", video_url=" + video_url +
                    '}';
        }
    }

    @Override public String toString() {
        return "PostVideoResponse{" +
                "success'" + success +
                ", item=" + item.toString() +
                '}';
    }
}
