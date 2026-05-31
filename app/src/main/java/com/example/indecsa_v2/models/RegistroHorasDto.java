package com.example.indecsa_v2.models;

import com.google.gson.annotations.SerializedName;

/**
 * Refleja la entidad RegistroHoras del backend.
 *
 * Backend usa objetos anidados (asignacionTrabajadorProyecto, cuadrilla,
 * empleadoRegistro) y no tiene los campos `tipoPeriodo` ni `observaciones`
 * ni los flat ids planos `idTrabajador`/`idProyecto`. Estos se conservan
 * como cache local o se derivan de los anidados.
 */
public class RegistroHorasDto {

    @SerializedName("idRegistro")                   private Integer                          idRegistro;
    @SerializedName("asignacionTrabajadorProyecto") private AsignacionTrabajadorProyectoDto  asignacionTrabajadorProyecto;
    @SerializedName("cuadrilla")                    private CuadrillaDto                     cuadrilla;
    @SerializedName("empleadoRegistro")             private EmpleadoDto                      empleadoRegistro;
    @SerializedName("fechaRegistro")                private String                           fechaRegistro;
    @SerializedName("horasTrabajadas")              private Double                           horasTrabajadas;

    private transient String tipoPeriodoLocal;
    private transient String observacionesLocal;

    public RegistroHorasDto() {}

    public Integer getIdRegistro()            { return idRegistro; }
    public void    setIdRegistro(Integer v)   { this.idRegistro = v; }

    public AsignacionTrabajadorProyectoDto getAsignacionTrabajadorProyecto()                    { return asignacionTrabajadorProyecto; }
    public void                            setAsignacionTrabajadorProyecto(AsignacionTrabajadorProyectoDto v) { this.asignacionTrabajadorProyecto = v; }

    public CuadrillaDto getCuadrilla()              { return cuadrilla; }
    public void         setCuadrilla(CuadrillaDto v){ this.cuadrilla = v; }

    public EmpleadoDto getEmpleadoRegistro()             { return empleadoRegistro; }
    public void        setEmpleadoRegistro(EmpleadoDto v){ this.empleadoRegistro = v; }

    public String  getFechaRegistro()         { return fechaRegistro; }
    public void    setFechaRegistro(String v) { this.fechaRegistro = v; }

    public Double  getHorasTrabajadas()        { return horasTrabajadas; }
    public void    setHorasTrabajadas(Double v){ this.horasTrabajadas = v; }

    // ---- atajos planos ----
    public Integer getIdAsignacionTp() {
        return asignacionTrabajadorProyecto != null
                ? asignacionTrabajadorProyecto.getIdAsignacionTp()
                : null;
    }
    public void setIdAsignacionTp(Integer id) {
        if (id == null) { asignacionTrabajadorProyecto = null; return; }
        if (asignacionTrabajadorProyecto == null) {
            asignacionTrabajadorProyecto = new AsignacionTrabajadorProyectoDto();
        }
        asignacionTrabajadorProyecto.setIdAsignacionTp(id);
    }

    public Integer getIdCuadrilla() {
        return cuadrilla != null ? cuadrilla.getIdCuadrilla() : null;
    }
    public void setIdCuadrilla(Integer id) {
        if (id == null) { cuadrilla = null; return; }
        if (cuadrilla == null) cuadrilla = new CuadrillaDto();
        cuadrilla.setIdCuadrilla(id);
    }

    public Integer getIdEmpleadoRegistro() {
        return empleadoRegistro != null ? empleadoRegistro.getIdEmpleado() : null;
    }
    public void setIdEmpleadoRegistro(Integer id) {
        if (id == null) { empleadoRegistro = null; return; }
        if (empleadoRegistro == null) empleadoRegistro = new EmpleadoDto();
        empleadoRegistro.setIdEmpleado(id);
    }

    public Integer getIdTrabajador() {
        if (asignacionTrabajadorProyecto == null) return null;
        return asignacionTrabajadorProyecto.getIdTrabajador();
    }

    public String getNombreTrabajador() {
        if (asignacionTrabajadorProyecto != null && asignacionTrabajadorProyecto.getTrabajador() != null) {
            return asignacionTrabajadorProyecto.getTrabajador().getNombreTrabajador();
        }
        return null;
    }

    public Integer getIdProyecto() {
        if (asignacionTrabajadorProyecto == null) return null;
        return asignacionTrabajadorProyecto.getIdProyecto();
    }

    public String getNombreProyecto() {
        if (asignacionTrabajadorProyecto != null && asignacionTrabajadorProyecto.getProyecto() != null) {
            return asignacionTrabajadorProyecto.getProyecto().getNombreProyecto();
        }
        return null;
    }

    public String getNombreCuadrilla() {
        return cuadrilla != null ? cuadrilla.getNombreCuadrilla() : null;
    }

    /** Backend no persiste tipoPeriodo en RegistroHoras. */
    public String getTipoPeriodo() { return tipoPeriodoLocal; }
    public void   setTipoPeriodo(String v) { this.tipoPeriodoLocal = v; }

    /** Backend no persiste observaciones en RegistroHoras. */
    public String getObservaciones() { return observacionesLocal; }
    public void   setObservaciones(String v) { this.observacionesLocal = v; }
}
