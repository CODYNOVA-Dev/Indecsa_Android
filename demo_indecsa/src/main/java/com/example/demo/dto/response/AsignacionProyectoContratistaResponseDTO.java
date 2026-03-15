package com.example.demo.dto.response;

import com.example.demo.dto.response.ContratistaResponseDTO;
import com.example.demo.dto.response.ProyectoResponseDTO;
import com.example.demo.model.AsignacionProyectoContratista.EstatusContrato;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
public class AsignacionProyectoContratistaResponseDTO {

    private Integer idAsignacionPc;
    private ProyectoResponseDTO proyecto;
    private ContratistaResponseDTO contratista;
    private String numeroContrato;
    private LocalDate fechaInicio;
    private LocalDate fechaFinEstimada;
    private BigDecimal montoContratado;
    private EstatusContrato estatusContrato;
    private String observaciones;
}
