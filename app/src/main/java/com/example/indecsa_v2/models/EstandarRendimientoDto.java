package com.example.indecsa_v2.models;

import com.google.gson.annotations.SerializedName;

/**
 * Refleja la entidad EstandarRendimiento del backend.
 * Backend NO tiene un campo `descripcion`; se conserva como cache local
 * solo-cliente para no romper la UI legacy.
 */
public class EstandarRendimientoDto {

    @SerializedName("idEstandar")          private Integer idEstandar;
    @SerializedName("nombreActividad")     private String  nombreActividad;
    @SerializedName("unidadMedida")        private String  unidadMedida;        // m2, m3, ml, piezas, porcentaje
    @SerializedName("rendimientoEsperado") private Double  rendimientoEsperado;

    private transient String descripcionLocal;

    public EstandarRendimientoDto() {}

    public EstandarRendimientoDto(Integer idEstandar) {
        this.idEstandar = idEstandar;
    }

    public Integer getIdEstandar()              { return idEstandar; }
    public void    setIdEstandar(Integer v)     { this.idEstandar = v; }

    public String  getNombreActividad()          { return nombreActividad; }
    public void    setNombreActividad(String v)  { this.nombreActividad = v; }

    public String  getUnidadMedida()             { return unidadMedida; }
    public void    setUnidadMedida(String v)     { this.unidadMedida = v; }

    public Double  getRendimientoEsperado()         { return rendimientoEsperado; }
    public void    setRendimientoEsperado(Double v) { this.rendimientoEsperado = v; }

    public String  getDescripcion()              { return descripcionLocal; }
    public void    setDescripcion(String v)      { this.descripcionLocal = v; }

    @Override
    public String toString() {
        return nombreActividad != null ? nombreActividad : "Estandar #" + idEstandar;
    }
}
