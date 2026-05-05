package com.example.indecsa_v2.models;

public class EstandarRendimientoDto {

    private Integer idEstandar;
    private String  nombreActividad;
    private String  unidadMedida;
    private Double  rendimientoEsperado;
    private String  descripcion;

    public Integer getIdEstandar()              { return idEstandar; }
    public void    setIdEstandar(Integer v)     { this.idEstandar = v; }

    public String  getNombreActividad()          { return nombreActividad; }
    public void    setNombreActividad(String v)  { this.nombreActividad = v; }

    public String  getUnidadMedida()             { return unidadMedida; }
    public void    setUnidadMedida(String v)     { this.unidadMedida = v; }

    public Double  getRendimientoEsperado()         { return rendimientoEsperado; }
    public void    setRendimientoEsperado(Double v) { this.rendimientoEsperado = v; }

    public String  getDescripcion()              { return descripcion; }
    public void    setDescripcion(String v)      { this.descripcion = v; }

    @Override
    public String toString() {
        return nombreActividad != null ? nombreActividad : "Estandar #" + idEstandar;
    }
}
