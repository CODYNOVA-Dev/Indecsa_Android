package com.example.indecsa_v2.models;

/**
 * Refleja la entidad AvancePartida del backend.
 *
 * Backend usa objetos anidados (proyecto, cuadrilla, estandar, empleadoRegistro)
 * y no expone `unidadMedida`, `cantidadProgramada`, `observaciones` ni los
 * "nombre*" planos. Estos campos se conservan como cache local y como
 * atajos de lectura derivados de los objetos anidados.
 */
public class AvancePartidaDto {

    private Integer                idAvance;
    private ProyectoDto            proyecto;
    private CuadrillaDto           cuadrilla;
    private EstandarRendimientoDto estandar;
    private EmpleadoDto            empleadoRegistro;
    private String                 nombrePartida;
    private String                 fechaRegistro;
    private Double                 cantidadEjecutada;

    // ---- cache local solo-cliente (no enviado al backend) ----
    private transient String unidadMedidaLocal;
    private transient Double cantidadProgramadaLocal;
    private transient String observacionesLocal;

    public AvancePartidaDto() {}

    public Integer getIdAvance()             { return idAvance; }
    public void    setIdAvance(Integer v)    { this.idAvance = v; }

    public ProyectoDto getProyecto()             { return proyecto; }
    public void        setProyecto(ProyectoDto v){ this.proyecto = v; }

    public CuadrillaDto getCuadrilla()             { return cuadrilla; }
    public void         setCuadrilla(CuadrillaDto v){ this.cuadrilla = v; }

    public EstandarRendimientoDto getEstandar()             { return estandar; }
    public void                   setEstandar(EstandarRendimientoDto v){ this.estandar = v; }

    public EmpleadoDto getEmpleadoRegistro()           { return empleadoRegistro; }
    public void        setEmpleadoRegistro(EmpleadoDto v){ this.empleadoRegistro = v; }

    public String  getNombrePartida()         { return nombrePartida; }
    public void    setNombrePartida(String v) { this.nombrePartida = v; }

    public String  getFechaRegistro()         { return fechaRegistro; }
    public void    setFechaRegistro(String v) { this.fechaRegistro = v; }

    public Double  getCantidadEjecutada()        { return cantidadEjecutada; }
    public void    setCantidadEjecutada(Double v){ this.cantidadEjecutada = v; }

    // ---- atajos planos ----
    public Integer getIdProyecto() {
        return proyecto != null ? proyecto.getIdProyecto() : null;
    }
    public void setIdProyecto(Integer id) {
        if (id == null) { proyecto = null; return; }
        if (proyecto == null) proyecto = new ProyectoDto();
        proyecto.setIdProyecto(id);
    }

    public Integer getIdCuadrilla() {
        return cuadrilla != null ? cuadrilla.getIdCuadrilla() : null;
    }
    public void setIdCuadrilla(Integer id) {
        if (id == null) { cuadrilla = null; return; }
        if (cuadrilla == null) cuadrilla = new CuadrillaDto();
        cuadrilla.setIdCuadrilla(id);
    }

    public Integer getIdEstandar() {
        return estandar != null ? estandar.getIdEstandar() : null;
    }
    public void setIdEstandar(Integer id) {
        if (id == null) { estandar = null; return; }
        if (estandar == null) estandar = new EstandarRendimientoDto();
        estandar.setIdEstandar(id);
    }

    public Integer getIdEmpleadoRegistro() {
        return empleadoRegistro != null ? empleadoRegistro.getIdEmpleado() : null;
    }
    public void setIdEmpleadoRegistro(Integer id) {
        if (id == null) { empleadoRegistro = null; return; }
        if (empleadoRegistro == null) empleadoRegistro = new EmpleadoDto();
        empleadoRegistro.setIdEmpleado(id);
    }

    public String getNombreProyecto() {
        return proyecto != null ? proyecto.getNombreProyecto() : null;
    }

    public String getNombreCuadrilla() {
        return cuadrilla != null ? cuadrilla.getNombreCuadrilla() : null;
    }

    public String getNombreActividad() {
        return estandar != null ? estandar.getNombreActividad() : null;
    }

    /**
     * Backend NO almacena `unidadMedida` directamente en AvancePartida;
     * lo expone vía el estandar anidado. Si hay estandar, se usa su unidad.
     */
    public String getUnidadMedida() {
        if (estandar != null && estandar.getUnidadMedida() != null) {
            return estandar.getUnidadMedida();
        }
        return unidadMedidaLocal;
    }
    public void setUnidadMedida(String v) { this.unidadMedidaLocal = v; }

    /** Backend no persiste cantidadProgramada en AvancePartida. */
    public Double getCantidadProgramada() { return cantidadProgramadaLocal; }
    public void   setCantidadProgramada(Double v) { this.cantidadProgramadaLocal = v; }

    /** Backend no persiste observaciones en AvancePartida. */
    public String getObservaciones() { return observacionesLocal; }
    public void   setObservaciones(String v) { this.observacionesLocal = v; }
}
