package com.example.demo.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Table(
    name = "Asignacion_Trabajador_Proyecto",
    uniqueConstraints = {
        @UniqueConstraint(name = "uq_trabajador_proyecto", columnNames = {"id_trabajador", "id_proyecto"})
    }
)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AsignacionTrabajadorProyecto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_asignacion_tp")
    private Integer idAsignacionTp;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_trabajador", nullable = false)
    private Trabajador trabajador;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_proyecto", nullable = false)
    private Proyecto proyecto;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_asignacion_pc", nullable = false)
    private AsignacionProyectoContratista asignacionProyectoContratista;

    @Column(name = "rol_en_proyecto", length = 100)
    private String rolEnProyecto;

    @Column(name = "fecha_inicio")
    private LocalDate fechaInicio;

    @Column(name = "fecha_fin_estimada")
    private LocalDate fechaFinEstimada;

    @Enumerated(EnumType.STRING)
    @Column(name = "estatus_asignacion", nullable = false, columnDefinition = "ENUM('ACTIVO','SUSPENDIDO','FINALIZADO') DEFAULT 'ACTIVO'")
    private EstatusAsignacion estatusAsignacion = EstatusAsignacion.ACTIVO;

    @Column(name = "observaciones", length = 500)
    private String observaciones;

    public enum EstatusAsignacion {
        ACTIVO,
        SUSPENDIDO,
        FINALIZADO
    }
}
