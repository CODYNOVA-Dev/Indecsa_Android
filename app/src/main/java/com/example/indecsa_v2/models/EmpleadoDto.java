package com.example.indecsa_v2.models;

import com.google.gson.annotations.SerializedName;

/**
 * Refleja la entidad Empleado del backend (id, nombre, curp, correo, contrasena,
 * foto y un Rol anidado). Mantiene getNombreRol() / setNombreRol() como atajos
 * planos para no romper código de UI que los usaba.
 */
public class EmpleadoDto {

    @SerializedName("idEmpleado")     private Integer idEmpleado;
    @SerializedName("nombreEmpleado") private String  nombreEmpleado;
    @SerializedName("curp")           private String  curp;
    @SerializedName("correoEmpleado") private String  correoEmpleado;
    @SerializedName("contrasena")     private String  contrasena;
    @SerializedName("fotoPerfilUrl")  private String  fotoPerfilUrl;
    @SerializedName("rol")            private Rol     rol;

    public EmpleadoDto() {}

    public Integer getIdEmpleado()         { return idEmpleado; }
    public void    setIdEmpleado(Integer v){ this.idEmpleado = v; }

    public String  getNombreEmpleado()         { return nombreEmpleado; }
    public void    setNombreEmpleado(String v) { this.nombreEmpleado = v; }

    public String  getCurp()                   { return curp; }
    public void    setCurp(String v)           { this.curp = v; }

    public String  getCorreoEmpleado()         { return correoEmpleado; }
    public void    setCorreoEmpleado(String v) { this.correoEmpleado = v; }

    public String  getContrasena()             { return contrasena; }
    public void    setContrasena(String v)     { this.contrasena = v; }

    public String  getFotoPerfilUrl()          { return fotoPerfilUrl; }
    public void    setFotoPerfilUrl(String v)  { this.fotoPerfilUrl = v; }

    public Rol     getRol()                    { return rol; }
    public void    setRol(Rol v)               { this.rol = v; }

    // ---- atajos planos ----
    public String getNombreRol() {
        return rol != null ? rol.getNombreRol() : null;
    }
    public void setNombreRol(String nombreRol) {
        if (rol == null) rol = new Rol();
        rol.setNombreRol(nombreRol);
    }

    public Integer getIdRol() {
        return rol != null ? rol.getIdRol() : null;
    }
    public void setIdRol(Integer idRol) {
        if (rol == null) rol = new Rol();
        rol.setIdRol(idRol);
    }
}
