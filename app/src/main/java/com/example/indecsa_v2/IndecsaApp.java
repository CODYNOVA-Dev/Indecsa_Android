package com.example.indecsa_v2;

import android.app.Application;

import com.example.indecsa_v2.network.RetrofitClient;

/**
 * Inicializa RetrofitClient una sola vez, antes de cualquier Activity.
 * Antes la inicialización vivía en MainActivity, lo que provocaba NPE si
 * el SO restauraba una activity interna (p.ej. tras kill por memoria)
 * sin pasar por el splash.
 */
public class IndecsaApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        RetrofitClient.init(this);
    }
}
