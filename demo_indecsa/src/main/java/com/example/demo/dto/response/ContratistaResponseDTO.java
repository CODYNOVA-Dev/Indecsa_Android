package com.example.demo.dto.response;

import com.example.demo.model.Contratista.EstadoContratista;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ContratistaResponseDTO {

    private Integer idContratista;
    private String rfcContratista;
    private String telefonoContratista;
    private String correoContratista;
    // La contraseña nunca se expone en el response
    private String descripcionContratista;
    private String experiencia;
    private Byte calificacionContratista;
    private EstadoContratista estadoContratista;
}
