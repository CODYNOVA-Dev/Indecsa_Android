package com.example.demo.repository;

import com.example.demo.model.Proyecto;
import com.example.demo.model.Proyecto.EstatusProyecto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface ProyectoRepository extends JpaRepository<Proyecto, Integer> {

    List<Proyecto> findByEstatusProyecto(EstatusProyecto estatusProyecto);

    List<Proyecto> findByMunicipioProyectoIgnoreCase(String municipioProyecto);

    List<Proyecto> findByEstadoProyectoGeoIgnoreCase(String estadoProyectoGeo);

    List<Proyecto> findByTipoProyectoIgnoreCase(String tipoProyecto);

    List<Proyecto> findByFechaEstimadaInicioAfter(LocalDate fecha);

    List<Proyecto> findByFechaEstimadaFinBefore(LocalDate fecha);

    boolean existsByNombreProyecto(String nombreProyecto);
}
