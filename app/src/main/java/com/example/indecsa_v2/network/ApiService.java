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
import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;
import retrofit2.http.Streaming;

/**
 * Rutas alineadas al backend Spring Boot real.
 * BASE_URL (en RetrofitClient) = https://.../api/  → todas las rutas son relativas.
 *
 * <h3>Convención del backend para filtros</h3>
 * El backend usa SUBRUTAS por atributo, no query params:
 *   GET /cuadrillas/proyecto/{idProyecto}        (no ?idProyecto=)
 *   GET /asignaciones/trabajador-proyecto/proyecto/{idProyecto}
 *   GET /registros-horas/asignacion/{idAsignacionTp}
 *   etc.
 *
 * <h3>Cambios de estado</h3>
 * Para mutar solo el campo de estado/estatus el backend tiene PATCH:
 *   PATCH /trabajadores/{id}/estado
 *   PATCH /contratistas/{id}/estado
 *   PATCH /proyectos/{id}/estatus
 *   PATCH /cuadrillas/{id}/estatus
 *   PATCH /asignaciones/{tipo}/{id}/estatus
 *   PATCH /registros-migratorios/{id}/activo
 * Actualmente la UI hace PUT con el objeto completo. Funciona pero es
 * pesado y susceptible a sobrescribir cambios concurrentes — migrar a PATCH
 * cuando se pueda (queda como deuda técnica del PR-H).
 *
 * <h3>Endpoints NO documentados en el contrato actual</h3>
 *   - GET /rendimiento/trabajador/{id}     y /rendimiento/proyecto/{id}
 *   - GET /reportes/rendimiento/...        y /reportes/horas/... y /reportes/avance/...
 * El cliente los sigue declarando porque la UI los invoca, pero no aparecen
 * en la spec del backend. Si responden 404 hay que decidir si:
 *   (a) backend va a agregarlos
 *   (b) cliente quita las tabs/dialogs que los usan (Tab_Admin_Reportes,
 *       Tab_*_PersonalObra para rendimiento, botones "Generar PDF" en detalle).
 *
 * <h3>Pagination & search</h3>
 * El backend NO soporta {@code Pageable} ni filtro genérico {@code ?search=}.
 * Toda lista grande se trae completa y se filtra client-side. Si el backend
 * agrega Pageable en el futuro, recuperar PageDto del historial (commit
 * previo a PR-F).
 *
 * <h3>Matriz de permisos por rol (definida en F5.3 / BE5)</h3>
 * El backend valida {@code Authorization: Bearer} en TODOS los endpoints y
 * autoriza por rol con {@code @PreAuthorize}. Cuando un endpoint protegido
 * recibe un JWT de un rol sin permiso, devuelve 403 y el cliente muestra
 * "Esta acción requiere permisos de administrador" (ver
 * {@link com.example.indecsa_v2.util.ApiErrorMessages}).
 *
 * <table>
 *   <caption>Permisos por rol</caption>
 *   <tr>
 *     <th>Recurso</th><th>ADMIN</th><th>CAPITAL_HUMANO</th>
 *   </tr>
 *   <tr><td>empleados</td><td>GET + POST + PUT + DELETE</td><td>GET</td></tr>
 *   <tr><td>trabajadores</td><td>GET + POST + PUT + DELETE + PATCH</td><td>GET</td></tr>
 *   <tr><td>contratistas</td><td>GET + POST + PUT + DELETE + PATCH</td><td>GET</td></tr>
 *   <tr><td>proyectos</td><td>GET + POST + PUT + DELETE + PATCH</td><td>GET</td></tr>
 *   <tr><td>cuadrillas</td><td>GET + POST + PUT + DELETE + PATCH</td><td>GET + POST + PUT + DELETE + PATCH</td></tr>
 *   <tr><td>asignaciones/*</td><td>GET + POST + PUT + DELETE + PATCH</td><td>GET + POST + PUT + DELETE + PATCH</td></tr>
 *   <tr><td>registros-horas</td><td>GET + POST + PUT + DELETE</td><td>GET + POST + PUT + DELETE</td></tr>
 *   <tr><td>avances-partida</td><td>GET + POST + PUT + DELETE</td><td>GET + POST + PUT + DELETE</td></tr>
 *   <tr><td>reportes / rendimiento</td><td>GET</td><td>GET</td></tr>
 *   <tr><td>estandares-rendimiento</td><td>GET + POST + PUT + DELETE</td><td>GET</td></tr>
 *   <tr><td>estados / roles / domicilios</td><td>GET + POST + PUT + DELETE</td><td>GET</td></tr>
 *   <tr><td>registros-migratorios</td><td>GET + POST + PUT + DELETE + PATCH</td><td>GET</td></tr>
 * </table>
 *
 * Resumen: <b>CAPITAL_HUMANO</b> es lectura + operación (asignar cuadrillas,
 * registrar horas/avances, generar reportes). <b>ADMIN</b> agrega gestión
 * de personal, contratistas, proyectos y catálogos.
 */
public interface ApiService {

    // ==================== AUTH ====================

    /**
     * POST empleados/login
     * Body: { "correoEmpleado": "...", "contrasena": "..." }
     * 200: LoginResponseDto
     * 401: credenciales incorrectas
     */
    @POST("empleados/login")
    Call<LoginResponseDto> login(@Body LoginRequestDto request);

    // ==================== EMPLEADOS ====================

    @GET("empleados")
    Call<List<EmpleadoDto>> getAllEmpleados();

    @GET("empleados/{id}")
    Call<EmpleadoDto> getEmpleadoById(@Path("id") Integer id);

    /** Empleados filtrados por rol. Backend: /rol/{idRol}. */
    @GET("empleados/rol/{idRol}")
    Call<List<EmpleadoDto>> getEmpleadosByRol(@Path("idRol") Integer idRol);

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

    /** Trabajadores por estado (ACTIVO, INACTIVO, BAJA, ...). Backend: /estado/{estado}. */
    @GET("trabajadores/estado/{estado}")
    Call<List<TrabajadorDto>> getTrabajadoresByEstado(@Path("estado") String estado);

    /** Trabajadores por especialidad. Backend: /especialidad/{especialidad}. */
    @GET("trabajadores/especialidad/{especialidad}")
    Call<List<TrabajadorDto>> getTrabajadoresByEspecialidad(@Path("especialidad") String especialidad);

    @POST("trabajadores")
    Call<TrabajadorDto> createTrabajador(@Body TrabajadorDto trabajador);

    @PUT("trabajadores/{id}")
    Call<TrabajadorDto> updateTrabajador(@Path("id") Integer id, @Body TrabajadorDto trabajador);

    /**
     * Cambio parcial de estado del trabajador (sin reenviar todo el objeto).
     * Body típico: {@code Map.of("estado", "ACTIVO")} — valores válidos:
     * DESCANSO, VACACIONES, INCAPACIDAD, ACTIVO, INACTIVO, BAJA, BOLETINADO.
     */
    @PATCH("trabajadores/{id}/estado")
    Call<TrabajadorDto> patchTrabajadorEstado(@Path("id") Integer id, @Body Map<String, Object> body);

    @DELETE("trabajadores/{id}")
    Call<Void> deleteTrabajador(@Path("id") Integer id);

    // ==================== CONTRATISTAS ====================

    @GET("contratistas")
    Call<List<Contratista>> getAllContratistas();

    @GET("contratistas/{id}")
    Call<Contratista> getContratistaById(@Path("id") Integer id);

    /** Contratistas por estado (ACTIVO, INACTIVO, SUSPENDIDO). Backend: /estado/{estado}. */
    @GET("contratistas/estado/{estado}")
    Call<List<Contratista>> getContratistasByEstado(@Path("estado") String estado);

    @POST("contratistas")
    Call<Contratista> createContratista(@Body Contratista contratista);

    @PUT("contratistas/{id}")
    Call<Contratista> updateContratista(@Path("id") Integer id, @Body Contratista contratista);

    /** Body: {@code Map.of("estado", "ACTIVO|INACTIVO|SUSPENDIDO")}. */
    @PATCH("contratistas/{id}/estado")
    Call<Contratista> patchContratistaEstado(@Path("id") Integer id, @Body Map<String, Object> body);

    @DELETE("contratistas/{id}")
    Call<Void> deleteContratista(@Path("id") Integer id);

    // ==================== PROYECTOS ====================

    @GET("proyectos")
    Call<List<ProyectoDto>> getAllProyectos();

    @GET("proyectos/{id}")
    Call<ProyectoDto> getProyectoById(@Path("id") Integer id);

    /** Proyectos por estatus (PLANEACION, EN_CURSO, FINALIZADO, ...). Backend: /estatus/{x}. */
    @GET("proyectos/estatus/{estatus}")
    Call<List<ProyectoDto>> getProyectosByEstatus(@Path("estatus") String estatus);

    /** Proyectos por cliente. Backend: /cliente/{cliente}. */
    @GET("proyectos/cliente/{cliente}")
    Call<List<ProyectoDto>> getProyectosByCliente(@Path("cliente") String cliente);

    @POST("proyectos")
    Call<ProyectoDto> createProyecto(@Body ProyectoDto proyectoDto);

    @PUT("proyectos/{id}")
    Call<ProyectoDto> updateProyecto(@Path("id") Integer id, @Body ProyectoDto proyectoDto);

    /** Body: {@code Map.of("estatus", "PLANEACION|EN_CURSO|PENDIENTE|FINALIZADO|CANCELADO")}. */
    @PATCH("proyectos/{id}/estatus")
    Call<ProyectoDto> patchProyectoEstatus(@Path("id") Integer id, @Body Map<String, Object> body);

    @DELETE("proyectos/{id}")
    Call<Void> deleteProyecto(@Path("id") Integer id);

    // ==================== ASIGNACIONES TRABAJADOR-PROYECTO ====================

    @GET("asignaciones/trabajador-proyecto")
    Call<List<AsignacionTrabajadorProyectoDto>> getAllAsignacionesTrabajadorProyecto();

    @GET("asignaciones/trabajador-proyecto/{id}")
    Call<AsignacionTrabajadorProyectoDto> getAsignacionTrabajadorById(@Path("id") Integer id);

    @POST("asignaciones/trabajador-proyecto")
    Call<AsignacionTrabajadorProyectoDto> createAsignacionTrabajador(@Body AsignacionTrabajadorProyectoDto dto);

    @PUT("asignaciones/trabajador-proyecto/{id}")
    Call<AsignacionTrabajadorProyectoDto> updateAsignacionTrabajador(@Path("id") Integer id,
                                                                    @Body AsignacionTrabajadorProyectoDto dto);

    /** Body: {@code Map.of("estatus", "ACTIVO|SUSPENDIDO|INCAPACIDAD|CANCELADO|VACACIONES|FINALIZADO")}. */
    @PATCH("asignaciones/trabajador-proyecto/{id}/estatus")
    Call<AsignacionTrabajadorProyectoDto> patchAsignacionTrabajadorEstatus(@Path("id") Integer id,
                                                                          @Body Map<String, Object> body);

    @DELETE("asignaciones/trabajador-proyecto/{id}")
    Call<Void> deleteAsignacionTrabajador(@Path("id") Integer id);

    /** Asignaciones de un proyecto. Backend: /proyecto/{idProyecto}. */
    @GET("asignaciones/trabajador-proyecto/proyecto/{idProyecto}")
    Call<List<AsignacionTrabajadorProyectoDto>> getAsignacionesByProyecto(@Path("idProyecto") Integer idProyecto);

    /** Asignaciones de un trabajador. Backend: /trabajador/{idTrabajador}. */
    @GET("asignaciones/trabajador-proyecto/trabajador/{idTrabajador}")
    Call<List<AsignacionTrabajadorProyectoDto>> getAsignacionesByTrabajador(@Path("idTrabajador") Integer idTrabajador);

    /** Asignaciones de un contrato (idAsignacionPc). Backend: /contrato/{id}. */
    @GET("asignaciones/trabajador-proyecto/contrato/{idAsignacionPc}")
    Call<List<AsignacionTrabajadorProyectoDto>> getAsignacionesByContrato(@Path("idAsignacionPc") Integer idAsignacionPc);

    /** Asignaciones por estatus (ACTIVO, SUSPENDIDO, FINALIZADO, ...). */
    @GET("asignaciones/trabajador-proyecto/estatus/{estatus}")
    Call<List<AsignacionTrabajadorProyectoDto>> getAsignacionesByEstatus(@Path("estatus") String estatus);

    // ==================== ASIGNACIONES PROYECTO-CONTRATISTA ====================

    @GET("asignaciones/proyecto-contratista")
    Call<List<AsignacionProyectoContratistaDto>> getAllAsignacionesProyectoContratista();

    @GET("asignaciones/proyecto-contratista/{id}")
    Call<AsignacionProyectoContratistaDto> getAsignacionPcById(@Path("id") Integer id);

    @POST("asignaciones/proyecto-contratista")
    Call<AsignacionProyectoContratistaDto> createAsignacionPc(@Body AsignacionProyectoContratistaDto dto);

    @PUT("asignaciones/proyecto-contratista/{id}")
    Call<AsignacionProyectoContratistaDto> updateAsignacionPc(@Path("id") Integer id,
                                                              @Body AsignacionProyectoContratistaDto dto);

    /** Body: {@code Map.of("estatus", "ACTIVO|VIGENTE|SUSPENDIDO|FINALIZADO|CANCELADO")}. */
    @PATCH("asignaciones/proyecto-contratista/{id}/estatus")
    Call<AsignacionProyectoContratistaDto> patchAsignacionPcEstatus(@Path("id") Integer id,
                                                                   @Body Map<String, Object> body);

    @DELETE("asignaciones/proyecto-contratista/{id}")
    Call<Void> deleteAsignacionPc(@Path("id") Integer id);

    /** Asignaciones PC por proyecto. Backend: /proyecto/{idProyecto}. */
    @GET("asignaciones/proyecto-contratista/proyecto/{idProyecto}")
    Call<List<AsignacionProyectoContratistaDto>> getAsignacionesPcByProyecto(@Path("idProyecto") Integer idProyecto);

    /** Asignaciones PC por contratista. Backend: /contratista/{idContratista}. */
    @GET("asignaciones/proyecto-contratista/contratista/{idContratista}")
    Call<List<AsignacionProyectoContratistaDto>> getAsignacionesPcByContratista(@Path("idContratista") Integer idContratista);

    /** Asignaciones PC por estatus (ACTIVO, VIGENTE, SUSPENDIDO, ...). */
    @GET("asignaciones/proyecto-contratista/estatus/{estatus}")
    Call<List<AsignacionProyectoContratistaDto>> getAsignacionesPcByEstatus(@Path("estatus") String estatus);

    // ==================== CUADRILLAS ====================

    @GET("cuadrillas")
    Call<List<CuadrillaDto>> getAllCuadrillas();

    @GET("cuadrillas/{id}")
    Call<CuadrillaDto> getCuadrillaById(@Path("id") Integer id);

    @POST("cuadrillas")
    Call<CuadrillaDto> createCuadrilla(@Body CuadrillaDto cuadrilla);

    @PUT("cuadrillas/{id}")
    Call<CuadrillaDto> updateCuadrilla(@Path("id") Integer id, @Body CuadrillaDto cuadrilla);

    /** Body: {@code Map.of("estatus", "ACTIVO|INACTIVO")}. */
    @PATCH("cuadrillas/{id}/estatus")
    Call<CuadrillaDto> patchCuadrillaEstatus(@Path("id") Integer id, @Body Map<String, Object> body);

    @DELETE("cuadrillas/{id}")
    Call<Void> deleteCuadrilla(@Path("id") Integer id);

    /** Cuadrillas de un proyecto. Backend: /proyecto/{idProyecto}. */
    @GET("cuadrillas/proyecto/{idProyecto}")
    Call<List<CuadrillaDto>> getCuadrillasByProyecto(@Path("idProyecto") Integer idProyecto);

    /** Cuadrillas por estatus (ACTIVO, INACTIVO). */
    @GET("cuadrillas/estatus/{estatus}")
    Call<List<CuadrillaDto>> getCuadrillasByEstatus(@Path("estatus") String estatus);

    // ==================== REGISTRO DE HORAS ====================

    @GET("registros-horas")
    Call<List<RegistroHorasDto>> getAllRegistrosHoras();

    @GET("registros-horas/{id}")
    Call<RegistroHorasDto> getRegistroHorasById(@Path("id") Integer id);

    @POST("registros-horas")
    Call<RegistroHorasDto> createRegistroHoras(@Body RegistroHorasDto dto);

    @PUT("registros-horas/{id}")
    Call<RegistroHorasDto> updateRegistroHoras(@Path("id") Integer id, @Body RegistroHorasDto dto);

    @DELETE("registros-horas/{id}")
    Call<Void> deleteRegistroHoras(@Path("id") Integer id);

    /** Registros por asignación trabajador-proyecto. Backend: /asignacion/{id}. */
    @GET("registros-horas/asignacion/{idAsignacionTp}")
    Call<List<RegistroHorasDto>> getRegistrosHorasByAsignacion(@Path("idAsignacionTp") Integer idAsignacionTp);

    /** Registros por cuadrilla. Backend: /cuadrilla/{id}. */
    @GET("registros-horas/cuadrilla/{idCuadrilla}")
    Call<List<RegistroHorasDto>> getRegistrosHorasByCuadrilla(@Path("idCuadrilla") Integer idCuadrilla);

    /** Registros por fecha. Backend: /fecha/{fecha} (formato yyyy-MM-dd). */
    @GET("registros-horas/fecha/{fecha}")
    Call<List<RegistroHorasDto>> getRegistrosHorasByFecha(@Path("fecha") String fechaYmd);

    // NOTA: el backend NO expone filtro por proyecto ni por trabajador para
    // registros-horas. Si necesitás "registros de un proyecto", traete todos
    // con getAllRegistrosHoras() y filtrá client-side por r.getIdProyecto().
    // El método legacy getRegistroHorasByProyecto/ByTrabajador fue removido
    // porque pretendía existir y devolvía siempre la lista completa.

    // ==================== AVANCE DE PARTIDA ====================

    @GET("avances-partida")
    Call<List<AvancePartidaDto>> getAllAvances();

    @GET("avances-partida/{id}")
    Call<AvancePartidaDto> getAvanceById(@Path("id") Integer id);

    @POST("avances-partida")
    Call<AvancePartidaDto> createAvancePartida(@Body AvancePartidaDto dto);

    @PUT("avances-partida/{id}")
    Call<AvancePartidaDto> updateAvancePartida(@Path("id") Integer id, @Body AvancePartidaDto dto);

    @DELETE("avances-partida/{id}")
    Call<Void> deleteAvancePartida(@Path("id") Integer id);

    /** Avances de un proyecto. Backend: /proyecto/{idProyecto}. */
    @GET("avances-partida/proyecto/{idProyecto}")
    Call<List<AvancePartidaDto>> getAvancesByProyecto(@Path("idProyecto") Integer idProyecto);

    /** Avances de una cuadrilla. Backend: /cuadrilla/{idCuadrilla}. */
    @GET("avances-partida/cuadrilla/{idCuadrilla}")
    Call<List<AvancePartidaDto>> getAvancesByCuadrilla(@Path("idCuadrilla") Integer idCuadrilla);

    /** Avances por fecha. Backend: /fecha/{fecha} (formato yyyy-MM-dd). */
    @GET("avances-partida/fecha/{fecha}")
    Call<List<AvancePartidaDto>> getAvancesByFecha(@Path("fecha") String fechaYmd);

    // NOTA: el backend no expone filtro por rango de fechas en avances-partida.
    // Si necesitás un rango, traete por proyecto y filtrá client-side por fecha.

    // ==================== ESTÁNDARES DE RENDIMIENTO ====================

    @GET("estandares-rendimiento")
    Call<List<EstandarRendimientoDto>> getAllEstandares();

    @GET("estandares-rendimiento/{id}")
    Call<EstandarRendimientoDto> getEstandarById(@Path("id") Integer id);

    /**
     * Búsqueda de estándares. Backend expone el endpoint pero los query params
     * exactos no están en la spec — se pasa con @QueryMap genérico para que el
     * caller arme los que necesite (ej. {@code Map.of("texto", "albañilería")}).
     */
    @GET("estandares-rendimiento/buscar")
    Call<List<EstandarRendimientoDto>> buscarEstandares(@retrofit2.http.QueryMap Map<String, String> filtros);

    /** Estándares por unidad de medida (m2, m3, ml, piezas, porcentaje). */
    @GET("estandares-rendimiento/unidad/{unidadMedida}")
    Call<List<EstandarRendimientoDto>> getEstandaresByUnidad(@Path("unidadMedida") String unidadMedida);

    @POST("estandares-rendimiento")
    Call<EstandarRendimientoDto> createEstandar(@Body EstandarRendimientoDto dto);

    @PUT("estandares-rendimiento/{id}")
    Call<EstandarRendimientoDto> updateEstandar(@Path("id") Integer id, @Body EstandarRendimientoDto dto);

    @DELETE("estandares-rendimiento/{id}")
    Call<Void> deleteEstandar(@Path("id") Integer id);

    // ==================== CATÁLOGOS ====================

    @GET("estados")
    Call<List<Estado>> getAllEstados();

    @GET("estados/{id}")
    Call<Estado> getEstadoById(@Path("id") Integer id);

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

    /** Domicilios filtrados por estado (geográfico). Backend: /estado/{idEstado}. */
    @GET("domicilios/estado/{idEstado}")
    Call<List<Domicilio>> getDomiciliosByEstado(@Path("idEstado") Integer idEstado);

    @POST("domicilios")
    Call<Domicilio> createDomicilio(@Body Domicilio domicilio);

    @PUT("domicilios/{id}")
    Call<Domicilio> updateDomicilio(@Path("id") Integer id, @Body Domicilio domicilio);

    @DELETE("domicilios/{id}")
    Call<Void> deleteDomicilio(@Path("id") Integer id);

    @GET("roles")
    Call<List<Rol>> getAllRoles();

    @GET("roles/{id}")
    Call<Rol> getRolById(@Path("id") Integer id);

    @POST("roles")
    Call<Rol> createRol(@Body Rol rol);

    @PUT("roles/{id}")
    Call<Rol> updateRol(@Path("id") Integer id, @Body Rol rol);

    @DELETE("roles/{id}")
    Call<Void> deleteRol(@Path("id") Integer id);

    @GET("registros-migratorios")
    Call<List<RegistroMigratorio>> getAllRegistrosMigratorios();

    @GET("registros-migratorios/{id}")
    Call<RegistroMigratorio> getRegistroMigratorioById(@Path("id") Integer id);

    @POST("registros-migratorios")
    Call<RegistroMigratorio> createRegistroMigratorio(@Body RegistroMigratorio registro);

    @PUT("registros-migratorios/{id}")
    Call<RegistroMigratorio> updateRegistroMigratorio(@Path("id") Integer id, @Body RegistroMigratorio registro);

    /** Cambia solo el campo activo (Boolean). Body: {@code Map.of("activo", true|false)}. */
    @PATCH("registros-migratorios/{id}/activo")
    Call<RegistroMigratorio> patchRegistroMigratorioActivo(@Path("id") Integer id,
                                                           @Body Map<String, Object> body);

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
