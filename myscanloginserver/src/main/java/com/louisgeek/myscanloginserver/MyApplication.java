package com.louisgeek.myscanloginserver;

import android.app.Application;

/**
 * Created by louisgeek on 2018/8/24.
 */
public class MyApplication extends Application {
    private static MyApplication sInstance;

    @Override
    public void onCreate() {
        super.onCreate();
        sInstance = this;
    }

    public static MyApplication getInstance() {
        return sInstance;
    }
}
