package com.example.demo.repository;

import com.example.demo.model.Contratista;
import com.example.demo.model.Contratista.EstadoContratista;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ContratistaRepository extends JpaRepository<Contratista, Integer> {

    Optional<Contratista> findByCorreoContratista(String correoContratista);

    Optional<Contratista> findByRfcContratista(String rfcContratista);

    boolean existsByCorreoContratista(String correoContratista);

    boolean existsByRfcContratista(String rfcContratista);

    List<Contratista> findByEstadoContratista(EstadoContratista estadoContratista);
}
