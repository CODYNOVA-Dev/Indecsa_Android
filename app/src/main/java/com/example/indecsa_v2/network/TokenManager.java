package com.example.indecsa_v2.network;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Almacenamiento local de la sesión del usuario:
 *   - {@code jwt_token}  — JWT emitido por el backend en POST /empleados/login
 *                          (firma RS256/HS256, payload con idEmpleado + rol).
 *   - {@code nombre_rol} — "ADMIN" o "CAPITAL_HUMANO", usado por MainActivity
 *                          para autologin al panel correspondiente.
 *
 * <h3>Duración de la sesión</h3>
 * El TTL del token lo decide el backend (decisión F5.3: ~2h). Cuando expira,
 * el siguiente request devuelve 401 y {@link AuthInterceptor} limpia la
 * sesión con {@link #clearSession()} y redirige al login. El cliente NO
 * intenta refresh automático — el usuario re-loguea con correo+contraseña.
 *
 * Si la fricción de re-login cada 2h se vuelve molesta, considerar agregar
 * refresh-token en una v2 (backend emite 2 tokens; cliente refresca el JWT
 * antes de expirar sin pedir credenciales).
 *
 * <h3>Persistencia</h3>
 * SharedPreferences en MODE_PRIVATE. Los datos sobreviven a:
 *   - cierre de la app
 *   - reinicios del SO
 *   - rotación de pantalla
 *
 * Se borran solo cuando: (a) el usuario hace logout manual desde
 * Panel_Inicial_*, (b) AuthInterceptor recibe 401 fuera de login, o
 * (c) el usuario reinstala / limpia datos desde Settings.
 */
public class TokenManager {

    private static final String PREFS_NAME = "indecsa_prefs";
    private static final String KEY_TOKEN  = "jwt_token";
    private static final String KEY_ROL    = "nombre_rol";

    private final SharedPreferences prefs;

    public TokenManager(Context context) {
        prefs = context.getApplicationContext()
                .getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }

    public void saveToken(String token) {
        prefs.edit().putString(KEY_TOKEN, token).apply();
    }

    public String getToken() {
        return prefs.getString(KEY_TOKEN, null);
    }

    public void clearToken() {
        prefs.edit().remove(KEY_TOKEN).apply();
    }

    public void saveRole(String rol) {
        prefs.edit().putString(KEY_ROL, rol).apply();
    }

    public String getRole() {
        return prefs.getString(KEY_ROL, null);
    }

    public void clearSession() {
        prefs.edit()
                .remove(KEY_TOKEN)
                .remove(KEY_ROL)
                .apply();
    }
}
