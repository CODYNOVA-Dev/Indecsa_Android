package com.example.indecsa_v2.models;

/**
 * Refleja la entidad Rol del backend. `nombreRol` es un enum
 * serializado como String: "ADMIN" o "CAPITAL_HUMANO".
 */
public class Rol {

    private Integer idRol;
    private String  nombreRol;
    private String  descripcionRol;

    public Rol() {}

    public Rol(Integer idRol) {
        this.idRol = idRol;
    }

    public Integer getIdRol()                { return idRol; }
    public void    setIdRol(Integer v)       { this.idRol = v; }

    public String  getNombreRol()            { return nombreRol; }
    public void    setNombreRol(String v)    { this.nombreRol = v; }

    public String  getDescripcionRol()       { return descripcionRol; }
    public void    setDescripcionRol(String v){ this.descripcionRol = v; }
}
