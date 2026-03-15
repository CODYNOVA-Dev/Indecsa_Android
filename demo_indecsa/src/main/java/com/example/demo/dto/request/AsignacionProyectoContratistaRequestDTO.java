package com.example.demo.dto.request;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class AsignacionProyectoContratistaRequestDTO {

    @NotNull(message = "El id del proyecto es obligatorio")
    private Integer idProyecto;

    @NotNull(message = "El id del contratista es obligatorio")
    private Integer idContratista;

    @Size(max = 50, message = "El número de contrato no puede exceder 50 caracteres")
    private String numeroContrato;

    private LocalDate fechaInicio;

    private LocalDate fechaFinEstimada;

    @DecimalMin(value = "0.0", inclusive = false, message = "El monto contratado debe ser mayor a 0")
    @Digits(integer = 13, fraction = 2, message = "El monto no puede exceder 13 enteros y 2 decimales")
    private BigDecimal montoContratado;

    @Size(max = 500, message = "Las observaciones no pueden exceder 500 caracteres")
    private String observaciones;
}
