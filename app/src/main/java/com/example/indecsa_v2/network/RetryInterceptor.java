package com.example.indecsa_v2.network;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Reintenta hasta 2 veces (total: 3 intentos) ante respuestas 5xx, con backoff
 * exponencial 500ms → 1500ms. Solo se aplica a métodos idempotentes (GET/HEAD)
 * para no duplicar efectos de POST/PUT/DELETE/PATCH.
 *
 * Si el servidor responde no-5xx (incluyendo 4xx o éxito), se devuelve esa
 * respuesta sin reintentar.
 */
public class RetryInterceptor implements Interceptor {

    private static final int    MAX_RETRIES   = 2;
    private static final long   BASE_DELAY_MS = 500L;
    private static final double MULTIPLIER    = 3.0;

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();
        String method = request.method();
        boolean idempotent = "GET".equals(method) || "HEAD".equals(method);

        Response response = chain.proceed(request);

        if (!idempotent) return response;

        int retry = 0;
        while (response.code() >= 500 && retry < MAX_RETRIES) {
            response.close();
            try {
                long delay = (long) (BASE_DELAY_MS * Math.pow(MULTIPLIER, retry));
                Thread.sleep(delay);
            } catch (InterruptedException ie) {
                Thread.currentThread().interrupt();
                break;
            }
            retry++;
            response = chain.proceed(request);
        }

        return response;
    }
}
