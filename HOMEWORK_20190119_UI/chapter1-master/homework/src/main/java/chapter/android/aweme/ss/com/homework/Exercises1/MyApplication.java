package chapter.android.aweme.ss.com.homework.Exercises1;

import android.app.Application;

/**
 * Created by 41626 on 2019/1/19.
 */

public class MyApplication extends Application {
    public String name;
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public void onCreate() {
        // TODO Auto-generated method stub
        super.onCreate();
    }
}
