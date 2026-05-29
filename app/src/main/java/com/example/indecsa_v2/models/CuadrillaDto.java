package com.example.indecsa_v2.models;

import com.google.gson.annotations.SerializedName;

/**
 * Refleja la entidad Cuadrilla del backend (proyecto anidado).
 *
 * El backend NO tiene una columna `observaciones` en esta tabla; el setter
 * existe solo como cache local (no viaja en JSON).
 */
public class CuadrillaDto {

    @SerializedName("idCuadrilla")      private Integer     idCuadrilla;
    @SerializedName("proyecto")         private ProyectoDto proyecto;
    @SerializedName("nombreCuadrilla")  private String      nombreCuadrilla;
    @SerializedName("frenteTrabajo")    private String      frenteTrabajo;
    @SerializedName("estatusCuadrilla") private String      estatusCuadrilla; // ACTIVO, INACTIVO

    private transient String observacionesLocal;

    public CuadrillaDto() {}

    public Integer getIdCuadrilla()         { return idCuadrilla; }
    public void    setIdCuadrilla(Integer v){ this.idCuadrilla = v; }

    public ProyectoDto getProyecto()           { return proyecto; }
    public void        setProyecto(ProyectoDto v){ this.proyecto = v; }

    public String getNombreCuadrilla()         { return nombreCuadrilla; }
    public void   setNombreCuadrilla(String v) { this.nombreCuadrilla = v; }

    public String getFrenteTrabajo()        { return frenteTrabajo; }
    public void   setFrenteTrabajo(String v){ this.frenteTrabajo = v; }

    public String getEstatusCuadrilla()      { return estatusCuadrilla; }
    public void   setEstatusCuadrilla(String v){ this.estatusCuadrilla = v; }

    // ---- atajos planos ----
    public Integer getIdProyecto() {
        return proyecto != null ? proyecto.getIdProyecto() : null;
    }
    public void setIdProyecto(Integer id) {
        if (id == null) { proyecto = null; return; }
        if (proyecto == null) proyecto = new ProyectoDto();
        proyecto.setIdProyecto(id);
    }

    public String getNombreProyecto() {
        return proyecto != null ? proyecto.getNombreProyecto() : null;
    }
    public void setNombreProyecto(String v) {
        if (proyecto == null) proyecto = new ProyectoDto();
        proyecto.setNombreProyecto(v);
    }

    public String getObservaciones() { return observacionesLocal; }
    public void   setObservaciones(String v) { this.observacionesLocal = v; }
}
