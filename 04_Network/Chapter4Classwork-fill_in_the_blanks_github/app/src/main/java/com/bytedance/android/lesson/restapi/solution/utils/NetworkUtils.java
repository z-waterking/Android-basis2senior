package com.bytedance.android.lesson.restapi.solution.utils;


import com.bytedance.android.lesson.restapi.solution.bean.Cat;
import com.bytedance.android.lesson.restapi.solution.bean.FeedResponse;
import com.bytedance.android.lesson.restapi.solution.bean.PostVideoResponse;
import com.bytedance.android.lesson.restapi.solution.newtork.ICatService;
import com.bytedance.android.lesson.restapi.solution.newtork.IMiniDouyinService;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * @author Xavier.S
 * @date 2019.01.15 13:27
 */
public class NetworkUtils {

    public static String getResponseWithHttpURLConnection(String url) {
        String result = null;
        InputStream in = null;
        HttpURLConnection urlConnection = null;
        try {
            URL netUrl = new URL(url);
            urlConnection = (HttpURLConnection) netUrl.openConnection();
            urlConnection.setRequestMethod("GET");
            in = new BufferedInputStream(urlConnection.getInputStream());
            result = readStream(in);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return result;
    }
    //实现getResponseWithRetrofitAsync方法用来请求Cat数据
    public static void getResponseWithRetrofitAsync_Cat(Callback<Cat[]> callback) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://api.thecatapi.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        retrofit.create(ICatService.class).randomCat().
                enqueue(callback);
    }

    //实现getResponseWithRetrofitAsync方法用来请求Feed数据
    public static void getResponseWithRetrofitAsync_Feed(Callback<FeedResponse> callback) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://10.108.10.39:8080/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        retrofit.create(IMiniDouyinService.class).allFeed().
                enqueue(callback);
    }


    private static String readStream(final InputStream inputStream) {
        final Scanner scanner = new Scanner(inputStream);
        scanner.useDelimiter("\\A");
        final String data = scanner.next();
        return data;
    }

    private static String readStreamBuffer(InputStream in) {
        StringBuilder result = new StringBuilder();
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(in));
            String s;
            while ((s = reader.readLine()) != null) {
                result.append(s);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result.toString();
    }
}
