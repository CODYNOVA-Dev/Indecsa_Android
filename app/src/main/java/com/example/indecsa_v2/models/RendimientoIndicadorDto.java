package com.example.indecsa_v2.models;

import com.google.gson.annotations.SerializedName;

public class RendimientoIndicadorDto {

    @SerializedName("idTrabajador")         private Integer idTrabajador;
    @SerializedName("nombreTrabajador")     private String  nombreTrabajador;
    @SerializedName("idProyecto")           private Integer idProyecto;
    @SerializedName("nombreProyecto")       private String  nombreProyecto;
    @SerializedName("idCuadrilla")          private Integer idCuadrilla;
    @SerializedName("nombreCuadrilla")      private String  nombreCuadrilla;
    @SerializedName("periodoInicio")        private String  periodoInicio;
    @SerializedName("periodoFin")           private String  periodoFin;
    @SerializedName("totalHorasTrabajadas") private Double  totalHorasTrabajadas;
    @SerializedName("totalAvanceEjecutado") private Double  totalAvanceEjecutado;
    @SerializedName("unidadMedida")         private String  unidadMedida;
    @SerializedName("rendimientoReal")      private Double  rendimientoReal;
    @SerializedName("rendimientoEsperado")  private Double  rendimientoEsperado;
    @SerializedName("porcentajeDesviacion") private Double  porcentajeDesviacion;
    @SerializedName("indicadorSemaforo")    private String  indicadorSemaforo;

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
