package com.example.indecsa_v2.network;

import android.content.Context;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {

    // ⬇ Cambia según dónde corra el backend:
    // Emulador → servidor local:
    //private static final String BASE_URL = "http://10.0.2.2:8080/api/";
    // Celular físico / Railway:
    private static final String BASE_URL = "https://indecsaspringboot-production.up.railway.app/api/";

    private static Retrofit retrofit;
    private static ApiService apiService;
    private static TokenManager tokenManager;

    public static void init(Context context) {
        tokenManager = new TokenManager(context);

        HttpLoggingInterceptor logger = new HttpLoggingInterceptor();
        logger.setLevel(HttpLoggingInterceptor.Level.BODY);

        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(new AuthInterceptor(tokenManager))
                .addInterceptor(logger)
                .connectTimeout(40, TimeUnit.SECONDS)
                .readTimeout(60, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .build();

        retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        apiService = retrofit.create(ApiService.class);
    }

    public static TokenManager getTokenManager() {
        return tokenManager;
    }

    public static ApiService getApiService() {
        return apiService;
    }
}
