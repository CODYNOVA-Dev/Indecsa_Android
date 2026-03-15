package com.example.demo.dto.response;

import com.indecsa.model.Trabajador.EstadoTrabajador;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Data
@Builder
public class TrabajadorResponseDTO {

    private Integer idTrabajador;
    private String nombreTrabajador;
    private String nssTrabajador;
    private String experiencia;
    private String telefonoTrabajador;
    private String correoTrabajador;
    // La contraseña nunca se expone en el response
    private String especialidadTrabajador;
    private EstadoTrabajador estadoTrabajador;
    private String descripcionTrabajador;
    private Byte calificacionTrabajador;
    private LocalDate fechaIngreso;
}
