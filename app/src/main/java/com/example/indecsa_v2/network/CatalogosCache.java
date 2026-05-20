package com.example.indecsa_v2.network;

import androidx.annotation.NonNull;

import com.example.indecsa_v2.models.Domicilio;
import com.example.indecsa_v2.models.Estado;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Cache simple en memoria para los catálogos del backend.
 *
 * Uso típico:
 *   CatalogosCache.getEstados(estados -> { ...llenar spinner... });
 *
 * El primer acceso dispara una llamada HTTP; las siguientes devuelven el
 * cache. Se puede invalidar con clear().
 */
public final class CatalogosCache {

    private CatalogosCache() {}

    public interface Listener<T> {
        void onResult(List<T> items);
    }

    private static List<Estado> estados;
    private static List<Domicilio> domicilios;

    public static void clear() {
        estados = null;
        domicilios = null;
    }

    public static void getEstados(@NonNull Listener<Estado> cb) {
        if (estados != null) {
            cb.onResult(estados);
            return;
        }
        RetrofitClient.getApiService().getAllEstados().enqueue(new Callback<List<Estado>>() {
            @Override public void onResponse(@NonNull Call<List<Estado>> call, @NonNull Response<List<Estado>> r) {
                estados = r.isSuccessful() && r.body() != null ? r.body() : new ArrayList<>();
                cb.onResult(estados);
            }
            @Override public void onFailure(@NonNull Call<List<Estado>> call, @NonNull Throwable t) {
                cb.onResult(Collections.emptyList());
            }
        });
    }

    public static void getDomicilios(@NonNull Listener<Domicilio> cb) {
        if (domicilios != null) {
            cb.onResult(domicilios);
            return;
        }
        RetrofitClient.getApiService().getAllDomicilios().enqueue(new Callback<List<Domicilio>>() {
            @Override public void onResponse(@NonNull Call<List<Domicilio>> call, @NonNull Response<List<Domicilio>> r) {
                domicilios = r.isSuccessful() && r.body() != null ? r.body() : new ArrayList<>();
                cb.onResult(domicilios);
            }
            @Override public void onFailure(@NonNull Call<List<Domicilio>> call, @NonNull Throwable t) {
                cb.onResult(Collections.emptyList());
            }
        });
    }

    /** Útil tras crear un domicilio nuevo desde la UI. */
    public static void addDomicilio(Domicilio d) {
        if (domicilios == null) domicilios = new ArrayList<>();
        domicilios.add(d);
    }
}
