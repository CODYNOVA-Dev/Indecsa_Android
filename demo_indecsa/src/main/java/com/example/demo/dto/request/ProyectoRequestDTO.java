package com.example.demo.dto.request;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.time.LocalDate;

@Data
public class ProyectoRequestDTO {

    @NotBlank(message = "El nombre del proyecto es obligatorio")
    @Size(max = 150, message = "El nombre no puede exceder 150 caracteres")
    private String nombreProyecto;

    @Size(max = 80, message = "El tipo de proyecto no puede exceder 80 caracteres")
    private String tipoProyecto;

    @Size(max = 200, message = "El lugar no puede exceder 200 caracteres")
    private String lugarProyecto;

    @Size(max = 100, message = "El municipio no puede exceder 100 caracteres")
    private String municipioProyecto;

    @Size(max = 100, message = "El estado geográfico no puede exceder 100 caracteres")
    private String estadoProyectoGeo;

    private LocalDate fechaEstimadaInicio;

    private LocalDate fechaEstimadaFin;

    @Min(value = 1, message = "La calificación mínima es 1")
    @Max(value = 5, message = "La calificación máxima es 5")
    private Byte calificacionProyecto;

    @Size(max = 500, message = "La descripción no puede exceder 500 caracteres")
    private String descripcionProyecto;
}
