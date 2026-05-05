package com.example.indecsa_v2.models;

public class RendimientoIndicadorDto {

    private Integer idTrabajador;
    private String  nombreTrabajador;
    private Integer idProyecto;
    private String  nombreProyecto;
    private Integer idCuadrilla;
    private String  nombreCuadrilla;
    private String  periodoInicio;
    private String  periodoFin;
    private Double  totalHorasTrabajadas;
    private Double  totalAvanceEjecutado;
    private String  unidadMedida;
    private Double  rendimientoReal;
    private Double  rendimientoEsperado;
    private Double  porcentajeDesviacion;
    private String  indicadorSemaforo;

    public Integer getIdTrabajador()             { return idTrabajador; }
    public void    setIdTrabajador(Integer v)    { this.idTrabajador = v; }

    public String  getNombreTrabajador()          { return nombreTrabajador; }
    public void    setNombreTrabajador(String v)  { this.nombreTrabajador = v; }

    public Integer getIdProyecto()               { return idProyecto; }
    public void    setIdProyecto(Integer v)      { this.idProyecto = v; }

    public String  getNombreProyecto()            { return nombreProyecto; }
    public void    setNombreProyecto(String v)    { this.nombreProyecto = v; }

    public Integer getIdCuadrilla()              { return idCuadrilla; }
    public void    setIdCuadrilla(Integer v)     { this.idCuadrilla = v; }

    public String  getNombreCuadrilla()           { return nombreCuadrilla; }
    public void    setNombreCuadrilla(String v)   { this.nombreCuadrilla = v; }

    public String  getPeriodoInicio()             { return periodoInicio; }
    public void    setPeriodoInicio(String v)     { this.periodoInicio = v; }

    public String  getPeriodoFin()                { return periodoFin; }
    public void    setPeriodoFin(String v)        { this.periodoFin = v; }

    public Double  getTotalHorasTrabajadas()         { return totalHorasTrabajadas; }
    public void    setTotalHorasTrabajadas(Double v) { this.totalHorasTrabajadas = v; }

    public Double  getTotalAvanceEjecutado()         { return totalAvanceEjecutado; }
    public void    setTotalAvanceEjecutado(Double v) { this.totalAvanceEjecutado = v; }

    public String  getUnidadMedida()              { return unidadMedida; }
    public void    setUnidadMedida(String v)      { this.unidadMedida = v; }

    public Double  getRendimientoReal()           { return rendimientoReal; }
    public void    setRendimientoReal(Double v)   { this.rendimientoReal = v; }

    public Double  getRendimientoEsperado()       { return rendimientoEsperado; }
    public void    setRendimientoEsperado(Double v){ this.rendimientoEsperado = v; }

    public Double  getPorcentajeDesviacion()         { return porcentajeDesviacion; }
    public void    setPorcentajeDesviacion(Double v) { this.porcentajeDesviacion = v; }

    public String  getIndicadorSemaforo()         { return indicadorSemaforo; }
    public void    setIndicadorSemaforo(String v) { this.indicadorSemaforo = v; }
}
