package com.example.indecsa_v2.models;

public class RegistroMigratorio {

    private Integer idMigratorio;
    private String  folioDocumento;
    private String  categoria;
    private String  fechaEmision;
    private Integer diasVigencia;
    private String  fechaVencimiento;
    private Boolean permisoTrabajo;
    private Boolean activo;

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
