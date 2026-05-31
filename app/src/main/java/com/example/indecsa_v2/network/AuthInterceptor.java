package com.example.indecsa_v2.network;

import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;

import com.example.indecsa_v2.IndecsaApp;
import com.example.indecsa_v2.R;
import com.example.indecsa_v2.login.CorreoLoginActivity;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicBoolean;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Manejo global de auth en la capa de red. Política definida en F5.3:
 *
 * <h3>Códigos que maneja este interceptor</h3>
 * <ul>
 *   <li><b>401 Unauthorized</b> (token inválido, expirado o ausente, excepto
 *       en endpoints de login): limpia la sesión local y redirige al login
 *       con CLEAR_TASK. Es siempre un error de sesión, nunca de permisos.</li>
 *   <li><b>403 Forbidden</b>: NO se maneja acá. El interceptor deja propagar
 *       el response al caller, porque 403 significa "el JWT es válido pero
 *       tu rol no puede hacer esta acción" — el caller sabe el contexto
 *       (qué intentaba hacer) y muestra el mensaje específico vía
 *       {@link com.example.indecsa_v2.util.ApiErrorMessages#forCode(int)},
 *       que devuelve la string "Esta acción requiere permisos de
 *       administrador". Cerrar sesión en 403 sería UX horrible — el usuario
 *       sigue logueado, solo perdió una acción puntual.</li>
 *   <li><b>5xx / IOException</b>: tampoco se manejan acá. Cada caller los
 *       muestra como Toast vía ApiErrorMessages.</li>
 * </ul>
 *
 * <h3>Matriz de roles (BE5)</h3>
 * Ver Javadoc del header de {@link ApiService} para qué puede cada rol.
 */
public class AuthInterceptor implements Interceptor {

    private final TokenManager tokenManager;

    // Evita disparar múltiples Toasts / startActivity cuando llegan varios 401
    // en paralelo (p. ej. tabs cargando con token expirado).
    private static final AtomicBoolean redirigiendo = new AtomicBoolean(false);

    public AuthInterceptor(TokenManager tokenManager) {
        this.tokenManager = tokenManager;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        String token = tokenManager.getToken();
        Request original = chain.request();
        String path = original.url().encodedPath();
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
        if (!redirigiendo.compareAndSet(false, true)) return;

        IndecsaApp app = IndecsaApp.get();
        if (app == null) {
            redirigiendo.set(false);
            return;
        }

        Handler main = new Handler(Looper.getMainLooper());
        main.post(() -> {
            Toast.makeText(app, R.string.sesion_expirada, Toast.LENGTH_LONG).show();

            Intent intent = new Intent(app, CorreoLoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            app.startActivity(intent);
        });

        main.postDelayed(() -> redirigiendo.set(false), 5000);
    }
}
