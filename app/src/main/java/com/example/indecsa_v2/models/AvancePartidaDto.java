package com.example.indecsa_v2.models;

public class AvancePartidaDto {

    private Integer idAvance;
    private Integer idProyecto;
    private Integer idCuadrilla;
    private Integer idEstandar;
    private String  nombrePartida;
    private String  fechaRegistro;
    private Double  cantidadEjecutada;
    private String  unidadMedida;
    private Double  cantidadProgramada;
    private String  observaciones;

    // Response-only fields
    private String  nombreProyecto;
    private String  nombreCuadrilla;
    private String  nombreActividad;

    public Integer getIdAvance()             { return idAvance; }
    public void    setIdAvance(Integer v)    { this.idAvance = v; }

    public Integer getIdProyecto()           { return idProyecto; }
    public void    setIdProyecto(Integer v)  { this.idProyecto = v; }

    public Integer getIdCuadrilla()          { return idCuadrilla; }
    public void    setIdCuadrilla(Integer v) { this.idCuadrilla = v; }

    public Integer getIdEstandar()           { return idEstandar; }
    public void    setIdEstandar(Integer v)  { this.idEstandar = v; }

    public String  getNombrePartida()         { return nombrePartida; }
    public void    setNombrePartida(String v) { this.nombrePartida = v; }

    public String  getFechaRegistro()         { return fechaRegistro; }
    public void    setFechaRegistro(String v) { this.fechaRegistro = v; }

    public Double  getCantidadEjecutada()        { return cantidadEjecutada; }
    public void    setCantidadEjecutada(Double v){ this.cantidadEjecutada = v; }

    public String  getUnidadMedida()          { return unidadMedida; }
    public void    setUnidadMedida(String v)  { this.unidadMedida = v; }

    public Double  getCantidadProgramada()        { return cantidadProgramada; }
    public void    setCantidadProgramada(Double v){ this.cantidadProgramada = v; }

    public String  getObservaciones()          { return observaciones; }
    public void    setObservaciones(String v)  { this.observaciones = v; }

    public String  getNombreProyecto()          { return nombreProyecto; }
    public void    setNombreProyecto(String v)  { this.nombreProyecto = v; }

    public String  getNombreCuadrilla()          { return nombreCuadrilla; }
    public void    setNombreCuadrilla(String v)  { this.nombreCuadrilla = v; }

    public String  getNombreActividad()          { return nombreActividad; }
    public void    setNombreActividad(String v)  { this.nombreActividad = v; }
}
