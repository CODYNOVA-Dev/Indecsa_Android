package com.example.indecsa_v2.models;

import com.google.gson.annotations.SerializedName;

/**
 * Refleja la entidad AsignacionTrabajadorProyecto del backend.
 *
 * El backend serializa Trabajador, Proyecto y AsignacionProyectoContratista
 * como objetos anidados — no como FKs planas. Este DTO mantiene los atajos
 * planos (getIdTrabajador / setIdTrabajador, etc.) para que la UI existente
 * no necesite cambios.
 *
 * El backend NO tiene una columna `observaciones` en esta tabla; el setter
 * existe solo como cache local (no viaja en JSON).
 */
public class AsignacionTrabajadorProyectoDto {

    @SerializedName("idAsignacionTp")                private Integer                          idAsignacionTp;
    @SerializedName("trabajador")                    private TrabajadorDto                    trabajador;
    @SerializedName("proyecto")                      private ProyectoDto                      proyecto;
    @SerializedName("asignacionProyectoContratista") private AsignacionProyectoContratistaDto asignacionProyectoContratista;
    @SerializedName("puestoEnProyecto")              private String                           puestoEnProyecto;
    @SerializedName("fechaInicio")                   private String                           fechaInicio;
    @SerializedName("fechaFinEstimada")              private String                           fechaFinEstimada;
    @SerializedName("estatusAsignacion")             private String                           estatusAsignacion; // ACTIVO, SUSPENDIDO, INCAPACIDAD, CANCELADO, VACACIONES, FINALIZADO

    private transient String                 observacionesLocal;

    public AsignacionTrabajadorProyectoDto() {}

    public Integer getIdAsignacionTp()           { return idAsignacionTp; }
    public void    setIdAsignacionTp(Integer v)  { this.idAsignacionTp = v; }

    public TrabajadorDto getTrabajador()                  { return trabajador; }
    public void          setTrabajador(TrabajadorDto v)   { this.trabajador = v; }

    public ProyectoDto getProyecto()                  { return proyecto; }
    public void        setProyecto(ProyectoDto v)     { this.proyecto = v; }

    public AsignacionProyectoContratistaDto getAsignacionProyectoContratista() { return asignacionProyectoContratista; }
    public void setAsignacionProyectoContratista(AsignacionProyectoContratistaDto v) { this.asignacionProyectoContratista = v; }

    public String getPuestoEnProyecto()             { return puestoEnProyecto; }
    public void   setPuestoEnProyecto(String v)     { this.puestoEnProyecto = v; }

    public String getFechaInicio()                  { return fechaInicio; }
    public void   setFechaInicio(String v)          { this.fechaInicio = v; }

    public String getFechaFinEstimada()             { return fechaFinEstimada; }
    public void   setFechaFinEstimada(String v)     { this.fechaFinEstimada = v; }

    public String getEstatusAsignacion()            { return estatusAsignacion; }
    public void   setEstatusAsignacion(String v)    { this.estatusAsignacion = v; }

    // ---- atajos planos ----

    public Integer getIdTrabajador() {
        return trabajador != null ? trabajador.getIdTrabajador() : null;
    }
    public void setIdTrabajador(Integer id) {
        if (id == null) { trabajador = null; return; }
        if (trabajador == null) trabajador = new TrabajadorDto();
        trabajador.setIdTrabajador(id);
    }

    public Integer getIdProyecto() {
        return proyecto != null ? proyecto.getIdProyecto() : null;
    }
    public void setIdProyecto(Integer id) {
        if (id == null) { proyecto = null; return; }
        if (proyecto == null) proyecto = new ProyectoDto();
        proyecto.setIdProyecto(id);
    }

    public Integer getIdAsignacionPc() {
        return asignacionProyectoContratista != null
                ? asignacionProyectoContratista.getIdAsignacionPc()
                : null;
    }
    public void setIdAsignacionPc(Integer id) {
        if (id == null) { asignacionProyectoContratista = null; return; }
        if (asignacionProyectoContratista == null) {
            asignacionProyectoContratista = new AsignacionProyectoContratistaDto();
        }
        asignacionProyectoContratista.setIdAsignacionPc(id);
    }

    /** Cache local. Backend no persiste este campo en esta tabla. */
    public String getObservaciones() { return observacionesLocal; }
    public void   setObservaciones(String v) { this.observacionesLocal = v; }
}
