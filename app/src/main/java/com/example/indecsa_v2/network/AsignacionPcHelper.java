package com.example.indecsa_v2.network;

import androidx.annotation.NonNull;

import com.example.indecsa_v2.models.AsignacionProyectoContratistaDto;
import com.example.indecsa_v2.models.Contratista;
import com.example.indecsa_v2.models.ProyectoDto;

import java.time.LocalDate;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Helper para resolver el AsignacionProyectoContratista que ata un proyecto
 * a un contratista (contrato marco). Toda AsignacionTrabajadorProyecto
 * requiere un idAsignacionPc en el backend.
 *
 * Estrategia:
 *   1. GET asignaciones/proyecto-contratista/proyecto/{idProyecto}
 *      (el backend no soporta filtrar por proyecto+contratista en un solo
 *      request; filtramos por contratista en cliente sobre el resultado)
 *   2. Si hay alguno con estatus ACTIVO o VIGENTE → reutilizar el más reciente
 *   3. Si no → crear uno nuevo con personalAsignado estimado y estatus VIGENTE
 */
public final class AsignacionPcHelper {

    private AsignacionPcHelper() {}

    public interface Callback {
        void onResolved(@NonNull AsignacionProyectoContratistaDto dto);
        void onError(@NonNull String msg);
    }

    public static void obtenerOCrear(int idProyecto,
                                     int idContratista,
                                     int personalEstimado,
                                     @NonNull Callback cb) {

        RetrofitClient.getApiService()
                .getAsignacionesPcByProyecto(idProyecto)
                .enqueue(new retrofit2.Callback<List<AsignacionProyectoContratistaDto>>() {
                    @Override
                    public void onResponse(@NonNull Call<List<AsignacionProyectoContratistaDto>> call,
                                           @NonNull Response<List<AsignacionProyectoContratistaDto>> r) {
                        if (r.isSuccessful() && r.body() != null) {
                            Integer targetContratista = idContratista;
                            for (AsignacionProyectoContratistaDto a : r.body()) {
                                // Filtrar client-side por contratista (backend solo filtra por proyecto).
                                // Usamos .equals() en vez de != para no depender de auto-unboxing
                                // y blindar contra futuros refactors de int → Integer.
                                if (a.getIdContratista() == null
                                        || !a.getIdContratista().equals(targetContratista)) {
                                    continue;
                                }
                                String est = a.getEstatusContrato();
                                if ("ACTIVO".equals(est) || "VIGENTE".equals(est)) {
                                    cb.onResolved(a);
                                    return;
                                }
                            }
                            crearNuevo(idProyecto, idContratista, personalEstimado, cb);
                        } else if (r.code() == 404) {
                            crearNuevo(idProyecto, idContratista, personalEstimado, cb);
                        } else {
                            cb.onError("No se pudo verificar el contrato (HTTP " + r.code() + ")");
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<List<AsignacionProyectoContratistaDto>> call,
                                          @NonNull Throwable t) {
                        cb.onError("Sin conexión al verificar contrato");
                    }
                });
    }

    private static void crearNuevo(int idProyecto, int idContratista,
                                   int personalEstimado, @NonNull Callback cb) {
        AsignacionProyectoContratistaDto nuevo = new AsignacionProyectoContratistaDto();
        nuevo.setProyecto(new ProyectoDto(idProyecto));
        nuevo.setContratista(new Contratista(idContratista));
        nuevo.setFechaInicio(LocalDate.now().toString());
        nuevo.setPersonalAsignado(Math.max(1, personalEstimado));
        nuevo.setEstatusContrato("VIGENTE");

        RetrofitClient.getApiService().createAsignacionPc(nuevo).enqueue(
                new retrofit2.Callback<AsignacionProyectoContratistaDto>() {
                    @Override
                    public void onResponse(@NonNull Call<AsignacionProyectoContratistaDto> call,
                                           @NonNull Response<AsignacionProyectoContratistaDto> r) {
                        if (r.isSuccessful() && r.body() != null) {
                            cb.onResolved(r.body());
                        } else {
                            cb.onError("No se pudo crear el contrato (HTTP " + r.code() + ")");
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<AsignacionProyectoContratistaDto> call,
                                          @NonNull Throwable t) {
                        cb.onError("Sin conexión al crear contrato");
                    }
                });
    }
}
