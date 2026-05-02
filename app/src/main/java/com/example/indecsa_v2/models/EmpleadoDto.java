package com.example.indecsa_v2.models;

import com.google.gson.annotations.SerializedName;

public class EmpleadoDto {

    @SerializedName("idEmpleado")
    private Integer idEmpleado;

    @SerializedName("nombreEmpleado")
    private String nombreEmpleado;

    @SerializedName("correoEmpleado")
    private String correoEmpleado;

    @SerializedName("nombreRol")
    private String nombreRol;

    public Integer getIdEmpleado()    { return idEmpleado; }
    public String getNombreEmpleado() { return nombreEmpleado; }
    public String getCorreoEmpleado() { return correoEmpleado; }
    public String getNombreRol()      { return nombreRol; }

    public void setIdEmpleado(Integer v)    { this.idEmpleado = v; }
    public void setNombreEmpleado(String v) { this.nombreEmpleado = v; }
    public void setCorreoEmpleado(String v) { this.correoEmpleado = v; }
    public void setNombreRol(String v)      { this.nombreRol = v; }
}