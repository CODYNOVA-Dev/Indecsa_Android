package com.example.indecsa_v2;

import android.app.Application;

import com.example.indecsa_v2.network.RetrofitClient;

/**
 * Inicializa RetrofitClient una sola vez, antes de cualquier Activity.
 * Expone una instancia estática para que AuthInterceptor pueda lanzar
 * CorreoLoginActivity con CLEAR_TASK cuando llega un 401 desde cualquier
 * request en background.
 */
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
