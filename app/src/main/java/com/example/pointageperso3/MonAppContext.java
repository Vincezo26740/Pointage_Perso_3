package com.example.pointageperso3;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;

public class MonAppContext extends Application {
    @SuppressLint("StaticFieldLeak")
    public static Context context;

    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
    }

}
