package com.example.demo.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Table(name = "Proyecto")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Proyecto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_proyecto")
    private Integer idProyecto;

    @Column(name = "nombre_proyecto", nullable = false, length = 150)
    private String nombreProyecto;

    @Column(name = "tipo_proyecto", length = 80)
    private String tipoProyecto;

    @Column(name = "lugar_proyecto", length = 200)
    private String lugarProyecto;

    @Column(name = "municipio_proyecto", length = 100)
    private String municipioProyecto;

    @Column(name = "estado_proyecto_geo", length = 100)
    private String estadoProyectoGeo;

    @Column(name = "fecha_estimada_inicio")
    private LocalDate fechaEstimadaInicio;

    @Column(name = "fecha_estimada_fin")
    private LocalDate fechaEstimadaFin;

    @Column(name = "calificacion_proyecto")
    private Byte calificacionProyecto;

    @Enumerated(EnumType.STRING)
    @Column(name = "estatus_proyecto", nullable = false, columnDefinition = "ENUM('PLANEACION','EN_CURSO','PAUSADO','FINALIZADO','CANCELADO') DEFAULT 'PLANEACION'")
    private EstatusProyecto estatusProyecto = EstatusProyecto.PLANEACION;

    @Column(name = "descripcion_proyecto", length = 500, columnDefinition = "VARCHAR(500) DEFAULT 'Sin descripcion'")
    private String descripcionProyecto;

    public enum EstatusProyecto {
        PLANEACION,
        EN_CURSO,
        PAUSADO,
        FINALIZADO,
        CANCELADO
    }
}
