package com.bytedance.android.lesson.restapi.solution.bean;

import com.google.gson.annotations.SerializedName;

/**
 * @author Xavier.S
 * @date 2019.01.20 14:18
 */
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
