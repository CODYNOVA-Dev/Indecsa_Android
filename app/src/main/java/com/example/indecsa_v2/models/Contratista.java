package com.example.indecsa_v2.models;

import com.google.gson.annotations.SerializedName;

/**
 * Refleja la entidad Contratista del backend.
 *
 * Campos del backend:
 *   idContratista, nombreContratista, curp, rfcContratista,
 *   telefonoContratista, correoContratista, descripcionContratista,
 *   fotoPerfilUrl, experiencia, calificacionContratista (Byte),
 *   estadoContratista (enum ACTIVO/INACTIVO/SUSPENDIDO),
 *   estadoOperacion (Estado anidado).
 *
 * Se mantiene `ubicacionContratista` como campo solo-cliente
 * (transient → ignorado por Gson) para no romper el código existente
 * que lo lee/escribe — pero NO viaja al backend.
 */
public class Contratista {

    @SerializedName("idContratista")          private Integer idContratista;
    @SerializedName("nombreContratista")      private String  nombreContratista;
    @SerializedName("curp")                   private String  curp;
    @SerializedName("rfcContratista")         private String  rfcContratista;
    @SerializedName("telefonoContratista")    private String  telefonoContratista;
    @SerializedName("correoContratista")      private String  correoContratista;
    @SerializedName("descripcionContratista") private String  descripcionContratista;
    @SerializedName("fotoPerfilUrl")          private String  fotoPerfilUrl;
    @SerializedName("experiencia")            private String  experiencia;
    @SerializedName("calificacionContratista") private Integer calificacionContratista; // backend usa Byte; Gson lo deserializa OK como Integer
    @SerializedName("estadoContratista")      private String  estadoContratista;
    @SerializedName("estadoOperacion")        private Estado  estadoOperacion;

    // Campo legacy solo-cliente (no enviar al backend, no leer de él)
    private transient String ubicacionContratista;

    public Contratista() {}

    public Contratista(Integer idContratista) {
        this.idContratista = idContratista;
    }

    public Integer getIdContratista()          { return idContratista; }
    public void    setIdContratista(Integer v) { this.idContratista = v; }

    public String  getNombreContratista()           { return nombreContratista; }
    public void    setNombreContratista(String v)   { this.nombreContratista = v; }

    public String  getCurp()                        { return curp; }
    public void    setCurp(String v)                { this.curp = v; }

    public String  getRfcContratista()              { return rfcContratista; }
    public void    setRfcContratista(String v)      { this.rfcContratista = v; }

    public String  getTelefonoContratista()         { return telefonoContratista; }
    public void    setTelefonoContratista(String v) { this.telefonoContratista = v; }

    public String  getCorreoContratista()           { return correoContratista; }
    public void    setCorreoContratista(String v)   { this.correoContratista = v; }

    public String  getDescripcionContratista()      { return descripcionContratista; }
    public void    setDescripcionContratista(String v){ this.descripcionContratista = v; }

    public String  getFotoPerfilUrl()               { return fotoPerfilUrl; }
    public void    setFotoPerfilUrl(String v)       { this.fotoPerfilUrl = v; }

    public String  getExperiencia()                 { return experiencia; }
    public void    setExperiencia(String v)         { this.experiencia = v; }

    public Integer getCalificacionContratista()         { return calificacionContratista; }
    public void    setCalificacionContratista(Integer v){ this.calificacionContratista = v; }

    public String  getEstadoContratista()           { return estadoContratista; }
    public void    setEstadoContratista(String v)   { this.estadoContratista = v; }

    public Estado  getEstadoOperacion()             { return estadoOperacion; }
    public void    setEstadoOperacion(Estado v)     { this.estadoOperacion = v; }

    // ---- atajos ----
    public Integer getIdEstadoOperacion() {
        return estadoOperacion != null ? estadoOperacion.getIdEstado() : null;
    }
    public void setIdEstadoOperacion(Integer id) {
        if (id == null) { estadoOperacion = null; return; }
        if (estadoOperacion == null) estadoOperacion = new Estado();
        estadoOperacion.setIdEstado(id);
    }

    /**
     * Legacy: la UI usaba "ubicación" como un texto libre que no existía en backend.
     * Si hay `estadoOperacion`, devuelve su nombre; si no, devuelve el cache local.
     */
    public String getUbicacionContratista() {
        if (estadoOperacion != null && estadoOperacion.getNombreEst() != null) {
            return estadoOperacion.getNombreEst();
        }
        return ubicacionContratista;
    }
    public void setUbicacionContratista(String v) {
        this.ubicacionContratista = v;
    }
}
