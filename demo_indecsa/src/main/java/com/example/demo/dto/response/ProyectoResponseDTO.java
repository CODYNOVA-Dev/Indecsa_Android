package com.example.demo.dto.response;

import com.indecsa.model.Proyecto.EstatusProyecto;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Data
@Builder
public class ProyectoResponseDTO {

    private Integer idProyecto;
    private String nombreProyecto;
    private String tipoProyecto;
    private String lugarProyecto;
    private String municipioProyecto;
    private String estadoProyectoGeo;
    private LocalDate fechaEstimadaInicio;
    private LocalDate fechaEstimadaFin;
    private Byte calificacionProyecto;
    private EstatusProyecto estatusProyecto;
    private String descripcionProyecto;
}
