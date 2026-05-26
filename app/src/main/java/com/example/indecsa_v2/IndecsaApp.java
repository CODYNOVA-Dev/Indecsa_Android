package com.example.indecsa_v2;

import android.app.Application;

import com.example.indecsa_v2.network.RetrofitClient;

public class IndecsaApp extends Application {

    private static IndecsaApp instance;

    public static IndecsaApp get() {
        return instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        RetrofitClient.init(this);
    }
}
