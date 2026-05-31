package com.example.indecsa_v2.models;

import com.google.gson.annotations.SerializedName;

public class RegistroMigratorio {

    @SerializedName("idMigratorio")     private Integer idMigratorio;
    @SerializedName("folioDocumento")   private String  folioDocumento;
    @SerializedName("categoria")        private String  categoria;
    @SerializedName("fechaEmision")     private String  fechaEmision;
    @SerializedName("diasVigencia")     private Integer diasVigencia;
    @SerializedName("fechaVencimiento") private String  fechaVencimiento;
    @SerializedName("permisoTrabajo")   private Boolean permisoTrabajo;
    @SerializedName("activo")           private Boolean activo;

    public RegistroMigratorio() {}

    public RegistroMigratorio(Integer idMigratorio) {
        this.idMigratorio = idMigratorio;
    }

    public Integer getIdMigratorio()           { return idMigratorio; }
    public void    setIdMigratorio(Integer v)  { this.idMigratorio = v; }

    public String  getFolioDocumento()         { return folioDocumento; }
    public void    setFolioDocumento(String v) { this.folioDocumento = v; }

    public String  getCategoria()              { return categoria; }
    public void    setCategoria(String v)      { this.categoria = v; }

    public String  getFechaEmision()           { return fechaEmision; }
    public void    setFechaEmision(String v)   { this.fechaEmision = v; }

    public Integer getDiasVigencia()           { return diasVigencia; }
    public void    setDiasVigencia(Integer v)  { this.diasVigencia = v; }

    public String  getFechaVencimiento()       { return fechaVencimiento; }
    public void    setFechaVencimiento(String v){ this.fechaVencimiento = v; }

    public Boolean getPermisoTrabajo()         { return permisoTrabajo; }
    public void    setPermisoTrabajo(Boolean v){ this.permisoTrabajo = v; }

    public Boolean getActivo()                 { return activo; }
    public void    setActivo(Boolean v)        { this.activo = v; }
}
