package com.example.nicetry;

import android.app.Application;

public class MyApplication extends Application {
    public static final String BACKEND_BASE_URL = "http://192.168.1.16:3000/";
    public static String user = "";

    public void onCreate() {
        super.onCreate();
    }
}