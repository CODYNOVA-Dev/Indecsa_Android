package com.example.indecsa_v2.models;

import com.google.gson.annotations.SerializedName;

/**
 * Coincide con LoginResponse del backend (POST /api/v1/empleados/login).
 * Devuelve los datos del empleado autenticado, con el rol "aplanado" en
 * idRol / nombreRol / descripcionRol.
 */
public class LoginResponseDto {

    @SerializedName("idEmpleado")
    private Integer idEmpleado;

    @SerializedName("nombreEmpleado")
    private String nombreEmpleado;

    @SerializedName("correoEmpleado")
    private String correoEmpleado;

    @SerializedName("curp")
    private String curp;

    @SerializedName("fotoPerfilUrl")
    private String fotoPerfilUrl;

    @SerializedName("idRol")
    private Integer idRol;

    @SerializedName("nombreRol")
    private String nombreRol;

    @SerializedName("descripcionRol")
    private String descripcionRol;

    public Integer getIdEmpleado()    { return idEmpleado; }
    public String  getNombreEmpleado(){ return nombreEmpleado; }
    public String  getCorreoEmpleado(){ return correoEmpleado; }
    public String  getCurp()          { return curp; }
    public String  getFotoPerfilUrl() { return fotoPerfilUrl; }
    public Integer getIdRol()         { return idRol; }
    public String  getNombreRol()     { return nombreRol; }
    public String  getDescripcionRol(){ return descripcionRol; }
}
