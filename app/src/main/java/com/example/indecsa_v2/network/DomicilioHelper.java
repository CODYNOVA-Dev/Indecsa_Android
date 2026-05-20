package com.example.indecsa_v2.network;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.indecsa_v2.models.Domicilio;
import com.example.indecsa_v2.models.Estado;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Helper para crear un Domicilio inline desde los diálogos de Agregar
 * Trabajador / Agregar Proyecto. Evita pedir al usuario abrir otra pantalla.
 */
public final class DomicilioHelper {

    private DomicilioHelper() {}

    public interface Callback {
        void onCreated(@NonNull Domicilio domicilio);
        void onError(@NonNull String msg);
    }

    /**
     * Crea un Domicilio nuevo en backend a partir de los datos mínimos.
     * Si `idEstado` es null, falla.
     */
    public static void crear(@Nullable String calle,
                             @Nullable String numExt,
                             @Nullable String colonia,
                             @Nullable Integer codPost,
                             @Nullable String munAlc,
                             @Nullable Integer idEstado,
                             @NonNull Callback cb) {

        if (idEstado == null) {
            cb.onError("Falta seleccionar el estado geográfico");
            return;
        }

        Domicilio d = new Domicilio();
        d.setCalle(calle != null && !calle.isEmpty() ? calle : "Sin especificar");
        d.setNumExt(numExt);
        d.setColonia(colonia);
        d.setCodPost(codPost);
        d.setMunAlc(munAlc);
        d.setEstado(new Estado(idEstado));

        RetrofitClient.getApiService().createDomicilio(d).enqueue(new retrofit2.Callback<Domicilio>() {
            @Override public void onResponse(@NonNull Call<Domicilio> call, @NonNull Response<Domicilio> r) {
                if (r.isSuccessful() && r.body() != null) {
                    Domicilio created = r.body();
                    CatalogosCache.addDomicilio(created);
                    cb.onCreated(created);
                } else {
                    cb.onError("No se pudo crear el domicilio (HTTP " + r.code() + ")");
                }
            }
            @Override public void onFailure(@NonNull Call<Domicilio> call, @NonNull Throwable t) {
                cb.onError("Sin conexión al crear domicilio");
            }
        });
    }
}
