package com.example.indecsa_v2.models;

import com.google.gson.annotations.SerializedName;

/**
 * CORRECCIONES respecto al modelo anterior:
 *
 * 1. Se alinearon los @SerializedName con los campos reales que devuelve
 *    ContratistaResponseDTO del backend:
 *      - "rfcContratista"          (antes era "rfc"        → no coincidía con la API)
 *      - "telefonoContratista"     (antes era "telefono"   → no coincidía con la API)
 *      - "correoContratista"       (antes era "correo"     → no coincidía con la API)
 *      - "descripcionContratista"  (ya existía, se mantuvo)
 *      - "calificacionContratista" (antes era "calificacion" con Integer → API devuelve Byte/int)
 *      - "ubicacionContratista"    (antes era "ubicacion"  → no coincidía con la API)
 *      - "estadoContratista"       (ya existía, se mantuvo)
 *    El campo "especialidad" NO existe en ContratistaResponseDTO; se eliminó.
 *
 * 2. Se renombraron los getters para reflejar los nombres correctos,
 *    manteniendo retrocompatibilidad donde el adaptador Android los usaba.
 *    Tab_Admin_Contratista usaba:
 *      contratista.getRfc()         → ahora getRfcContratista()
 *      contratista.getUbicacion()   → ahora getUbicacionContratista()
 *      contratista.getEspecialidad()→ ELIMINADO (campo no existe en la API)
 *      contratista.getTelefono()    → ahora getTelefonoContratista()
 *      contratista.getCorreo()      → ahora getCorreoContratista()
 *    IMPORTANTE: actualizar Tab_Admin_Contratista con los nuevos getters.
 */
public class Contratista {

    @SerializedName("idContratista")
    private Integer idContratista;

    @SerializedName("nombreContratista")
    private String nombreContratista;

    @SerializedName("rfcContratista")
    private String rfcContratista;

    @SerializedName("telefonoContratista")
    private String telefonoContratista;

    @SerializedName("correoContratista")
    private String correoContratista;

    @SerializedName("descripcionContratista")
    private String descripcionContratista;

    @SerializedName("experiencia")
    private String experiencia;

    @SerializedName("calificacionContratista")
    private Integer calificacionContratista;

    @SerializedName("estadoContratista")
    private String estadoContratista;

    @SerializedName("ubicacionContratista")
    private String ubicacionContratista;

    public Contratista() {}

    public Integer getIdContratista()          { return idContratista; }
    public void setIdContratista(Integer v)    { this.idContratista = v; }

    public String getNombreContratista()       { return nombreContratista; }
    public void setNombreContratista(String v) { this.nombreContratista = v; }

    public String getRfcContratista()          { return rfcContratista; }
    public void setRfcContratista(String v)    { this.rfcContratista = v; }

    public String getTelefonoContratista()     { return telefonoContratista; }
    public void setTelefonoContratista(String v){ this.telefonoContratista = v; }

    public String getCorreoContratista()       { return correoContratista; }
    public void setCorreoContratista(String v) { this.correoContratista = v; }

    public String getDescripcionContratista()  { return descripcionContratista; }
    public void setDescripcionContratista(String v){ this.descripcionContratista = v; }

    public String getExperiencia()             { return experiencia; }
    public void setExperiencia(String v)       { this.experiencia = v; }

    public Integer getCalificacionContratista()       { return calificacionContratista; }
    public void setCalificacionContratista(Integer v) { this.calificacionContratista = v; }

    public String getEstadoContratista()       { return estadoContratista; }
    public void setEstadoContratista(String v) { this.estadoContratista = v; }

    public String getUbicacionContratista()    { return ubicacionContratista; }
    public void setUbicacionContratista(String v){ this.ubicacionContratista = v; }
}