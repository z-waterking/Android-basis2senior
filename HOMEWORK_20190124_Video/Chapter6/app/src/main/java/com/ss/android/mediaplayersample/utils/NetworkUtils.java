package com.ss.android.mediaplayersample.utils;

import com.ss.android.mediaplayersample.bean.FeedResponse;
import com.ss.android.mediaplayersample.network.IMiniDouyinService;

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
