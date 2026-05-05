package com.example.indecsa_v2.network;

import com.example.indecsa_v2.models.AsignacionTrabajadorProyectoDto;
import com.example.indecsa_v2.models.AvancePartidaDto;
import com.example.indecsa_v2.models.Contratista;
import com.example.indecsa_v2.models.CuadrillaDto;
import com.example.indecsa_v2.models.EstandarRendimientoDto;
import com.example.indecsa_v2.models.LoginRequestDto;
import com.example.indecsa_v2.models.LoginResponseDto;
import com.example.indecsa_v2.models.ProyectoDto;
import com.example.indecsa_v2.models.RegistroHorasDto;
import com.example.indecsa_v2.models.RendimientoIndicadorDto;
import com.example.indecsa_v2.models.TrabajadorDto;
import com.example.indecsa_v2.models.EmpleadoDto;

import java.util.List;

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
 * CORRECCIONES respecto a la versión anterior:
 *
 * 1. LOGIN
 *    - Eliminados loginAdmin(), loginCapHum(), registrarAdmin(), registrarCapHumano().
 *      El backend actual tiene un único endpoint de login unificado.
 *    - Añadido login() → POST /empleados/login con LoginRequestDto / LoginResponseDto.
 *
 * 2. TRABAJADORES
 *    - Eliminado getTrabajadoresFiltrados(): el backend no tiene /trabajadores/filtros.
 *      Para filtrar se usan los endpoints /trabajadores/estado/{estado} y
 *      /trabajadores/especialidad/{especialidad} por separado.
 *    - Añadidos getByEstado() y getByEspecialidad() para reflejar los endpoints reales.
 *    - Añadido cambiarEstadoTrabajador() → PATCH /trabajadores/{id}/estado
 *
 * 3. CONTRATISTAS
 *    - Eliminado obtenerContratistasPorEstadoYEspecialidad(): endpoint inexistente.
 *    - Añadido getContratistasByEstado() → GET /contratistas/estado/{estado}
 *    - Añadido cambiarEstadoContratista() → PATCH /contratistas/{id}/estado
 *    - Eliminado crearContratista() duplicado (ya existía como POST "contratistas").
 *
 * 4. PROYECTOS
 *    - Añadido getByEstatus() → GET /proyectos/estatus/{estatus}
 *    - Añadido getByMunicipio() → GET /proyectos/municipio/{municipio}
 *    - Añadido cambiarEstatusProyecto() → PATCH /proyectos/{id}/estatus
 *
 * 5. FICHAS
 *    - Eliminado todo el bloque de fichas. La BD actual no tiene tabla Ficha;
 *      FichaDto, FichaCreateDto, FichaCompletaDto son artefactos de una versión
 *      anterior. Si se reintroduce esa entidad, agregar de nuevo los endpoints.
 *
 * 6. DTOs eliminados del import
 *    - AdminDto, CapHumDto, FichaDto, FichaCreateDto, FichaCompletaDto,
 *      LoginRequestAdmin, LoginRequestCapHum, LoginResponse → ya no existen / no se usan.
 *
 * BASE_URL: sigue en RetrofitClient; se quitó BASE_URL de aquí (era inconsistente
 * porque Tab_Admin_Contratista creaba su propio Retrofit con ApiService.BASE_URL
 * que aquí no existía como constante). Unificar siempre a través de RetrofitClient.
 */
public interface ApiService {

    // ==================== AUTH ====================

    @GET("empleados")
    Call<List<EmpleadoDto>> getAllEmpleados();

    @GET("empleados/rol/{idRol}")
    Call<List<EmpleadoDto>> getEmpleadosByRol(@Path("idRol") Integer idRol);

    @DELETE("empleados/{id}")
    Call<Void> deleteEmpleado(@Path("id") Integer id);

    /**
     * Único endpoint de login.
     * Body: { "correoEmpleado": "admin", "contrasena": "1234" }
     * Response 200: { idEmpleado, nombreEmpleado, correoEmpleado, nombreRol }
     * Response 401: credenciales incorrectas
     */
    @POST("auth/login")
    Call<LoginResponseDto> login(@Body LoginRequestDto request);

    // ==================== TRABAJADORES ====================

    @GET("trabajadores")
    Call<List<TrabajadorDto>> getAllTrabajadores();

    @GET("trabajadores/{id}")
    Call<TrabajadorDto> getTrabajadorById(@Path("id") Integer id);

    @GET("trabajadores/estado/{estado}")
    Call<List<TrabajadorDto>> getTrabajadoresByEstado(@Path("estado") String estado);

    @GET("trabajadores/especialidad/{especialidad}")
    Call<List<TrabajadorDto>> getTrabajadoresByEspecialidad(@Path("especialidad") String especialidad);

    @POST("trabajadores")
    Call<TrabajadorDto> createTrabajador(@Body TrabajadorDto trabajador);

    @PUT("trabajadores/{id}")
    Call<TrabajadorDto> updateTrabajador(@Path("id") Integer id, @Body TrabajadorDto trabajador);

    @PATCH("trabajadores/{id}/estado")
    Call<TrabajadorDto> cambiarEstadoTrabajador(@Path("id") Integer id, @Query("estado") String estado);

    @DELETE("trabajadores/{id}")
    Call<Void> deleteTrabajador(@Path("id") Integer id);

    // ==================== CONTRATISTAS ====================

    @GET("contratistas")
    Call<List<Contratista>> getAllContratistas();

    @GET("contratistas/{id}")
    Call<Contratista> getContratistaById(@Path("id") Integer id);

    @GET("contratistas/estado/{estado}")
    Call<List<Contratista>> getContratistasByEstado(@Path("estado") String estado);

    @POST("contratistas")
    Call<Contratista> createContratista(@Body Contratista contratista);

    @PUT("contratistas/{id}")
    Call<Contratista> updateContratista(@Path("id") Integer id, @Body Contratista contratista);

    @PATCH("contratistas/{id}/estado")
    Call<Contratista> cambiarEstadoContratista(@Path("id") Integer id, @Query("estado") String estado);

    @DELETE("contratistas/{id}")
    Call<Void> deleteContratista(@Path("id") Integer id);

    // ==================== PROYECTOS ====================

    @GET("proyectos")
    Call<List<ProyectoDto>> getAllProyectos();

    @GET("proyectos/{id}")
    Call<ProyectoDto> getProyectoById(@Path("id") Integer id);

    @GET("proyectos/estatus/{estatus}")
    Call<List<ProyectoDto>> getProyectosByEstatus(@Path("estatus") String estatus);

    @GET("proyectos/municipio/{municipio}")
    Call<List<ProyectoDto>> getProyectosByMunicipio(@Path("municipio") String municipio);

    @POST("proyectos")
    Call<ProyectoDto> createProyecto(@Body ProyectoDto proyectoDto);

    @PUT("proyectos/{id}")
    Call<ProyectoDto> updateProyecto(@Path("id") Integer id, @Body ProyectoDto proyectoDto);

    @PATCH("proyectos/{id}/estatus")
    Call<ProyectoDto> cambiarEstatusProyecto(@Path("id") Integer id, @Query("estatus") String estatus);

    @DELETE("proyectos/{id}")
    Call<Void> deleteProyecto(@Path("id") Integer id);

    // ==================== ASIGNACIONES TRABAJADOR-PROYECTO ====================

    @GET("asignaciones-trabajador/proyecto/{idProyecto}")
    Call<List<AsignacionTrabajadorProyectoDto>> getAsignacionesByProyecto(@Path("idProyecto") Integer idProyecto);

    @GET("asignaciones-trabajador/trabajador/{idTrabajador}")
    Call<List<AsignacionTrabajadorProyectoDto>> getAsignacionesByTrabajador(@Path("idTrabajador") Integer idTrabajador);

    @GET("asignaciones-trabajador/{id}")
    Call<AsignacionTrabajadorProyectoDto> getAsignacionById(@Path("id") Integer id);

    // ==================== CUADRILLAS ====================

    @GET("cuadrillas/proyecto/{idProyecto}")
    Call<List<CuadrillaDto>> getCuadrillasByProyecto(@Path("idProyecto") Integer idProyecto);

    @POST("cuadrillas")
    Call<CuadrillaDto> createCuadrilla(@Body CuadrillaDto cuadrilla);

    @PUT("cuadrillas/{id}")
    Call<CuadrillaDto> updateCuadrilla(@Path("id") Integer id, @Body CuadrillaDto cuadrilla);

    @DELETE("cuadrillas/{id}")
    Call<Void> deleteCuadrilla(@Path("id") Integer id);

    // ==================== REGISTRO DE HORAS ====================

    @GET("registro-horas/proyecto/{idProyecto}")
    Call<List<RegistroHorasDto>> getRegistroHorasByProyecto(
            @Path("idProyecto") Integer idProyecto,
            @Query("fechaInicio") String fechaInicio,
            @Query("fechaFin") String fechaFin);

    @GET("registro-horas/trabajador/{idTrabajador}")
    Call<List<RegistroHorasDto>> getRegistroHorasByTrabajador(@Path("idTrabajador") Integer idTrabajador);

    @POST("registro-horas")
    Call<RegistroHorasDto> createRegistroHoras(@Body RegistroHorasDto dto);

    @DELETE("registro-horas/{id}")
    Call<Void> deleteRegistroHoras(@Path("id") Integer id);

    // ==================== AVANCE DE PARTIDA ====================

    @GET("avance-partida/proyecto/{idProyecto}")
    Call<List<AvancePartidaDto>> getAvancesByProyecto(
            @Path("idProyecto") Integer idProyecto,
            @Query("fechaInicio") String fechaInicio,
            @Query("fechaFin") String fechaFin);

    @POST("avance-partida")
    Call<AvancePartidaDto> createAvancePartida(@Body AvancePartidaDto dto);

    @DELETE("avance-partida/{id}")
    Call<Void> deleteAvancePartida(@Path("id") Integer id);

    // ==================== ESTÁNDARES DE RENDIMIENTO ====================

    @GET("estandares-rendimiento")
    Call<List<EstandarRendimientoDto>> getAllEstandares();

    // ==================== INDICADORES DE RENDIMIENTO ====================

    @GET("rendimiento/trabajador/{idTrabajador}")
    Call<List<RendimientoIndicadorDto>> getRendimientoTrabajador(
            @Path("idTrabajador") Integer idTrabajador,
            @Query("fechaInicio") String fechaInicio,
            @Query("fechaFin") String fechaFin);

    @GET("rendimiento/proyecto/{idProyecto}")
    Call<List<RendimientoIndicadorDto>> getRendimientoProyecto(
            @Path("idProyecto") Integer idProyecto,
            @Query("fechaInicio") String fechaInicio,
            @Query("fechaFin") String fechaFin);

    // ==================== REPORTES PDF ====================

    @Streaming
    @GET("reportes/rendimiento/trabajador/{id}")
    Call<ResponseBody> descargarRendimientoTrabajador(
            @Path("id") Integer id,
            @Query("fechaInicio") String fechaInicio,
            @Query("fechaFin") String fechaFin);

    @Streaming
    @GET("reportes/horas/proyecto/{id}")
    Call<ResponseBody> descargarHorasProyecto(
            @Path("id") Integer id,
            @Query("fechaInicio") String fechaInicio,
            @Query("fechaFin") String fechaFin);

    @Streaming
    @GET("reportes/avance/proyecto/{id}")
    Call<ResponseBody> descargarAvanceObra(
            @Path("id") Integer id,
            @Query("fechaInicio") String fechaInicio,
            @Query("fechaFin") String fechaFin);
}