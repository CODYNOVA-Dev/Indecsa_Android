package com.example.demo.dto.request;

import com.indecsa.model.Rol.NombreRol;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class RolRequestDTO {

    @NotNull(message = "El nombre del rol es obligatorio")
    private NombreRol nombreRol;

    private String descripcionRol;
}
