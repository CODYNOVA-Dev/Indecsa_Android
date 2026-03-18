package com.example.indecsa_v2.models;

import com.google.gson.annotations.SerializedName;

public class TrabajadorDto {

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

    // Valores posibles: "ACTIVO", "INACTIVO", "VACACIONES", "BAJA"
    @SerializedName("estadoTrabajador")
    private String estadoTrabajador;

    @SerializedName("descripcionTrabajador")
    private String descripcionTrabajador;

    @SerializedName("calificacionTrabajador")
    private Integer calificacionTrabajador;

    @SerializedName("fechaIngreso")
    private String fechaIngreso; // formato ISO: "2021-03-01"

    // Valores posibles: "CDMX", "Hidalgo", "Puebla"
    @SerializedName("ubicacionTrabajador")
    private String ubicacionTrabajador;

    public Integer getIdTrabajador()          { return idTrabajador; }
    public String getNombreTrabajador()       { return nombreTrabajador; }
    public String getNssTrabajador()          { return nssTrabajador; }
    public String getExperiencia()            { return experiencia; }
    public String getTelefonoTrabajador()     { return telefonoTrabajador; }
    public String getCorreoTrabajador()       { return correoTrabajador; }
    public String getEspecialidadTrabajador() { return especialidadTrabajador; }
    public String getEstadoTrabajador()       { return estadoTrabajador; }
    public String getDescripcionTrabajador()  { return descripcionTrabajador; }
    public Integer getCalificacionTrabajador(){ return calificacionTrabajador; }
    public String getFechaIngreso()           { return fechaIngreso; }
    public String getUbicacionTrabajador()    { return ubicacionTrabajador; }

    public void setNombreTrabajador(String v)       { this.nombreTrabajador = v; }
    public void setNssTrabajador(String v)          { this.nssTrabajador = v; }
    public void setExperiencia(String v)            { this.experiencia = v; }
    public void setTelefonoTrabajador(String v)     { this.telefonoTrabajador = v; }
    public void setCorreoTrabajador(String v)       { this.correoTrabajador = v; }
    public void setEspecialidadTrabajador(String v) { this.especialidadTrabajador = v; }
    public void setEstadoTrabajador(String v)       { this.estadoTrabajador = v; }
    public void setDescripcionTrabajador(String v)  { this.descripcionTrabajador = v; }
    public void setCalificacionTrabajador(Integer v){ this.calificacionTrabajador = v; }
    public void setFechaIngreso(String v)           { this.fechaIngreso = v; }
    public void setUbicacionTrabajador(String v)    { this.ubicacionTrabajador = v; }
}