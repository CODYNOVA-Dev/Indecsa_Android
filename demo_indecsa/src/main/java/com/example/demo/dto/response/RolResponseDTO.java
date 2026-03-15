package com.example.demo.dto.response;
import com.example.demo.model.Rol.NombreRol;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RolResponseDTO {

    private Integer idRol;
    private NombreRol nombreRol;
    private String descripcionRol;
}
