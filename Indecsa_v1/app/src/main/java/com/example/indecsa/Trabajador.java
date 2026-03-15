package com.example.indecsa;

import com.example.indecsa.models.TrabajadorDto;
import java.util.ArrayList;
import java.util.List;

public class Trabajador {
    private String nombre;
    private String descripcion;
    private String especialidad;
    private String estado;
    private int experiencia;
    private String disponibilidad;
    private int imagenResId;
    private Integer id;
    private String nss;

    public Trabajador(String nombre, String descripcion, String especialidad,
                      String estado, int experiencia, String disponibilidad, int imagenResId) {
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.especialidad = especialidad;
        this.estado = estado;
        this.experiencia = experiencia;
        this.disponibilidad = disponibilidad;
        this.imagenResId = imagenResId;
    }

    public Trabajador(Integer id, String nss, String nombre, String descripcion, String especialidad,
                      String estado, int experiencia, String disponibilidad, int imagenResId) {
        this.id = id;
        this.nss = nss;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.especialidad = especialidad;
        this.estado = estado;
        this.experiencia = experiencia;
        this.disponibilidad = disponibilidad;
        this.imagenResId = imagenResId;
    }

    public String getNombre() { return nombre; }
    public String getDescripcion() { return descripcion; }
    public String getEspecialidad() { return especialidad; }
    public String getEstado() { return estado; }
    public int getExperiencia() { return experiencia; }
    public String getDisponibilidad() { return disponibilidad; }
    public int getImagenResId() { return imagenResId; }
    public Integer getId() { return id; }
    public String getNss() { return nss; }

    public void setNombre(String nombre) { this.nombre = nombre; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
    public void setEspecialidad(String especialidad) { this.especialidad = especialidad; }
    public void setEstado(String estado) { this.estado = estado; }
    public void setExperiencia(int experiencia) { this.experiencia = experiencia; }
    public void setDisponibilidad(String disponibilidad) { this.disponibilidad = disponibilidad; }
    public void setId(Integer id) { this.id = id; }
    public void setNss(String nss) { this.nss = nss; }

    public static Trabajador fromDto(TrabajadorDto dto) {
        return new Trabajador(
                dto.getIdTrabajador(),
                dto.getNssTrabajador(),
                dto.getNombreTrabajador(),
                dto.getDescripcionTrabajador() != null ? dto.getDescripcionTrabajador() : "Sin descripción",
                dto.getEspecialidadTrabajador(),
                dto.getEstadoTrabajador(),
                0,
                "No especificado",
                R.drawable.usuario
        );
    }

    public static List<Trabajador> fromDtoList(List<TrabajadorDto> dtoList) {
        List<Trabajador> trabajadores = new ArrayList<>();
        if (dtoList != null) {
            for (TrabajadorDto dto : dtoList) {
                trabajadores.add(fromDto(dto));
            }
        }
        return trabajadores;
    }

    public TrabajadorDto toDto() {
        return new TrabajadorDto(
                this.id,
                this.nss != null ? this.nss : "00000000000",
                this.nombre,
                this.especialidad,
                this.estado,
                this.descripcion
        );
    }
}