package com.example.demo.dto.response;

import com.example.demo.dto.response.RolResponseDTO;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class EmpleadoResponseDTO {

    private Integer idEmpleado;
    private String nombreEmpleado;
    private String correoEmpleado;
    // La contraseña nunca se expone en el response
    private RolResponseDTO rol;
}
