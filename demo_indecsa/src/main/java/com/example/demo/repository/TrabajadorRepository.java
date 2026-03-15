package com.example.demo.repository;

import com.example.demo.model.Trabajador;
import com.example.demo.model.Trabajador.EstadoTrabajador;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TrabajadorRepository extends JpaRepository<Trabajador, Integer> {

    Optional<Trabajador> findByCorreoTrabajador(String correoTrabajador);

    boolean existsByCorreoTrabajador(String correoTrabajador);

    boolean existsByNssTrabajador(String nssTrabajador);

    List<Trabajador> findByEstadoTrabajador(EstadoTrabajador estadoTrabajador);

    List<Trabajador> findByEspecialidadTrabajadorContainingIgnoreCase(String especialidad);

    List<Trabajador> findByEstadoTrabajadorAndEspecialidadTrabajadorContainingIgnoreCase(
            EstadoTrabajador estadoTrabajador, String especialidad);
}
