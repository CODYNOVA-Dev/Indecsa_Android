package com.example.indecsa_v2.models;

/**
 * Refleja la entidad Proyecto del backend.
 *
 * Backend requiere NOT NULL: nombreProyecto, cliente, domicilio,
 * estatusProyecto. La UI actual (AgregarProyectoDialog) solo captura
 * nombre, tipo y "lugar"; un POST con esos campos fallará con 400.
 *
 * Se mantienen los atajos lugarProyecto / municipioProyecto /
 * estadoProyectoGeo como cache local (transient) para no romper la UI.
 */
public class ProyectoDto {

    private Integer   idProyecto;
    private String    nombreProyecto;
    private String    tipoProyecto;   // Construccion, Remodelacion, Venta_mobiliaria, Instalacion_de_mobiliario
    private String    ofertaTrabajo;
    private String    cliente;
    private Domicilio domicilio;
    private String    fechaEstimadaInicio;
    private String    fechaEstimadaFin;
    private Integer   calificacionProyecto; // backend es Byte
    private String    estatusProyecto;      // PLANEACION, EN_CURSO, PENDIENTE, FINALIZADO, CANCELADO
    private String    descripcionProyecto;
    private String    imagenProyectoUrl;

    // ---- cache local solo-cliente ----
    private transient String lugarProyectoLocal;
    private transient String municipioProyectoLocal;
    private transient String estadoProyectoGeoLocal;

    public ProyectoDto() {}

    public ProyectoDto(Integer idProyecto) {
        this.idProyecto = idProyecto;
    }

    /** Constructor de conveniencia usado por AgregarProyectoDialog. */
    public ProyectoDto(String nombreProyecto, String tipoProyecto, String lugarProyecto) {
        this.nombreProyecto = nombreProyecto;
        this.tipoProyecto = tipoProyecto;
        this.lugarProyectoLocal = lugarProyecto;
    }

    public Integer getIdProyecto()              { return idProyecto; }
    public void    setIdProyecto(Integer v)     { this.idProyecto = v; }

    public String  getNombreProyecto()          { return nombreProyecto; }
    public void    setNombreProyecto(String v)  { this.nombreProyecto = v; }

    public String  getTipoProyecto()            { return tipoProyecto; }
    public void    setTipoProyecto(String v)    { this.tipoProyecto = v; }

    public String  getOfertaTrabajo()           { return ofertaTrabajo; }
    public void    setOfertaTrabajo(String v)   { this.ofertaTrabajo = v; }

    public String  getCliente()                 { return cliente; }
    public void    setCliente(String v)         { this.cliente = v; }

    public Domicilio getDomicilio()             { return domicilio; }
    public void      setDomicilio(Domicilio v)  { this.domicilio = v; }

    public String  getFechaEstimadaInicio()         { return fechaEstimadaInicio; }
    public void    setFechaEstimadaInicio(String v) { this.fechaEstimadaInicio = v; }

    public String  getFechaEstimadaFin()            { return fechaEstimadaFin; }
    public void    setFechaEstimadaFin(String v)    { this.fechaEstimadaFin = v; }

    public Integer getCalificacionProyecto()         { return calificacionProyecto; }
    public void    setCalificacionProyecto(Integer v){ this.calificacionProyecto = v; }

    public String  getEstatusProyecto()         { return estatusProyecto; }
    public void    setEstatusProyecto(String v) { this.estatusProyecto = v; }

    public String  getDescripcionProyecto()         { return descripcionProyecto; }
    public void    setDescripcionProyecto(String v) { this.descripcionProyecto = v; }

    public String  getImagenProyectoUrl()           { return imagenProyectoUrl; }
    public void    setImagenProyectoUrl(String v)   { this.imagenProyectoUrl = v; }

    // ---- atajos legacy ----

    public Integer getIdDomicilio() {
        return domicilio != null ? domicilio.getIdDomicilio() : null;
    }
    public void setIdDomicilio(Integer id) {
        if (id == null) { domicilio = null; return; }
        if (domicilio == null) domicilio = new Domicilio();
        domicilio.setIdDomicilio(id);
    }

    /**
     * Backend no tiene "lugarProyecto" plano; se deriva del domicilio si existe.
     */
    public String getLugarProyecto() {
        if (domicilio != null) {
            String r = domicilio.resumen();
            if (r != null && !r.isEmpty()) return r;
        }
        return lugarProyectoLocal;
    }
    public void setLugarProyecto(String v) {
        this.lugarProyectoLocal = v;
    }

    public String getMunicipioProyecto() {
        if (domicilio != null && domicilio.getMunAlc() != null) {
            return domicilio.getMunAlc();
        }
        return municipioProyectoLocal;
    }
    public void setMunicipioProyecto(String v) {
        this.municipioProyectoLocal = v;
    }

    public String getEstadoProyectoGeo() {
        if (domicilio != null && domicilio.getEstado() != null) {
            return domicilio.getEstado().getNombreEst();
        }
        return estadoProyectoGeoLocal;
    }
    public void setEstadoProyectoGeo(String v) {
        this.estadoProyectoGeoLocal = v;
    }
}
