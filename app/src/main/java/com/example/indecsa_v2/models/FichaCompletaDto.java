package com.example.indecsa_v2.models;

import com.google.gson.annotations.SerializedName;

public class FichaCompletaDto {
    @SerializedName("idFicha")
    private Integer idFicha;

    @SerializedName("fichaEstado")
    private String fichaEstado;

    @SerializedName("fichaEspecialidad")
    private String fichaEspecialidad;

    @SerializedName("nombreContratista")
    private String nombreContratista;

    @SerializedName("descripcionContratista")
    private String descripcionContratista;

    @SerializedName("telefonoContratista")
    private String telefonoContratista;

    @SerializedName("correoContratista")
    private String correoContratista;

    @SerializedName("especialidadContratista")
    private String especialidadContratista;

    @SerializedName("nombreProyecto")
    private String nombreProyecto;

    @SerializedName("lugarProyecto")
    private String lugarProyecto;

    @SerializedName("tipoProyecto")
    private String tipoProyecto;

    @SerializedName("equipoTrabajo")
    private String equipoTrabajo;
    // Añade este campo:
    @SerializedName("idProyecto")
    private Integer idProyecto;

    // Y su getter/setter:
    public Integer getIdProyecto() { return idProyecto; }
    public void setIdProyecto(Integer idProyecto) { this.idProyecto = idProyecto; }



    // Getters y Setters
    public Integer getIdFicha() { return idFicha; }
    public void setIdFicha(Integer idFicha) { this.idFicha = idFicha; }

    public String getFichaEstado() { return fichaEstado; }
    public void setFichaEstado(String fichaEstado) { this.fichaEstado = fichaEstado; }

    public String getFichaEspecialidad() { return fichaEspecialidad; }
    public void setFichaEspecialidad(String fichaEspecialidad) { this.fichaEspecialidad = fichaEspecialidad; }

    public String getNombreContratista() { return nombreContratista; }
    public void setNombreContratista(String nombreContratista) { this.nombreContratista = nombreContratista; }

    public String getDescripcionContratista() { return descripcionContratista; }
    public void setDescripcionContratista(String descripcionContratista) { this.descripcionContratista = descripcionContratista; }

    public String getTelefonoContratista() { return telefonoContratista; }
    public void setTelefonoContratista(String telefonoContratista) { this.telefonoContratista = telefonoContratista; }

    public String getCorreoContratista() { return correoContratista; }
    public void setCorreoContratista(String correoContratista) { this.correoContratista = correoContratista; }

    public String getEspecialidadContratista() { return especialidadContratista; }
    public void setEspecialidadContratista(String especialidadContratista) { this.especialidadContratista = especialidadContratista; }

    public String getNombreProyecto() { return nombreProyecto; }
    public void setNombreProyecto(String nombreProyecto) { this.nombreProyecto = nombreProyecto; }

    public String getLugarProyecto() { return lugarProyecto; }
    public void setLugarProyecto(String lugarProyecto) { this.lugarProyecto = lugarProyecto; }

    public String getTipoProyecto() { return tipoProyecto; }
    public void setTipoProyecto(String tipoProyecto) { this.tipoProyecto = tipoProyecto; }

    public String getEquipoTrabajo() { return equipoTrabajo; }
    public void setEquipoTrabajo(String equipoTrabajo) { this.equipoTrabajo = equipoTrabajo; }
}