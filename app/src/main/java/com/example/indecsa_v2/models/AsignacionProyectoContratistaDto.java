package com.example.indecsa_v2.models;

/**
 * Refleja la entidad AsignacionProyectoContratista del backend.
 * Vincula un Proyecto con un Contratista (contrato marco) sobre el que luego
 * se cuelgan asignaciones de trabajadores.
 */
public class AsignacionProyectoContratistaDto {

    private Integer       idAsignacionPc;
    private ProyectoDto   proyecto;
    private Contratista   contratista;
    private String        numeroContrato;
    private String        fechaInicio;
    private String        fechaFinEstimada;
    private Integer       personalAsignado;
    private String        estatusContrato; // ACTIVO, VIGENTE, SUSPENDIDO, FINALIZADO, CANCELADO
    private String        observaciones;

    public AsignacionProyectoContratistaDto() {}

    public AsignacionProyectoContratistaDto(Integer idAsignacionPc) {
        this.idAsignacionPc = idAsignacionPc;
    }

    public Integer getIdAsignacionPc()              { return idAsignacionPc; }
    public void    setIdAsignacionPc(Integer v)     { this.idAsignacionPc = v; }

    public ProyectoDto getProyecto()                { return proyecto; }
    public void        setProyecto(ProyectoDto v)   { this.proyecto = v; }

    public Contratista getContratista()             { return contratista; }
    public void        setContratista(Contratista v){ this.contratista = v; }

    public String  getNumeroContrato()              { return numeroContrato; }
    public void    setNumeroContrato(String v)      { this.numeroContrato = v; }

    public String  getFechaInicio()                 { return fechaInicio; }
    public void    setFechaInicio(String v)         { this.fechaInicio = v; }

    public String  getFechaFinEstimada()            { return fechaFinEstimada; }
    public void    setFechaFinEstimada(String v)    { this.fechaFinEstimada = v; }

    public Integer getPersonalAsignado()            { return personalAsignado; }
    public void    setPersonalAsignado(Integer v)   { this.personalAsignado = v; }

    public String  getEstatusContrato()             { return estatusContrato; }
    public void    setEstatusContrato(String v)     { this.estatusContrato = v; }

    public String  getObservaciones()               { return observaciones; }
    public void    setObservaciones(String v)       { this.observaciones = v; }

    // ---- atajos ----
    public Integer getIdProyecto() {
        return proyecto != null ? proyecto.getIdProyecto() : null;
    }
    public void setIdProyecto(Integer id) {
        if (proyecto == null) proyecto = new ProyectoDto();
        proyecto.setIdProyecto(id);
    }

    public Integer getIdContratista() {
        return contratista != null ? contratista.getIdContratista() : null;
    }
    public void setIdContratista(Integer id) {
        if (contratista == null) contratista = new Contratista();
        contratista.setIdContratista(id);
    }
}
