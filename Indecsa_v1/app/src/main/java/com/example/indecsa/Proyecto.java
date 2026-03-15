package com.example.indecsa;

public class Proyecto {
    private Integer idProyecto;
    private String proyectito;
    private String descripcion;
    private String contratista;
    private String especialidad;
    private String estado;
    private int avance;
    private String direccion;
    private int imagenResId;

    // Constructor con ID
    public Proyecto(Integer idProyecto, String proyectito, String descripcion, String contratista,
                    String especialidad, String estado, int avance, String direccion, int imagenResId) {
        this.idProyecto = idProyecto;
        this.proyectito = proyectito;
        this.descripcion = descripcion;
        this.contratista = contratista;
        this.especialidad = especialidad;
        this.estado = estado;
        this.avance = avance;
        this.direccion = direccion;
        this.imagenResId = imagenResId;
    }

    // Constructor sin ID (para crear nuevos)
    public Proyecto(String proyectito, String descripcion, String contratista,
                    String especialidad, String estado, int avance, String direccion, int imagenResId) {
        this(null, proyectito, descripcion, contratista, especialidad, estado, avance, direccion, imagenResId);
    }

    // Getters y Setters
    public Integer getIdProyecto() { return idProyecto; }
    public void setIdProyecto(Integer idProyecto) { this.idProyecto = idProyecto; }

    public String getProyectito() { return proyectito; }
    public void setProyectito(String proyectito) { this.proyectito = proyectito; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public String getContratista() { return contratista; }
    public void setContratista(String contratista) { this.contratista = contratista; }

    public String getEspecialidad() { return especialidad; }
    public void setEspecialidad(String especialidad) { this.especialidad = especialidad; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }

    public int getAvance() { return avance; }
    public void setAvance(int avance) { this.avance = avance; }

    public String getDireccion() { return direccion; }
    public void setDireccion(String direccion) { this.direccion = direccion; }

    public int getImagenResId() { return imagenResId; }
    public void setImagenResId(int imagenResId) { this.imagenResId = imagenResId; }
}