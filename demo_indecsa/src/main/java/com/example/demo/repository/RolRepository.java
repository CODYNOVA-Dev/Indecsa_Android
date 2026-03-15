package com.example.demo.repository;

import com.example.demo.model.Rol;
import com.example.demo.model.Rol.NombreRol;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RolRepository extends JpaRepository<Rol, Integer> {

    Optional<Rol> findByNombreRol(NombreRol nombreRol);

    boolean existsByNombreRol(NombreRol nombreRol);
}
