package com.example.indecsa_v2.models;

/**
 * Refleja AsignacionTrabajadorProyectoResponse del backend.
 * Las fechas se manejan como String (formato ISO yyyy-MM-dd).
 */
public class AsignacionTrabajadorProyectoDto {

    private Integer idAsignacionTp;
    private Integer idTrabajador;
    private Integer idProyecto;
    private Integer idAsignacionPc;
    private String  puestoEnProyecto;
    private String  fechaInicio;
    private String  fechaFinEstimada;
    private String  estatusAsignacion;
    private String  observaciones;

    public AsignacionTrabajadorProyectoDto() { }

    public Integer getIdAsignacionTp()       { return idAsignacionTp; }
    public void    setIdAsignacionTp(Integer v){ this.idAsignacionTp = v; }

    public Integer getIdTrabajador()         { return idTrabajador; }
    public void    setIdTrabajador(Integer v){ this.idTrabajador = v; }

    public Integer getIdProyecto()           { return idProyecto; }
    public void    setIdProyecto(Integer v)  { this.idProyecto = v; }

    public Integer getIdAsignacionPc()       { return idAsignacionPc; }
    public void    setIdAsignacionPc(Integer v){ this.idAsignacionPc = v; }

    public String getPuestoEnProyecto()      { return puestoEnProyecto; }
    public void   setPuestoEnProyecto(String v){ this.puestoEnProyecto = v; }

    public String getFechaInicio()           { return fechaInicio; }
    public void   setFechaInicio(String v)   { this.fechaInicio = v; }

    public String getFechaFinEstimada()      { return fechaFinEstimada; }
    public void   setFechaFinEstimada(String v){ this.fechaFinEstimada = v; }

    public String getEstatusAsignacion()     { return estatusAsignacion; }
    public void   setEstatusAsignacion(String v){ this.estatusAsignacion = v; }

    public String getObservaciones()         { return observaciones; }
    public void   setObservaciones(String v) { this.observaciones = v; }
}
