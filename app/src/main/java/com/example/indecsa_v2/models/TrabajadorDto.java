package com.example.indecsa_v2.models;

/**
 * Refleja la entidad Trabajador del backend.
 *
 * El backend requiere muchos campos NOT NULL para crear un Trabajador:
 *   curp, rfc, nacionalidad, puesto, descPuesto, escolaridad, contratacion,
 *   jornada, sexo, fechaIngreso, domicilio, estadoCalidadVida.
 * La UI actual (AgregarTrabajadorDialog) solo captura un subconjunto; los
 * trabajadores creados sin esos campos fallarán con 400 en el backend.
 *
 * Para no romper la UI existente, este DTO conserva los getters/setters
 * "planos" (descripcionTrabajador, ubicacionTrabajador, calificacionTrabajador)
 * como atajos sobre los campos reales del backend.
 */
public class TrabajadorDto {

    // ---- campos serializados al/desde backend ----
    private Integer idTrabajador;
    private String  nombreTrabajador;
    private String  curp;
    private String  rfc;
    private String  nssTrabajador;
    private String  nacionalidad;
    private RegistroMigratorio registroMigratorio;
    private Domicilio domicilio;
    private String  fotoPerfilUrl;
    private String  puesto;
    private String  descPuesto;
    private String  especialidadTrabajador;
    private String  escolaridad;
    private String  experiencia;
    private String  telefonoTrabajador;
    private String  correoTrabajador;
    private String  contratacion;
    private String  jornada;
    private String  estadoTrabajador; // enum: DESCANSO, VACACIONES, INCAPACIDAD, ACTIVO, INACTIVO, BAJA, BOLETINADO
    private Integer evaluacionTrabajador; // backend es Byte; Gson lo deserializa OK
    private String  fechaIngreso;     // ISO yyyy-MM-dd
    private Estado  estadoCalidadVida;
    private String  sexo;             // Masculino, Femenino, Otro
    private String  antPenal;
    private String  deudorAlim;
    private String  folioLicCond;
    private String  estadoCivil;
    private String  idiomas;
    private String  lenguaIndigena;

    // ---- cache local solo-cliente (no viaja al backend) ----
    private transient String descripcionTrabajadorLocal;
    private transient String ubicacionTrabajadorLocal;

    public TrabajadorDto() {}

    public TrabajadorDto(Integer idTrabajador) {
        this.idTrabajador = idTrabajador;
    }

    // ---- getters / setters directos ----
    public Integer getIdTrabajador()         { return idTrabajador; }
    public void    setIdTrabajador(Integer v){ this.idTrabajador = v; }

    public String  getNombreTrabajador()         { return nombreTrabajador; }
    public void    setNombreTrabajador(String v) { this.nombreTrabajador = v; }

    public String  getCurp()             { return curp; }
    public void    setCurp(String v)     { this.curp = v; }

    public String  getRfc()              { return rfc; }
    public void    setRfc(String v)      { this.rfc = v; }

    public String  getNssTrabajador()         { return nssTrabajador; }
    public void    setNssTrabajador(String v) { this.nssTrabajador = v; }

    public String  getNacionalidad()       { return nacionalidad; }
    public void    setNacionalidad(String v){ this.nacionalidad = v; }

    public RegistroMigratorio getRegistroMigratorio()       { return registroMigratorio; }
    public void               setRegistroMigratorio(RegistroMigratorio v){ this.registroMigratorio = v; }

    public Domicilio getDomicilio()         { return domicilio; }
    public void      setDomicilio(Domicilio v){ this.domicilio = v; }

    public String  getFotoPerfilUrl()       { return fotoPerfilUrl; }
    public void    setFotoPerfilUrl(String v){ this.fotoPerfilUrl = v; }

    public String  getPuesto()           { return puesto; }
    public void    setPuesto(String v)   { this.puesto = v; }

    public String  getDescPuesto()       { return descPuesto; }
    public void    setDescPuesto(String v){ this.descPuesto = v; }

    public String  getEspecialidadTrabajador()         { return especialidadTrabajador; }
    public void    setEspecialidadTrabajador(String v) { this.especialidadTrabajador = v; }

    public String  getEscolaridad()       { return escolaridad; }
    public void    setEscolaridad(String v){ this.escolaridad = v; }

    public String  getExperiencia()       { return experiencia; }
    public void    setExperiencia(String v){ this.experiencia = v; }

    public String  getTelefonoTrabajador()         { return telefonoTrabajador; }
    public void    setTelefonoTrabajador(String v) { this.telefonoTrabajador = v; }

    public String  getCorreoTrabajador()         { return correoTrabajador; }
    public void    setCorreoTrabajador(String v) { this.correoTrabajador = v; }

    public String  getContratacion()       { return contratacion; }
    public void    setContratacion(String v){ this.contratacion = v; }

    public String  getJornada()       { return jornada; }
    public void    setJornada(String v){ this.jornada = v; }

    public String  getEstadoTrabajador()         { return estadoTrabajador; }
    public void    setEstadoTrabajador(String v) { this.estadoTrabajador = v; }

    public Integer getEvaluacionTrabajador()         { return evaluacionTrabajador; }
    public void    setEvaluacionTrabajador(Integer v){ this.evaluacionTrabajador = v; }

    public String  getFechaIngreso()       { return fechaIngreso; }
    public void    setFechaIngreso(String v){ this.fechaIngreso = v; }

    public Estado  getEstadoCalidadVida()       { return estadoCalidadVida; }
    public void    setEstadoCalidadVida(Estado v){ this.estadoCalidadVida = v; }

    public String  getSexo()       { return sexo; }
    public void    setSexo(String v){ this.sexo = v; }

    public String  getAntPenal()       { return antPenal; }
    public void    setAntPenal(String v){ this.antPenal = v; }

    public String  getDeudorAlim()       { return deudorAlim; }
    public void    setDeudorAlim(String v){ this.deudorAlim = v; }

    public String  getFolioLicCond()       { return folioLicCond; }
    public void    setFolioLicCond(String v){ this.folioLicCond = v; }

    public String  getEstadoCivil()       { return estadoCivil; }
    public void    setEstadoCivil(String v){ this.estadoCivil = v; }

    public String  getIdiomas()       { return idiomas; }
    public void    setIdiomas(String v){ this.idiomas = v; }

    public String  getLenguaIndigena()       { return lenguaIndigena; }
    public void    setLenguaIndigena(String v){ this.lenguaIndigena = v; }

    // ---- atajos / compatibilidad con UI legacy ----

    /** Backend usa `evaluacionTrabajador`; la UI lo llama `calificacionTrabajador`. */
    public Integer getCalificacionTrabajador() {
        return evaluacionTrabajador;
    }
    public void setCalificacionTrabajador(Integer v) {
        this.evaluacionTrabajador = v;
    }

    /**
     * No existe `descripcionTrabajador` en backend. Se devuelve `descPuesto`
     * cuando está presente; si no, el cache local.
     */
    public String getDescripcionTrabajador() {
        return descPuesto != null ? descPuesto : descripcionTrabajadorLocal;
    }
    public void setDescripcionTrabajador(String v) {
        this.descripcionTrabajadorLocal = v;
        if (descPuesto == null || descPuesto.isEmpty()) {
            this.descPuesto = v;
        }
    }

    /**
     * No existe `ubicacionTrabajador` plano en backend. Se usa el estado
     * geográfico (estadoCalidadVida.nombreEst) cuando esté disponible.
     */
    public String getUbicacionTrabajador() {
        if (estadoCalidadVida != null && estadoCalidadVida.getNombreEst() != null) {
            return estadoCalidadVida.getNombreEst();
        }
        if (domicilio != null && domicilio.getMunAlc() != null) {
            return domicilio.getMunAlc();
        }
        return ubicacionTrabajadorLocal;
    }
    public void setUbicacionTrabajador(String v) {
        this.ubicacionTrabajadorLocal = v;
    }
}
