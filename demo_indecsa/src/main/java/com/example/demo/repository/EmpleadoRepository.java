package com.example.demo.repository;

import com.example.demo.model.Empleado;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EmpleadoRepository extends JpaRepository<Empleado, Integer> {

    Optional<Empleado> findByCorreoEmpleado(String correoEmpleado);

    boolean existsByCorreoEmpleado(String correoEmpleado);

    List<Empleado> findByRol_IdRol(Integer idRol);

    List<Empleado> findByRol_NombreRol(com.indecsa.model.Rol.NombreRol nombreRol);
}
