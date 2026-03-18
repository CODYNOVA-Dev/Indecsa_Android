package com.example.indecsa_v2.models;

import com.google.gson.annotations.SerializedName;

/**
 * NUEVO: Reemplaza a LoginRequestAdmin y LoginRequestCapHum.
 *
 * El backend ahora tiene un único endpoint POST /api/v1/empleados/login
 * que acepta { correoEmpleado, contrasena } y devuelve el rol en
 * LoginResponseDto. Ya no existen endpoints separados por rol.
 */
public class LoginRequestDto {

    @SerializedName("correoEmpleado")
    private String correoEmpleado;

    @SerializedName("contrasena")
    private String contrasena;

    public LoginRequestDto(String correoEmpleado, String contrasena) {
        this.correoEmpleado = correoEmpleado;
        this.contrasena = contrasena;
    }

    public String getCorreoEmpleado() { return correoEmpleado; }
    public String getContrasena()     { return contrasena; }
}