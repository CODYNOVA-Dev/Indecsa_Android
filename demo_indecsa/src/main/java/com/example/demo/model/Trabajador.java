package com.example.demo.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Table(name = "Trabajador")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Trabajador {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_trabajador")
    private Integer idTrabajador;

    @Column(name = "nombre_trabajador", nullable = false, length = 100)
    private String nombreTrabajador;

    @Column(name = "nss_trabajador", length = 15)
    private String nssTrabajador;

    @Column(name = "experiencia", length = 200)
    private String experiencia;

    @Column(name = "telefono_trabajador", nullable = false, length = 15)
    private String telefonoTrabajador;

    @Column(name = "correo_trabajador", nullable = false, unique = true, length = 100)
    private String correoTrabajador;

    @Column(name = "contrasenia_trabajador", nullable = false, length = 255)
    private String contraseniaTrabajador;

    @Column(name = "especialidad_trabajador", nullable = false, length = 100)
    private String especialidadTrabajador;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado_trabajador", nullable = false, columnDefinition = "ENUM('ACTIVO','INACTIVO','VACACIONES','BAJA') DEFAULT 'ACTIVO'")
    private EstadoTrabajador estadoTrabajador = EstadoTrabajador.ACTIVO;

    @Column(name = "descripcion_trabajador", length = 800, columnDefinition = "VARCHAR(800) DEFAULT 'Sin descripcion'")
    private String descripcionTrabajador;

    @Column(name = "calificacion_trabajador")
    private Byte calificacionTrabajador;

    @Column(name = "fecha_ingreso", nullable = false)
    private LocalDate fechaIngreso;

    public enum EstadoTrabajador {
        ACTIVO,
        INACTIVO,
        VACACIONES,
        BAJA
    }
}
