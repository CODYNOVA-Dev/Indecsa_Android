package com.example.indecsa_v2.models;

public class RegistroHorasDto {

    private Integer idRegistro;
    private Integer idAsignacionTp;
    private Integer idCuadrilla;
    private String  fechaRegistro;
    private Double  horasTrabajadas;
    private String  tipoPeriodo;
    private String  observaciones;

    // Response-only fields (populated by server)
    private Integer idTrabajador;
    private String  nombreTrabajador;
    private Integer idProyecto;
    private String  nombreProyecto;
    private String  nombreCuadrilla;

    public Integer getIdRegistro()            { return idRegistro; }
    public void    setIdRegistro(Integer v)   { this.idRegistro = v; }

    public Integer getIdAsignacionTp()           { return idAsignacionTp; }
    public void    setIdAsignacionTp(Integer v)  { this.idAsignacionTp = v; }

    public Integer getIdCuadrilla()           { return idCuadrilla; }
    public void    setIdCuadrilla(Integer v)  { this.idCuadrilla = v; }

    public String  getFechaRegistro()         { return fechaRegistro; }
    public void    setFechaRegistro(String v) { this.fechaRegistro = v; }

    public Double  getHorasTrabajadas()        { return horasTrabajadas; }
    public void    setHorasTrabajadas(Double v){ this.horasTrabajadas = v; }

    public String  getTipoPeriodo()           { return tipoPeriodo; }
    public void    setTipoPeriodo(String v)   { this.tipoPeriodo = v; }

    public String  getObservaciones()         { return observaciones; }
    public void    setObservaciones(String v) { this.observaciones = v; }

    public Integer getIdTrabajador()          { return idTrabajador; }
    public void    setIdTrabajador(Integer v) { this.idTrabajador = v; }

    public String  getNombreTrabajador()         { return nombreTrabajador; }
    public void    setNombreTrabajador(String v) { this.nombreTrabajador = v; }

    public Integer getIdProyecto()            { return idProyecto; }
    public void    setIdProyecto(Integer v)   { this.idProyecto = v; }

    public String  getNombreProyecto()         { return nombreProyecto; }
    public void    setNombreProyecto(String v) { this.nombreProyecto = v; }

    public String  getNombreCuadrilla()         { return nombreCuadrilla; }
    public void    setNombreCuadrilla(String v) { this.nombreCuadrilla = v; }
}
