package com.example.indecsa_v2.network;

import android.content.Context;
import android.content.SharedPreferences;

public class TokenManager {

    private static final String PREFS_NAME = "indecsa_prefs";
    private static final String KEY_TOKEN  = "jwt_token";
    private static final String KEY_ROL    = "nombre_rol";

    private final SharedPreferences prefs;

    public TokenManager(Context context) {
        prefs = context.getApplicationContext()
                .getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }

    // ─── Token ───────────────────────────────────────────────────────────────

    public void saveToken(String token) {
        prefs.edit().putString(KEY_TOKEN, token).apply();
    }

    public String getToken() {
        return prefs.getString(KEY_TOKEN, null);
    }

    public void clearToken() {
        prefs.edit().remove(KEY_TOKEN).apply();
    }

    // ─── Rol ─────────────────────────────────────────────────────────────────

    public void saveRole(String rol) {
        prefs.edit().putString(KEY_ROL, rol).apply();
    }

    public String getRole() {
        return prefs.getString(KEY_ROL, null);
    }

    // ─── Sesión completa ─────────────────────────────────────────────────────

    /** Limpia token y rol. Úsalo al hacer logout o al recibir 401. */
    public void clearSession() {
        prefs.edit()
                .remove(KEY_TOKEN)
                .remove(KEY_ROL)
                .apply();
    }
}
