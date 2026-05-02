package com.example.indecsa_v2.network;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public class AuthInterceptor implements Interceptor {

    private final TokenManager tokenManager;

    public AuthInterceptor(TokenManager tokenManager) {
        this.tokenManager = tokenManager;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        String token = tokenManager.getToken();
        Request original = chain.request();

        if (token == null) {
            return chain.proceed(original);
        }

        Request request = original.newBuilder()
                .header("Authorization", "Bearer " + token)
                .build();

        return chain.proceed(request);
    }
}
