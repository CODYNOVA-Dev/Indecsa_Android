package com.example.indecsa_v2.models;

import com.google.gson.annotations.SerializedName;

public class Domicilio {

    @SerializedName("idDomicilio") private Integer idDomicilio;
    @SerializedName("calle")       private String  calle;
    @SerializedName("numExt")      private String  numExt;
    @SerializedName("numInt")      private String  numInt;
    @SerializedName("colonia")     private String  colonia;
    @SerializedName("codPost")     private Integer codPost;
    @SerializedName("munAlc")      private String  munAlc;
    @SerializedName("estado")      private Estado  estado;

    public Domicilio() {}

    public Domicilio(Integer idDomicilio) {
        this.idDomicilio = idDomicilio;
    }

    public Integer getIdDomicilio()         { return idDomicilio; }
    public void    setIdDomicilio(Integer v){ this.idDomicilio = v; }

    public String  getCalle()               { return calle; }
    public void    setCalle(String v)       { this.calle = v; }

    public String  getNumExt()              { return numExt; }
    public void    setNumExt(String v)      { this.numExt = v; }

    public String  getNumInt()              { return numInt; }
    public void    setNumInt(String v)      { this.numInt = v; }

    public String  getColonia()             { return colonia; }
    public void    setColonia(String v)     { this.colonia = v; }

    public Integer getCodPost()             { return codPost; }
    public void    setCodPost(Integer v)    { this.codPost = v; }

    public String  getMunAlc()              { return munAlc; }
    public void    setMunAlc(String v)      { this.munAlc = v; }

    public Estado  getEstado()              { return estado; }
    public void    setEstado(Estado v)      { this.estado = v; }

    /** Construye una representación legible del domicilio para mostrar en UI. */
    public String resumen() {
        StringBuilder sb = new StringBuilder();
        if (calle   != null && !calle.isEmpty())   sb.append(calle).append(' ');
        if (numExt  != null && !numExt.isEmpty())  sb.append(numExt).append(' ');
        if (colonia != null && !colonia.isEmpty()) sb.append(colonia).append(", ");
        if (munAlc  != null && !munAlc.isEmpty())  sb.append(munAlc);
        if (estado  != null && estado.getNombreEst() != null) {
            if (sb.length() > 0) sb.append(", ");
            sb.append(estado.getNombreEst());
        }
        return sb.toString().trim();
    }
}
