package com.example.indecsa_v2.network;

import com.example.indecsa_v2.models.AdminDto;
import com.example.indecsa_v2.models.CapHumDto;
import com.example.indecsa_v2.models.Contratista;
import com.example.indecsa_v2.models.FichaCompletaDto;
import com.example.indecsa_v2.models.FichaCreateDto;
import com.example.indecsa_v2.models.FichaDto;
import com.example.indecsa_v2.models.LoginRequestAdmin;
import com.example.indecsa_v2.models.LoginRequestCapHum;
import com.example.indecsa_v2.models.LoginResponse;
import com.example.indecsa_v2.models.ProyectoDto;
import com.example.indecsa_v2.models.TrabajadorDto;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ApiService {



    // ==================== LOGIN ====================
    @POST("admin/login")
    Call<LoginResponse> loginAdmin(@Body LoginRequestAdmin request);

    @POST("capitalhumano/login")
    Call<LoginResponse> loginCapHum(@Body LoginRequestCapHum request);

    @POST("admin")
    Call<AdminDto> registrarAdmin(@Body LoginRequestAdmin request);

    @POST("capitalhumano")
    Call<CapHumDto> registrarCapHumano(@Body LoginRequestCapHum request);

    // ==================== TRABAJADORES ====================
    @GET("trabajadores")
    Call<List<TrabajadorDto>> getAllTrabajadores();

    @GET("trabajadores")
    Call<List<TrabajadorDto>> getTrabajadores();

    @GET("trabajadores/filtros")
    Call<List<TrabajadorDto>> getTrabajadoresFiltrados(
            @Query("estado") String estado,
            @Query("especialidad") String especialidad
    );

    @GET("trabajadores/{id}")
    Call<TrabajadorDto> getTrabajadorById(@Path("id") Integer id);

    @POST("trabajadores")
    Call<TrabajadorDto> createTrabajador(@Body TrabajadorDto trabajador);

    @PUT("trabajadores/{id}")
    Call<TrabajadorDto> updateTrabajador(@Path("id") Integer id, @Body TrabajadorDto trabajador);

    @DELETE("trabajadores/{id}")
    Call<Void> deleteTrabajador(@Path("id") Integer id);

    // ==================== CONTRATISTAS ====================
    @POST("contratistas")
    Call<Contratista> crearContratista(@Body Contratista contratista);

    @GET("contratistas")
    Call<List<Contratista>> obtenerContratistas();

    @GET("contratistas")
    Call<List<Contratista>> getContratistas();

    @GET("contratistas")
    Call<List<Contratista>> obtenerContratistasPorEstadoYEspecialidad(
            @Query("estado") String estado,
            @Query("especialidad") String especialidad
    );

    @PUT("contratistas/{id}")
    Call<Contratista> actualizarContratista(@Path("id") Integer id, @Body Contratista contratista);

    // ==================== PROYECTOS CRUD ====================
    @GET("proyectos")
    Call<List<ProyectoDto>> getProyectos();

    @GET("proyectos/{id}")
    Call<ProyectoDto> getProyectoById(@Path("id") Integer id);

    @POST("proyectos")
    Call<ProyectoDto> crearProyecto(@Body ProyectoDto proyectoDto);

    @PUT("proyectos/{id}")
    Call<ProyectoDto> actualizarProyecto(@Path("id") Integer id, @Body ProyectoDto proyectoDto);

    @DELETE("proyectos/{id}")
    Call<Void> eliminarProyecto(@Path("id") Integer id);

    // ==================== FICHAS ====================
    @GET("fichas")
    Call<List<FichaDto>> getAllFichas();

    @GET("fichas/filtros")
    Call<List<FichaDto>> getFichasFiltradas(
            @Query("estado") String estado,
            @Query("especialidad") String especialidad
    );

    @GET("fichas/estado/{estado}")
    Call<List<FichaDto>> getFichasPorEstado(@Path("estado") String estado);

    @GET("fichas/especialidad/{especialidad}")
    Call<List<FichaDto>> getFichasPorEspecialidad(@Path("especialidad") String especialidad);

    @GET("fichas/{id}")
    Call<FichaDto> getFichaById(@Path("id") Integer id);

    @POST("fichas")
    Call<FichaDto> createFicha(@Body FichaDto ficha);

    // NUEVO: Para crear fichas con trabajadores
    @POST("fichas")
    Call<Void> createFichaConTrabajadores(@Body FichaCreateDto ficha);



    @PUT("fichas/{id}")
    Call<FichaDto> updateFicha(@Path("id") Integer id, @Body FichaDto ficha);

    @DELETE("fichas/{id}")
    Call<Void> deleteFicha(@Path("id") Integer id);

    // ==================== FICHAS COMPLETAS ====================
    @GET("fichas/completas/filtros")
    Call<List<FichaCompletaDto>> getFichasCompletasFiltradas(
            @Query("estado") String estado,
            @Query("especialidad") String especialidad
    );


    // ==================== FICHAS CRUD ====================
    @POST("fichas")
    Call<FichaDto> crearFicha(@Body FichaDto fichaDto);
}