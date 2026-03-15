package com.example.indecsa;

import com.example.indecsa.models.FichaCompletaDto;

import java.util.List;

public class Ficha {
    private Integer idFicha;
    private String nombreContratista;
    private String descripcionContratista;
    private String telefonoContratista;
    private String correoContratista;
    private String especialidadContratista;
    private String nombreProyecto;
    private String lugarProyecto;
    private String tipoProyecto;
    private String fichaEstado;
    private String fichaEspecialidad;
    private String equipoTrabajo;

    // Constructor simple (para datos mock)
    public Ficha(String nombre, String descripcion, String proyecto, String equipo) {
        this.nombreContratista = nombre;
        this.descripcionContratista = descripcion;
        this.nombreProyecto = proyecto;
        this.equipoTrabajo = equipo;
    }

    // Constructor completo
    public Ficha(Integer idFicha, String nombreContratista, String descripcionContratista,
                 String telefonoContratista, String correoContratista, String especialidadContratista,
                 String nombreProyecto, String lugarProyecto, String tipoProyecto,
                 String fichaEstado, String fichaEspecialidad, String equipoTrabajo) {
        this.idFicha = idFicha;
        this.nombreContratista = nombreContratista;
        this.descripcionContratista = descripcionContratista;
        this.telefonoContratista = telefonoContratista;
        this.correoContratista = correoContratista;
        this.especialidadContratista = especialidadContratista;
        this.nombreProyecto = nombreProyecto;
        this.lugarProyecto = lugarProyecto;
        this.tipoProyecto = tipoProyecto;
        this.fichaEstado = fichaEstado;
        this.fichaEspecialidad = fichaEspecialidad;
        this.equipoTrabajo = equipoTrabajo;
    }

    // Método para convertir de FichaCompletaDto
    public static Ficha fromCompletaDto(FichaCompletaDto dto) {
        return new Ficha(
                dto.getIdFicha(),
                dto.getNombreContratista(),
                dto.getDescripcionContratista(),
                dto.getTelefonoContratista(),
                dto.getCorreoContratista(),
                dto.getEspecialidadContratista(),
                dto.getNombreProyecto(),
                dto.getLugarProyecto(),
                dto.getTipoProyecto(),
                dto.getFichaEstado(),
                dto.getFichaEspecialidad(),
                dto.getEquipoTrabajo()
        );
    }

    // Método para convertir lista
    public static List<Ficha> fromCompletaDtoList(List<FichaCompletaDto> dtoList) {
        List<Ficha> fichas = new java.util.ArrayList<>();
        if (dtoList != null) {
            for (FichaCompletaDto dto : dtoList) {
                fichas.add(fromCompletaDto(dto));
            }
        }
        return fichas;
    }

    // Getters y Setters
    public Integer getIdFicha() { return idFicha; }
    public void setIdFicha(Integer idFicha) { this.idFicha = idFicha; }

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

    public String getFichaEstado() { return fichaEstado; }
    public void setFichaEstado(String fichaEstado) { this.fichaEstado = fichaEstado; }

    public String getFichaEspecialidad() { return fichaEspecialidad; }
    public void setFichaEspecialidad(String fichaEspecialidad) { this.fichaEspecialidad = fichaEspecialidad; }

    public String getEquipoTrabajo() { return equipoTrabajo; }
    public void setEquipoTrabajo(String equipoTrabajo) { this.equipoTrabajo = equipoTrabajo; }

    // Métodos para compatibilidad con tu código existente
    public String getNombre() { return nombreContratista; }
    public String getDescripcion() { return descripcionContratista; }
    public String getProyecto() { return nombreProyecto; }
    public String getEquipo() { return equipoTrabajo; }
}