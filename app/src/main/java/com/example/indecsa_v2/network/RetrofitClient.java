package com.example.indecsa_v2.network;

import android.content.Context;

import com.example.indecsa_v2.BuildConfig;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {

    private static final String BASE_URL = BuildConfig.BASE_URL;

    private static Retrofit retrofit;
    private static ApiService apiService;
    private static TokenManager tokenManager;

    public static void init(Context context) {
        tokenManager = new TokenManager(context);
        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(new AuthInterceptor(tokenManager))
                .connectTimeout(40, TimeUnit.SECONDS)   // 40s para aguantar cold-start de Railway
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
