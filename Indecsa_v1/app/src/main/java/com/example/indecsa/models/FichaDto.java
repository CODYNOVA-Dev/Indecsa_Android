package com.example.indecsa.models;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class FichaDto {
    @SerializedName("idFicha")
    private Integer idFicha;

    @SerializedName("idContratista")
    private Integer idContratista;

    @SerializedName("idProyecto")
    private Integer idProyecto;

    @SerializedName("fichaEstado")
    private String fichaEstado;

    @SerializedName("fichaEspecialidad")
    private String fichaEspecialidad;

    @SerializedName("trabajadoresIds")
    private List<Integer> trabajadoresIds;

    public FichaDto() {}

    // Getters y Setters
    public Integer getIdFicha() { return idFicha; }
    public void setIdFicha(Integer idFicha) { this.idFicha = idFicha; }

    public Integer getIdContratista() { return idContratista; }
    public void setIdContratista(Integer idContratista) { this.idContratista = idContratista; }

    public Integer getIdProyecto() { return idProyecto; }
    public void setIdProyecto(Integer idProyecto) { this.idProyecto = idProyecto; }

    public String getFichaEstado() { return fichaEstado; }
    public void setFichaEstado(String fichaEstado) { this.fichaEstado = fichaEstado; }

    public String getFichaEspecialidad() { return fichaEspecialidad; }
    public void setFichaEspecialidad(String fichaEspecialidad) { this.fichaEspecialidad = fichaEspecialidad; }

    public List<Integer> getTrabajadoresIds() { return trabajadoresIds; }
    public void setTrabajadoresIds(List<Integer> trabajadoresIds) { this.trabajadoresIds = trabajadoresIds; }
}