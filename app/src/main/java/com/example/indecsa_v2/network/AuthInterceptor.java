package com.example.indecsa_v2.network;

import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;

import com.example.indecsa_v2.IndecsaApp;
import com.example.indecsa_v2.login.CorreoLoginActivity;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicBoolean;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public class AuthInterceptor implements Interceptor {

    private final TokenManager tokenManager;

    /**
     * Evita disparar múltiples Toasts / startActivity cuando llegan varios 401
     * en paralelo (p.ej. al cargar tabs simultáneamente con token expirado).
     */
    private static final AtomicBoolean redirigiendo = new AtomicBoolean(false);

    public AuthInterceptor(TokenManager tokenManager) {
        this.tokenManager = tokenManager;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        String token  = tokenManager.getToken();
        Request original = chain.request();
        String path  = original.url().encodedPath();
        boolean esLogin = path.endsWith("/auth/login") || path.endsWith("/empleados/login");

        Request request = (token == null || esLogin)
                ? original
                : original.newBuilder()
                        .header("Authorization", "Bearer " + token)
                        .build();

        Response response = chain.proceed(request);

        if (response.code() == 401 && !esLogin) {
            tokenManager.clearSession();
            forzarVolverALogin();
        }

        return response;
    }

    private void forzarVolverALogin() {
        // CAS: solo el primer 401 dispara el redirect; los demás caen al else.
        if (!redirigiendo.compareAndSet(false, true)) return;

        IndecsaApp app = IndecsaApp.get();
        if (app == null) {
            redirigiendo.set(false);
            return;
        }

        Handler main = new Handler(Looper.getMainLooper());
        main.post(() -> {
            Toast.makeText(app,
                    "Sesión expirada. Inicia sesión de nuevo.",
                    Toast.LENGTH_LONG).show();

            Intent intent = new Intent(app, CorreoLoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            app.startActivity(intent);
        });

        // Libera el flag después de un rato para que un futuro 401 vuelva a funcionar.
        main.postDelayed(() -> redirigiendo.set(false), 5000);
    }
}
