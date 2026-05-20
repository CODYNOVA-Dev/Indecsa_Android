package com.example.indecsa_v2.network;

import com.example.indecsa_v2.models.AsignacionProyectoContratistaDto;
import com.example.indecsa_v2.models.AsignacionTrabajadorProyectoDto;
import com.example.indecsa_v2.models.AvancePartidaDto;
import com.example.indecsa_v2.models.Contratista;
import com.example.indecsa_v2.models.CuadrillaDto;
import com.example.indecsa_v2.models.Domicilio;
import com.example.indecsa_v2.models.EmpleadoDto;
import com.example.indecsa_v2.models.Estado;
import com.example.indecsa_v2.models.EstandarRendimientoDto;
import com.example.indecsa_v2.models.LoginRequestDto;
import com.example.indecsa_v2.models.LoginResponseDto;
import com.example.indecsa_v2.models.ProyectoDto;
import com.example.indecsa_v2.models.RegistroHorasDto;
import com.example.indecsa_v2.models.RegistroMigratorio;
import com.example.indecsa_v2.models.RendimientoIndicadorDto;
import com.example.indecsa_v2.models.Rol;
import com.example.indecsa_v2.models.TrabajadorDto;

import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;
import retrofit2.http.Streaming;

/**
 * Rutas alineadas al backend Spring Boot en demo/.
 * BASE_URL (en RetrofitClient) = https://.../api/  → todas las rutas son relativas.
 *
 * - Login real: POST auth/login (valida correo + contrasena contra Empleado).
 * - Asignaciones, cuadrillas, avances y registros usan el patrón list-with-query
 *   en lugar de subrutas por proyecto/trabajador.
 *
 * Endpoints SIN equivalente real en el backend (devuelven 404 al llamarlos):
 *   - PATCH /{recurso}/{id}/estado            (no existe; usar PUT con el objeto completo)
 *   - GET   /trabajadores/estado/{estado}     (filtrar en cliente)
 *   - GET   /trabajadores/especialidad/{...}  (filtrar en cliente)
 *   - GET   /contratistas/estado/{estado}     (filtrar en cliente)
 *   - GET   /proyectos/estatus/{estatus}      (filtrar en cliente)
 *   - GET   /proyectos/municipio/{municipio}  (filtrar en cliente)
 *   - GET   /rendimiento/...                  (no implementado en backend)
 *   - GET   /reportes/...                     (no implementado en backend)
 * Las que la UI sigue invocando se conservan declaradas, pero responderán 404.
 */
public interface ApiService {

    // ==================== AUTH ====================

    /**
     * POST v1/empleados/login
     * Body: { "correoEmpleado": "...", "contrasena": "..." }
     * 200: LoginResponseDto
     * 401: credenciales incorrectas
     */
    @POST("v1/empleados/login")
    Call<LoginResponseDto> login(@Body LoginRequestDto request);

    // ==================== EMPLEADOS ====================

    @GET("empleados")
    Call<List<EmpleadoDto>> getAllEmpleados();

    @GET("empleados/{id}")
    Call<EmpleadoDto> getEmpleadoById(@Path("id") Integer id);

    @POST("empleados")
    Call<EmpleadoDto> createEmpleado(@Body EmpleadoDto empleado);

    @PUT("empleados/{id}")
    Call<EmpleadoDto> updateEmpleado(@Path("id") Integer id, @Body EmpleadoDto empleado);

    @DELETE("empleados/{id}")
    Call<Void> deleteEmpleado(@Path("id") Integer id);

    // ==================== TRABAJADORES ====================

    @GET("trabajadores")
    Call<List<TrabajadorDto>> getAllTrabajadores();

    @GET("trabajadores/{id}")
    Call<TrabajadorDto> getTrabajadorById(@Path("id") Integer id);

    @POST("trabajadores")
    Call<TrabajadorDto> createTrabajador(@Body TrabajadorDto trabajador);

    @PUT("trabajadores/{id}")
    Call<TrabajadorDto> updateTrabajador(@Path("id") Integer id, @Body TrabajadorDto trabajador);

    @DELETE("trabajadores/{id}")
    Call<Void> deleteTrabajador(@Path("id") Integer id);

    // ==================== CONTRATISTAS ====================

    @GET("contratistas")
    Call<List<Contratista>> getAllContratistas();

    @GET("contratistas/{id}")
    Call<Contratista> getContratistaById(@Path("id") Integer id);

    @POST("contratistas")
    Call<Contratista> createContratista(@Body Contratista contratista);

    @PUT("contratistas/{id}")
    Call<Contratista> updateContratista(@Path("id") Integer id, @Body Contratista contratista);

    @DELETE("contratistas/{id}")
    Call<Void> deleteContratista(@Path("id") Integer id);

    // ==================== PROYECTOS ====================

    @GET("proyectos")
    Call<List<ProyectoDto>> getAllProyectos();

    @GET("proyectos/{id}")
    Call<ProyectoDto> getProyectoById(@Path("id") Integer id);

    @POST("proyectos")
    Call<ProyectoDto> createProyecto(@Body ProyectoDto proyectoDto);

    @PUT("proyectos/{id}")
    Call<ProyectoDto> updateProyecto(@Path("id") Integer id, @Body ProyectoDto proyectoDto);

    @DELETE("proyectos/{id}")
    Call<Void> deleteProyecto(@Path("id") Integer id);

    // ==================== ASIGNACIONES TRABAJADOR-PROYECTO ====================

    @GET("asignaciones-trabajador-proyecto")
    Call<List<AsignacionTrabajadorProyectoDto>> getAllAsignacionesTrabajadorProyecto(
            @Query("idProyecto") Integer idProyecto,
            @Query("idTrabajador") Integer idTrabajador);

    @GET("asignaciones-trabajador-proyecto/{id}")
    Call<AsignacionTrabajadorProyectoDto> getAsignacionTrabajadorById(@Path("id") Integer id);

    @POST("asignaciones-trabajador-proyecto")
    Call<AsignacionTrabajadorProyectoDto> createAsignacionTrabajador(@Body AsignacionTrabajadorProyectoDto dto);

    @PUT("asignaciones-trabajador-proyecto/{id}")
    Call<AsignacionTrabajadorProyectoDto> updateAsignacionTrabajador(@Path("id") Integer id,
                                                                    @Body AsignacionTrabajadorProyectoDto dto);

    @DELETE("asignaciones-trabajador-proyecto/{id}")
    Call<Void> deleteAsignacionTrabajador(@Path("id") Integer id);

    /** Lista asignaciones por proyecto (backend usa ?idProyecto=). */
    @GET("asignaciones-trabajador-proyecto")
    Call<List<AsignacionTrabajadorProyectoDto>> getAsignacionesByProyecto(@Query("idProyecto") Integer idProyecto);

    /** Lista asignaciones por trabajador (backend usa ?idTrabajador=). */
    @GET("asignaciones-trabajador-proyecto")
    Call<List<AsignacionTrabajadorProyectoDto>> getAsignacionesByTrabajador(@Query("idTrabajador") Integer idTrabajador);

    // ==================== ASIGNACIONES PROYECTO-CONTRATISTA ====================

    @GET("asignaciones-proyecto-contratista")
    Call<List<AsignacionProyectoContratistaDto>> getAllAsignacionesProyectoContratista(
            @Query("idProyecto") Integer idProyecto,
            @Query("idContratista") Integer idContratista);

    @GET("asignaciones-proyecto-contratista/{id}")
    Call<AsignacionProyectoContratistaDto> getAsignacionPcById(@Path("id") Integer id);

    @POST("asignaciones-proyecto-contratista")
    Call<AsignacionProyectoContratistaDto> createAsignacionPc(@Body AsignacionProyectoContratistaDto dto);

    @PUT("asignaciones-proyecto-contratista/{id}")
    Call<AsignacionProyectoContratistaDto> updateAsignacionPc(@Path("id") Integer id,
                                                              @Body AsignacionProyectoContratistaDto dto);

    @DELETE("asignaciones-proyecto-contratista/{id}")
    Call<Void> deleteAsignacionPc(@Path("id") Integer id);

    // ==================== CUADRILLAS ====================

    @GET("cuadrillas")
    Call<List<CuadrillaDto>> getAllCuadrillas(@Query("idProyecto") Integer idProyecto);

    @GET("cuadrillas/{id}")
    Call<CuadrillaDto> getCuadrillaById(@Path("id") Integer id);

    @POST("cuadrillas")
    Call<CuadrillaDto> createCuadrilla(@Body CuadrillaDto cuadrilla);

    @PUT("cuadrillas/{id}")
    Call<CuadrillaDto> updateCuadrilla(@Path("id") Integer id, @Body CuadrillaDto cuadrilla);

    @DELETE("cuadrillas/{id}")
    Call<Void> deleteCuadrilla(@Path("id") Integer id);

    /** Lista cuadrillas de un proyecto (backend usa ?idProyecto=). */
    @GET("cuadrillas")
    Call<List<CuadrillaDto>> getCuadrillasByProyecto(@Query("idProyecto") Integer idProyecto);

    // ==================== REGISTRO DE HORAS ====================

    @GET("registros-horas")
    Call<List<RegistroHorasDto>> getAllRegistrosHoras(@Query("idAsignacionTp") Integer idAsignacionTp);

    @GET("registros-horas/{id}")
    Call<RegistroHorasDto> getRegistroHorasById(@Path("id") Integer id);

    @POST("registros-horas")
    Call<RegistroHorasDto> createRegistroHoras(@Body RegistroHorasDto dto);

    @PUT("registros-horas/{id}")
    Call<RegistroHorasDto> updateRegistroHoras(@Path("id") Integer id, @Body RegistroHorasDto dto);

    @DELETE("registros-horas/{id}")
    Call<Void> deleteRegistroHoras(@Path("id") Integer id);

    /**
     * Registros de horas. Backend ignora idProyecto y fechas (los acepta como
     * query params desconocidos); se devuelven todos los registros y la UI
     * debe filtrar.
     */
    @GET("registros-horas")
    Call<List<RegistroHorasDto>> getRegistroHorasByProyecto(@Query("idProyecto") Integer idProyecto,
                                                            @Query("fechaInicio") String fechaInicio,
                                                            @Query("fechaFin") String fechaFin);

    @GET("registros-horas")
    Call<List<RegistroHorasDto>> getRegistroHorasByTrabajador(@Query("idTrabajador") Integer idTrabajador);

    // ==================== AVANCE DE PARTIDA ====================

    @GET("avances-partida")
    Call<List<AvancePartidaDto>> getAllAvances(@Query("idProyecto") Integer idProyecto);

    @GET("avances-partida/{id}")
    Call<AvancePartidaDto> getAvanceById(@Path("id") Integer id);

    @POST("avances-partida")
    Call<AvancePartidaDto> createAvancePartida(@Body AvancePartidaDto dto);

    @PUT("avances-partida/{id}")
    Call<AvancePartidaDto> updateAvancePartida(@Path("id") Integer id, @Body AvancePartidaDto dto);

    @DELETE("avances-partida/{id}")
    Call<Void> deleteAvancePartida(@Path("id") Integer id);

    /**
     * Avances de un proyecto. Backend no soporta filtros por fecha aquí
     * (los acepta como query params desconocidos); el filtrado debe hacerse
     * en cliente.
     */
    @GET("avances-partida")
    Call<List<AvancePartidaDto>> getAvancesByProyecto(@Query("idProyecto") Integer idProyecto,
                                                      @Query("fechaInicio") String fechaInicio,
                                                      @Query("fechaFin") String fechaFin);

    // ==================== ESTÁNDARES DE RENDIMIENTO ====================

    @GET("estandares-rendimiento")
    Call<List<EstandarRendimientoDto>> getAllEstandares();

    @GET("estandares-rendimiento/{id}")
    Call<EstandarRendimientoDto> getEstandarById(@Path("id") Integer id);

    @POST("estandares-rendimiento")
    Call<EstandarRendimientoDto> createEstandar(@Body EstandarRendimientoDto dto);

    @PUT("estandares-rendimiento/{id}")
    Call<EstandarRendimientoDto> updateEstandar(@Path("id") Integer id, @Body EstandarRendimientoDto dto);

    @DELETE("estandares-rendimiento/{id}")
    Call<Void> deleteEstandar(@Path("id") Integer id);

    // ==================== CATÁLOGOS ====================

    @GET("estados")
    Call<List<Estado>> getAllEstados();

    @POST("estados")
    Call<Estado> createEstado(@Body Estado estado);

    @PUT("estados/{id}")
    Call<Estado> updateEstado(@Path("id") Integer id, @Body Estado estado);

    @DELETE("estados/{id}")
    Call<Void> deleteEstado(@Path("id") Integer id);

    @GET("domicilios")
    Call<List<Domicilio>> getAllDomicilios();

    @GET("domicilios/{id}")
    Call<Domicilio> getDomicilioById(@Path("id") Integer id);

    @POST("domicilios")
    Call<Domicilio> createDomicilio(@Body Domicilio domicilio);

    @PUT("domicilios/{id}")
    Call<Domicilio> updateDomicilio(@Path("id") Integer id, @Body Domicilio domicilio);

    @DELETE("domicilios/{id}")
    Call<Void> deleteDomicilio(@Path("id") Integer id);

    @GET("roles")
    Call<List<Rol>> getAllRoles();

    @POST("roles")
    Call<Rol> createRol(@Body Rol rol);

    @PUT("roles/{id}")
    Call<Rol> updateRol(@Path("id") Integer id, @Body Rol rol);

    @DELETE("roles/{id}")
    Call<Void> deleteRol(@Path("id") Integer id);

    @GET("registros-migratorios")
    Call<List<RegistroMigratorio>> getAllRegistrosMigratorios();

    @POST("registros-migratorios")
    Call<RegistroMigratorio> createRegistroMigratorio(@Body RegistroMigratorio registro);

    @PUT("registros-migratorios/{id}")
    Call<RegistroMigratorio> updateRegistroMigratorio(@Path("id") Integer id, @Body RegistroMigratorio registro);

    @DELETE("registros-migratorios/{id}")
    Call<Void> deleteRegistroMigratorio(@Path("id") Integer id);

    // ==================== INDICADORES DE RENDIMIENTO ====================

    @GET("rendimiento/trabajador/{idTrabajador}")
    Call<List<RendimientoIndicadorDto>> getRendimientoTrabajador(
            @Path("idTrabajador") Integer idTrabajador,
            @Query("fechaInicio") String fechaInicio,
            @Query("fechaFin")    String fechaFin);

    @GET("rendimiento/proyecto/{idProyecto}")
    Call<List<RendimientoIndicadorDto>> getRendimientoProyecto(
            @Path("idProyecto") Integer idProyecto,
            @Query("fechaInicio") String fechaInicio,
            @Query("fechaFin")    String fechaFin);

    // ==================== REPORTES PDF ====================

    @Streaming
    @GET("reportes/rendimiento/trabajador/{id}")
    Call<ResponseBody> descargarRendimientoTrabajador(
            @Path("id") Integer id,
            @Query("fechaInicio") String fechaInicio,
            @Query("fechaFin")    String fechaFin);

    @Streaming
    @GET("reportes/horas/proyecto/{id}")
    Call<ResponseBody> descargarHorasProyecto(
            @Path("id") Integer id,
            @Query("fechaInicio") String fechaInicio,
            @Query("fechaFin")    String fechaFin);

    @Streaming
    @GET("reportes/avance/proyecto/{id}")
    Call<ResponseBody> descargarAvanceObra(
            @Path("id") Integer id,
            @Query("fechaInicio") String fechaInicio,
            @Query("fechaFin")    String fechaFin);
}
