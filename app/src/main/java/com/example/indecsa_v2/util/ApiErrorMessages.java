package com.example.indecsa_v2.util;

import androidx.annotation.StringRes;

import com.example.indecsa_v2.R;

import java.io.IOException;

/**
 * Mapea códigos HTTP y Throwables de Retrofit a strings de error
 * user-friendly en strings.xml. Centraliza para que el usuario nunca vea
 * "Error al cargar (500)" sino "El servidor tuvo un problema. Intentá
 * de nuevo en un momento."
 *
 * Uso típico desde un callback:
 * <pre>
 *   public void onResponse(Call&lt;T&gt; c, Response&lt;T&gt; r) {
 *       if (!isAdded()) return;
 *       if (!r.isSuccessful() || r.body() == null) {
 *           Toast.makeText(getContext(),
 *                   ApiErrorMessages.forCode(r.code()),
 *                   Toast.LENGTH_SHORT).show();
 *           return;
 *       }
 *       // ... usar r.body()
 *   }
 *
 *   public void onFailure(Call&lt;T&gt; c, Throwable t) {
 *       if (!isAdded()) return;
 *       Toast.makeText(getContext(),
 *               ApiErrorMessages.forThrowable(t),
 *               Toast.LENGTH_SHORT).show();
 *   }
 * </pre>
 */
public final class ApiErrorMessages {

    private ApiErrorMessages() {}

    /** Mapea un código HTTP a un mensaje legible. */
    @StringRes
    public static int forCode(int code) {
        if (code == 401) return R.string.api_error_401;
        if (code == 403) return R.string.api_error_403;
        if (code == 404) return R.string.api_error_404;
        if (code >= 500) return R.string.api_error_5xx;
        if (code >= 400) return R.string.api_error_4xx;
        return R.string.api_error_unknown;
    }

    /**
     * Mapea una excepción de transporte (Retrofit onFailure) a mensaje legible.
     * IOException cubre timeouts, DNS, sin internet, TLS, etc.
     */
    @StringRes
    public static int forThrowable(Throwable t) {
        if (t instanceof IOException) return R.string.api_error_network;
        return R.string.api_error_unknown;
    }
}
