package com.example.indecsa_v2.models;

import com.google.gson.annotations.SerializedName;

/**
 * Catálogo Estado del backend.
 * Sirve tanto para "estado civil de calidad de vida" del Trabajador
 * como para "estado de operación" del Contratista, y referencia geográfica
 * en Domicilio.
 */
public class Estado {

    @SerializedName("idEstado")  private Integer idEstado;
    @SerializedName("nombreEst") private String  nombreEst;

    public Estado() {}

    public Estado(Integer idEstado) {
        this.idEstado = idEstado;
    }

    public Integer getIdEstado()           { return idEstado; }
    public void    setIdEstado(Integer v)  { this.idEstado = v; }

    public String  getNombreEst()          { return nombreEst; }
    public void    setNombreEst(String v)  { this.nombreEst = v; }

    @Override
    public String toString() {
        return nombreEst != null ? nombreEst : ("Estado #" + idEstado);
    }
}
