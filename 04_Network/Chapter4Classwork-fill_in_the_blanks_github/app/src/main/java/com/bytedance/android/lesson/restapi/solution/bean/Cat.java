package com.bytedance.android.lesson.restapi.solution.bean;

import android.renderscript.Sampler;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * @author Xavier.S
 * @date 2019.01.17 18:08
 */
public class Cat {
    // TODO-C1 (1) Implement your Cat Bean here according to the response json
    // 单条内容
    // {"breeds": [],"categories": [{"id": 2,"name": "space"}],"id": "5o","url": "https://cdn2.thecatapi.com/images/5o.gif"}
    @SerializedName("id") private String id;
    @SerializedName("url") private String url;
    @SerializedName("categories") private List<Category> categories;
    //取得ID
    public String getId(){
        return id;
    }
    //设置ID
    public void setId(String id){
        this.id = id;
    }
    //取得url
    public String getUrl(){
        return url;
    }
    //设置url
    public void setUrl(String url){
        this.url = url;
    }

    public List<Category> getCategories() {
        return categories;
    }

    //设置Categorie类
    public static class Category {
        /**
         * [0]
         * id : 2
         * name : space
         */
        @SerializedName("id") private int id;
        @SerializedName("name") private String name;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getName(){
            return name;
        }

        public void setName(String name){
            this.name = name;
        }

        @Override public String toString() {
            return "Categorie{" +
                    "id=" + id +
                    ", name='" + name +
                    "}";
        }
    }

    @Override public String toString() {
        return "Cat{" +
                "id='" + id +'\'' +
                ", url='" + url + '\'' +
                ", categories=" + categories +
                '}';
    }

}
