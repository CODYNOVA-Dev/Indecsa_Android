package com.example.indecsa_v2.models;

import com.google.gson.annotations.SerializedName;

/**
 * NUEVO: Reemplaza a LoginResponse.
 *
 * Coincide exactamente con LoginResponseDTO del backend:
 *   { idEmpleado, nombreEmpleado, correoEmpleado, nombreRol }
 *
 * nombreRol vale "ADMIN" o "CAPITAL_HUMANO".
 * IngresarContrasenaFragment ya lo usa con getNombreRol().
 */
public class LoginResponseDto {

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
}