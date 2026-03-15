package com.example.demo.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(
    name = "Asignacion_Proyecto_Contratista",
    uniqueConstraints = {
        @UniqueConstraint(name = "uq_proyecto_contratista", columnNames = {"id_proyecto", "id_contratista"})
    }
)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AsignacionProyectoContratista {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_asignacion_pc")
    private Integer idAsignacionPc;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_proyecto", nullable = false)
    private Proyecto proyecto;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_contratista", nullable = false)
    private Contratista contratista;

    @Column(name = "numero_contrato", length = 50)
    private String numeroContrato;

    @Column(name = "fecha_inicio")
    private LocalDate fechaInicio;

    @Column(name = "fecha_fin_estimada")
    private LocalDate fechaFinEstimada;

    @Column(name = "monto_contratado", precision = 15, scale = 2)
    private BigDecimal montoContratado;

    @Enumerated(EnumType.STRING)
    @Column(name = "estatus_contrato", nullable = false, columnDefinition = "ENUM('ACTIVO','SUSPENDIDO','FINALIZADO','RESCINDIDO') DEFAULT 'ACTIVO'")
    private EstatusContrato estatusContrato = EstatusContrato.ACTIVO;

    @Column(name = "observaciones", length = 500)
    private String observaciones;

    public enum EstatusContrato {
        ACTIVO,
        SUSPENDIDO,
        FINALIZADO,
        RESCINDIDO
    }
}
