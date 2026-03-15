package com.example.demo.service;

import com.example.demo.dto.request.AsignacionTrabajadorProyectoRequestDTO;
import com.example.demo.dto.response.AsignacionTrabajadorProyectoResponseDTO;
import com.example.demo.model.AsignacionTrabajadorProyecto.EstatusAsignacion;

import java.util.List;

public interface AsignacionTrabajadorProyectoService {

    List<AsignacionTrabajadorProyectoResponseDTO> findAll();

    AsignacionTrabajadorProyectoResponseDTO findById(Integer id);

    List<AsignacionTrabajadorProyectoResponseDTO> findByProyecto(Integer idProyecto);

    List<AsignacionTrabajadorProyectoResponseDTO> findByTrabajador(Integer idTrabajador);

    List<AsignacionTrabajadorProyectoResponseDTO> findByAsignacionPc(Integer idAsignacionPc);

    List<AsignacionTrabajadorProyectoResponseDTO> findByEstatus(EstatusAsignacion estatus);

    AsignacionTrabajadorProyectoResponseDTO create(AsignacionTrabajadorProyectoRequestDTO dto);

    AsignacionTrabajadorProyectoResponseDTO update(Integer id, AsignacionTrabajadorProyectoRequestDTO dto);

    AsignacionTrabajadorProyectoResponseDTO cambiarEstatus(Integer id, EstatusAsignacion estatus);

    void delete(Integer id);
}
