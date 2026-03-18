package com.example.indecsa_v2.network;

import com.example.indecsa_v2.models.Contratista;
import com.example.indecsa_v2.models.LoginRequestDto;
import com.example.indecsa_v2.models.LoginResponseDto;
import com.example.indecsa_v2.models.ProyectoDto;
import com.example.indecsa_v2.models.TrabajadorDto;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

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

    /**
     * Único endpoint de login.
     * Body: { "correoEmpleado": "admin", "contrasena": "1234" }
     * Response 200: { idEmpleado, nombreEmpleado, correoEmpleado, nombreRol }
     * Response 401: credenciales incorrectas
     */
    @POST("empleados/login")
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
}