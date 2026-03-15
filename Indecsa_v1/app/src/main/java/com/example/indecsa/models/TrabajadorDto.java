package com.example.indecsa.models;

public class TrabajadorDto {
    private Integer idTrabajador;
    private String nssTrabajador;
    private String nombreTrabajador;
    private String especialidadTrabajador;
    private String estadoTrabajador;
    private String descripcionTrabajador;

    public TrabajadorDto() {
    }

    public TrabajadorDto(Integer idTrabajador, String nssTrabajador,
                         String nombreTrabajador, String especialidadTrabajador,
                         String estadoTrabajador, String descripcionTrabajador) {
        this.idTrabajador = idTrabajador;
        this.nssTrabajador = nssTrabajador;
        this.nombreTrabajador = nombreTrabajador;
        this.especialidadTrabajador = especialidadTrabajador;
        this.estadoTrabajador = estadoTrabajador;
        this.descripcionTrabajador = descripcionTrabajador;
    }

    public Integer getIdTrabajador() {
        return idTrabajador;
    }

    public void setIdTrabajador(Integer idTrabajador) {
        this.idTrabajador = idTrabajador;
    }

    public String getNssTrabajador() {
        return nssTrabajador;
    }

    public void setNssTrabajador(String nssTrabajador) {
        this.nssTrabajador = nssTrabajador;
    }

    public String getNombreTrabajador() {
        return nombreTrabajador;
    }

    public void setNombreTrabajador(String nombreTrabajador) {
        this.nombreTrabajador = nombreTrabajador;
    }

    public String getEspecialidadTrabajador() {
        return especialidadTrabajador;
    }

    public void setEspecialidadTrabajador(String especialidadTrabajador) {
        this.especialidadTrabajador = especialidadTrabajador;
    }

    public String getEstadoTrabajador() {
        return estadoTrabajador;
    }

    public void setEstadoTrabajador(String estadoTrabajador) {
        this.estadoTrabajador = estadoTrabajador;
    }

    public String getDescripcionTrabajador() {
        return descripcionTrabajador;
    }

    public void setDescripcionTrabajador(String descripcionTrabajador) {
        this.descripcionTrabajador = descripcionTrabajador;
    }


}