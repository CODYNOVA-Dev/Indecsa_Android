package com.example.demo.repository;

import com.example.demo.model.AsignacionTrabajadorProyecto;
import com.example.demo.model.AsignacionTrabajadorProyecto.EstatusAsignacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AsignacionTrabajadorProyectoRepository
        extends JpaRepository<AsignacionTrabajadorProyecto, Integer> {

    List<AsignacionTrabajadorProyecto> findByProyecto_IdProyecto(Integer idProyecto);

    List<AsignacionTrabajadorProyecto> findByTrabajador_IdTrabajador(Integer idTrabajador);

    List<AsignacionTrabajadorProyecto> findByAsignacionProyectoContratista_IdAsignacionPc(
            Integer idAsignacionPc);

    List<AsignacionTrabajadorProyecto> findByEstatusAsignacion(EstatusAsignacion estatusAsignacion);

    List<AsignacionTrabajadorProyecto> findByProyecto_IdProyectoAndEstatusAsignacion(
            Integer idProyecto, EstatusAsignacion estatusAsignacion);

    List<AsignacionTrabajadorProyecto> findByTrabajador_IdTrabajadorAndEstatusAsignacion(
            Integer idTrabajador, EstatusAsignacion estatusAsignacion);

    Optional<AsignacionTrabajadorProyecto> findByTrabajador_IdTrabajadorAndProyecto_IdProyecto(
            Integer idTrabajador, Integer idProyecto);

    boolean existsByTrabajador_IdTrabajadorAndProyecto_IdProyecto(
            Integer idTrabajador, Integer idProyecto);
}
