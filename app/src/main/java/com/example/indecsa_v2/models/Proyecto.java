package com.example.indecsa_v2.models;

import com.google.gson.annotations.SerializedName;

/**
 * Modelo Android para la entidad Proyecto.
 * Mapeado desde com.example.demo.model.Proyecto (backend Spring Boot).
 *
 * Campos del backend:
 *   id_proyecto            → idProyecto
 *   nombre_proyecto        → nombreProyecto
 *   tipo_proyecto          → tipoProyecto
 *   lugar_proyecto         → lugarProyecto
 *   municipio_proyecto     → municipioProyecto
 *   estado_proyecto_geo    → estadoProyectoGeo   (ENUM: CDMX, Hidalgo, Puebla)
 *   fecha_estimada_inicio  → fechaEstimadaInicio  (LocalDate → String en JSON)
 *   fecha_estimada_fin     → fechaEstimadaFin     (LocalDate → String en JSON)
 *   calificacion_proyecto  → calificacionProyecto (Byte → Float para RatingBar)
 *   estatus_proyecto       → estatusProyecto      (ENUM: PLANEACION, EN_CURSO, PAUSADO, FINALIZADO, CANCELADO)
 *   descripcion_proyecto   → descripcionProyecto
 */
public class Proyecto {

    @SerializedName("idProyecto")
    private Integer idProyecto;

    @SerializedName("nombreProyecto")
    private String nombreProyecto;

    @SerializedName("tipoProyecto")
    private String tipoProyecto;

    @SerializedName("lugarProyecto")
    private String lugarProyecto;

    @SerializedName("municipioProyecto")
    private String municipioProyecto;

    /** ENUM del backend: "CDMX" | "Hidalgo" | "Puebla" */
    @SerializedName("estadoProyectoGeo")
    private String estadoProyectoGeo;

    /** Viene como "yyyy-MM-dd" desde LocalDate de Spring */
    @SerializedName("fechaEstimadaInicio")
    private String fechaEstimadaInicio;

    /** Viene como "yyyy-MM-dd" desde LocalDate de Spring */
    @SerializedName("fechaEstimadaFin")
    private String fechaEstimadaFin;

    /** Byte en backend → Float aquí para usarlo directo en RatingBar */
    @SerializedName("calificacionProyecto")
    private Float calificacionProyecto;

    /** ENUM del backend: "PLANEACION" | "EN_CURSO" | "PAUSADO" | "FINALIZADO" | "CANCELADO" */
    @SerializedName("estatusProyecto")
    private String estatusProyecto;

    @SerializedName("descripcionProyecto")
    private String descripcionProyecto;

    // ─── CONSTRUCTORES ───────────────────────────────────────────────────────

    public Proyecto() {}

    public Proyecto(Integer idProyecto, String nombreProyecto, String tipoProyecto,
                    String lugarProyecto, String municipioProyecto, String estadoProyectoGeo,
                    String fechaEstimadaInicio, String fechaEstimadaFin,
                    Float calificacionProyecto, String estatusProyecto, String descripcionProyecto) {
        this.idProyecto           = idProyecto;
        this.nombreProyecto       = nombreProyecto;
        this.tipoProyecto         = tipoProyecto;
        this.lugarProyecto        = lugarProyecto;
        this.municipioProyecto    = municipioProyecto;
        this.estadoProyectoGeo    = estadoProyectoGeo;
        this.fechaEstimadaInicio  = fechaEstimadaInicio;
        this.fechaEstimadaFin     = fechaEstimadaFin;
        this.calificacionProyecto = calificacionProyecto;
        this.estatusProyecto      = estatusProyecto;
        this.descripcionProyecto  = descripcionProyecto;
    }

    // ─── GETTERS ─────────────────────────────────────────────────────────────

    public Integer getIdProyecto()           { return idProyecto; }
    public String  getNombreProyecto()       { return nombreProyecto; }
    public String  getTipoProyecto()         { return tipoProyecto; }
    public String  getLugarProyecto()        { return lugarProyecto; }
    public String  getMunicipioProyecto()    { return municipioProyecto; }
    public String  getEstadoProyectoGeo()    { return estadoProyectoGeo; }
    public String  getFechaEstimadaInicio()  { return fechaEstimadaInicio; }
    public String  getFechaEstimadaFin()     { return fechaEstimadaFin; }
    public Float   getCalificacionProyecto() { return calificacionProyecto; }
    public String  getEstatusProyecto()      { return estatusProyecto; }
    public String  getDescripcionProyecto()  { return descripcionProyecto; }

    // ─── SETTERS ─────────────────────────────────────────────────────────────

    public void setIdProyecto(Integer idProyecto)                   { this.idProyecto = idProyecto; }
    public void setNombreProyecto(String nombreProyecto)            { this.nombreProyecto = nombreProyecto; }
    public void setTipoProyecto(String tipoProyecto)                { this.tipoProyecto = tipoProyecto; }
    public void setLugarProyecto(String lugarProyecto)              { this.lugarProyecto = lugarProyecto; }
    public void setMunicipioProyecto(String municipioProyecto)      { this.municipioProyecto = municipioProyecto; }
    public void setEstadoProyectoGeo(String estadoProyectoGeo)      { this.estadoProyectoGeo = estadoProyectoGeo; }
    public void setFechaEstimadaInicio(String fechaEstimadaInicio)  { this.fechaEstimadaInicio = fechaEstimadaInicio; }
    public void setFechaEstimadaFin(String fechaEstimadaFin)        { this.fechaEstimadaFin = fechaEstimadaFin; }
    public void setCalificacionProyecto(Float calificacionProyecto) { this.calificacionProyecto = calificacionProyecto; }
    public void setEstatusProyecto(String estatusProyecto)          { this.estatusProyecto = estatusProyecto; }
    public void setDescripcionProyecto(String descripcionProyecto)  { this.descripcionProyecto = descripcionProyecto; }
}