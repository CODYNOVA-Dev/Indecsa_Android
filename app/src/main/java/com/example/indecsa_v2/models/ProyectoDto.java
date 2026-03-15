package com.example.indecsa_v2.models;

import com.google.gson.annotations.SerializedName;

public class ProyectoDto {
    @SerializedName("idProyecto")
    private Integer idProyecto;

    @SerializedName("nombreProyecto")
    private String nombreProyecto;

    @SerializedName("tipoProyecto")
    private String tipoProyecto;

    @SerializedName("lugarProyecto")
    private String lugarProyecto;

    // Constructores
    public ProyectoDto() {}

    public ProyectoDto(String nombreProyecto, String tipoProyecto, String lugarProyecto) {
        this.nombreProyecto = nombreProyecto;
        this.tipoProyecto = tipoProyecto;
        this.lugarProyecto = lugarProyecto;
    }

    // Getters y Setters
    public Integer getIdProyecto() { return idProyecto; }
    public void setIdProyecto(Integer idProyecto) { this.idProyecto = idProyecto; }

    public String getNombreProyecto() { return nombreProyecto; }
    public void setNombreProyecto(String nombreProyecto) { this.nombreProyecto = nombreProyecto; }

    public String getTipoProyecto() { return tipoProyecto; }
    public void setTipoProyecto(String tipoProyecto) { this.tipoProyecto = tipoProyecto; }

    public String getLugarProyecto() { return lugarProyecto; }
    public void setLugarProyecto(String lugarProyecto) { this.lugarProyecto = lugarProyecto; }
}