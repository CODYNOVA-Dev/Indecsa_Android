package com.example.indecsa_v2.models;

import com.google.gson.annotations.SerializedName;

/**
 * Modelo Android para la entidad Trabajador.
 * Mapeado desde com.example.demo.model.Trabajador (backend Spring Boot).
 *
 * Campos del backend:
 *   id_trabajador           → idTrabajador
 *   nombre_trabajador       → nombreTrabajador
 *   nss_trabajador          → nssTrabajador
 *   experiencia             → experiencia
 *   telefono_trabajador     → telefonoTrabajador
 *   correo_trabajador       → correoTrabajador
 *   especialidad_trabajador → especialidadTrabajador
 *   estado_trabajador       → estadoTrabajador  (ENUM: ACTIVO, INACTIVO, VACACIONES, BAJA)
 *   descripcion_trabajador  → descripcionTrabajador
 *   calificacion_trabajador → calificacionTrabajador (Byte → Float para RatingBar)
 *   fecha_ingreso           → fechaIngreso      (LocalDate → String en JSON)
 *   ubicacion_trabajador    → ubicacionTrabajador (ENUM: CDMX, Hidalgo, Puebla)
 */
public class Trabajador {

    @SerializedName("idTrabajador")
    private Integer idTrabajador;

    @SerializedName("nombreTrabajador")
    private String nombreTrabajador;

    @SerializedName("nssTrabajador")
    private String nssTrabajador;

    @SerializedName("experiencia")
    private String experiencia;

    @SerializedName("telefonoTrabajador")
    private String telefonoTrabajador;

    @SerializedName("correoTrabajador")
    private String correoTrabajador;

    @SerializedName("especialidadTrabajador")
    private String especialidadTrabajador;

    /** ENUM del backend: "ACTIVO" | "INACTIVO" | "VACACIONES" | "BAJA" */
    @SerializedName("estadoTrabajador")
    private String estadoTrabajador;

    @SerializedName("descripcionTrabajador")
    private String descripcionTrabajador;

    /** Byte en backend → Float aquí para usarlo directo en RatingBar */
    @SerializedName("calificacionTrabajador")
    private Float calificacionTrabajador;

    /** Viene como "yyyy-MM-dd" desde LocalDate de Spring */
    @SerializedName("fechaIngreso")
    private String fechaIngreso;

    /** ENUM del backend: "CDMX" | "Hidalgo" | "Puebla" */
    @SerializedName("ubicacionTrabajador")
    private String ubicacionTrabajador;

    // ─── CONSTRUCTORES ───────────────────────────────────────────────────────

    public Trabajador() {}

    public Trabajador(Integer idTrabajador, String nombreTrabajador, String nssTrabajador,
                      String experiencia, String telefonoTrabajador, String correoTrabajador,
                      String especialidadTrabajador, String estadoTrabajador,
                      String descripcionTrabajador, Float calificacionTrabajador,
                      String fechaIngreso, String ubicacionTrabajador) {
        this.idTrabajador           = idTrabajador;
        this.nombreTrabajador       = nombreTrabajador;
        this.nssTrabajador          = nssTrabajador;
        this.experiencia            = experiencia;
        this.telefonoTrabajador     = telefonoTrabajador;
        this.correoTrabajador       = correoTrabajador;
        this.especialidadTrabajador = especialidadTrabajador;
        this.estadoTrabajador       = estadoTrabajador;
        this.descripcionTrabajador  = descripcionTrabajador;
        this.calificacionTrabajador = calificacionTrabajador;
        this.fechaIngreso           = fechaIngreso;
        this.ubicacionTrabajador    = ubicacionTrabajador;
    }

    // ─── GETTERS ─────────────────────────────────────────────────────────────

    public Integer getIdTrabajador()            { return idTrabajador; }
    public String  getNombreTrabajador()        { return nombreTrabajador; }
    public String  getNssTrabajador()           { return nssTrabajador; }
    public String  getExperiencia()             { return experiencia; }
    public String  getTelefonoTrabajador()      { return telefonoTrabajador; }
    public String  getCorreoTrabajador()        { return correoTrabajador; }
    public String  getEspecialidadTrabajador()  { return especialidadTrabajador; }
    public String  getEstadoTrabajador()        { return estadoTrabajador; }
    public String  getDescripcionTrabajador()   { return descripcionTrabajador; }
    public Float   getCalificacionTrabajador()  { return calificacionTrabajador; }
    public String  getFechaIngreso()            { return fechaIngreso; }
    public String  getUbicacionTrabajador()     { return ubicacionTrabajador; }

    // ─── SETTERS ─────────────────────────────────────────────────────────────

    public void setIdTrabajador(Integer idTrabajador)                      { this.idTrabajador = idTrabajador; }
    public void setNombreTrabajador(String nombreTrabajador)               { this.nombreTrabajador = nombreTrabajador; }
    public void setNssTrabajador(String nssTrabajador)                     { this.nssTrabajador = nssTrabajador; }
    public void setExperiencia(String experiencia)                         { this.experiencia = experiencia; }
    public void setTelefonoTrabajador(String telefonoTrabajador)           { this.telefonoTrabajador = telefonoTrabajador; }
    public void setCorreoTrabajador(String correoTrabajador)               { this.correoTrabajador = correoTrabajador; }
    public void setEspecialidadTrabajador(String especialidadTrabajador)   { this.especialidadTrabajador = especialidadTrabajador; }
    public void setEstadoTrabajador(String estadoTrabajador)               { this.estadoTrabajador = estadoTrabajador; }
    public void setDescripcionTrabajador(String descripcionTrabajador)     { this.descripcionTrabajador = descripcionTrabajador; }
    public void setCalificacionTrabajador(Float calificacionTrabajador)    { this.calificacionTrabajador = calificacionTrabajador; }
    public void setFechaIngreso(String fechaIngreso)                       { this.fechaIngreso = fechaIngreso; }
    public void setUbicacionTrabajador(String ubicacionTrabajador)         { this.ubicacionTrabajador = ubicacionTrabajador; }
}