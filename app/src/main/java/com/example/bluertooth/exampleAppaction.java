package com.example.bluertooth;

import android.app.Application;

import cn.jpush.android.api.JPushInterface;

public class exampleAppaction extends Application {

    public void onCreate() {
        super.onCreate();
        JPushInterface.setDebugMode(true);
        JPushInterface.init(this);
    }
}
