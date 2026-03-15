package com.example.demo.repository;

import com.example.demo.model.AsignacionProyectoContratista;
import com.example.demo.model.AsignacionProyectoContratista.EstatusContrato;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AsignacionProyectoContratistaRepository
        extends JpaRepository<AsignacionProyectoContratista, Integer> {

    List<AsignacionProyectoContratista> findByProyecto_IdProyecto(Integer idProyecto);

    List<AsignacionProyectoContratista> findByContratista_IdContratista(Integer idContratista);

    List<AsignacionProyectoContratista> findByEstatusContrato(EstatusContrato estatusContrato);

    List<AsignacionProyectoContratista> findByProyecto_IdProyectoAndEstatusContrato(
            Integer idProyecto, EstatusContrato estatusContrato);

    Optional<AsignacionProyectoContratista> findByProyecto_IdProyectoAndContratista_IdContratista(
            Integer idProyecto, Integer idContratista);

    boolean existsByProyecto_IdProyectoAndContratista_IdContratista(
            Integer idProyecto, Integer idContratista);
}
