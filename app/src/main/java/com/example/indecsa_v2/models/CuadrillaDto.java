package com.example.indecsa_v2.models;

public class CuadrillaDto {

    private Integer idCuadrilla;
    private Integer idProyecto;
    private String  nombreProyecto;
    private String  nombreCuadrilla;
    private String  frenteTrabajo;
    private String  estatusCuadrilla;
    private String  observaciones;

    public CuadrillaDto() { }

    public Integer getIdCuadrilla()         { return idCuadrilla; }
    public void    setIdCuadrilla(Integer v){ this.idCuadrilla = v; }

    public Integer getIdProyecto()          { return idProyecto; }
    public void    setIdProyecto(Integer v) { this.idProyecto = v; }

    public String getNombreProyecto()       { return nombreProyecto; }
    public void   setNombreProyecto(String v){ this.nombreProyecto = v; }

    public String getNombreCuadrilla()      { return nombreCuadrilla; }
    public void   setNombreCuadrilla(String v){ this.nombreCuadrilla = v; }

    public String getFrenteTrabajo()        { return frenteTrabajo; }
    public void   setFrenteTrabajo(String v){ this.frenteTrabajo = v; }

    public String getEstatusCuadrilla()     { return estatusCuadrilla; }
    public void   setEstatusCuadrilla(String v){ this.estatusCuadrilla = v; }

    public String getObservaciones()        { return observaciones; }
    public void   setObservaciones(String v){ this.observaciones = v; }
}
