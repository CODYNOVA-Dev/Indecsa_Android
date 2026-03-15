package com.example.indecsa_v2.models;

import com.google.gson.annotations.SerializedName;

public class Contratista {

    @SerializedName("idContratista")
    private Integer idContratista;

    @SerializedName("nombreContratista")
    private String nombreContratista;

    @SerializedName("estadoContratista")
    private String estadoContratista;

    @SerializedName("descripcionContratista")
    private String descripcionContratista;

    @SerializedName("calificacion")
    private Integer calificacion;

    @SerializedName("especialidad")
    private String especialidad;

    @SerializedName("telefono")
    private String telefono;

    @SerializedName("correo")
    private String correo;

    // Constructor vacío
    public Contratista() {}

    // GETTERS Y SETTERS
    public Integer getIdContratista() { return idContratista; }
    public void setIdContratista(Integer idContratista) { this.idContratista = idContratista; }

    public String getNombreContratista() { return nombreContratista; }
    public void setNombreContratista(String nombreContratista) { this.nombreContratista = nombreContratista; }

    public String getEstadoContratista() { return estadoContratista; }
    public void setEstadoContratista(String estadoContratista) { this.estadoContratista = estadoContratista; }

    public String getDescripcionContratista() { return descripcionContratista; }
    public void setDescripcionContratista(String descripcionContratista) { this.descripcionContratista = descripcionContratista; }

    public Integer getCalificacion() { return calificacion; }
    public void setCalificacion(Integer calificacion) { this.calificacion = calificacion; }

    public String getEspecialidad() { return especialidad; }
    public void setEspecialidad(String especialidad) { this.especialidad = especialidad; }

    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }

    public String getCorreo() { return correo; }
    public void setCorreo(String correo) { this.correo = correo; }
}