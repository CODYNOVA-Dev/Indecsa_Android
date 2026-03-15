package com.example.demo.service;

import com.example.demo.dto.request.AsignacionProyectoContratistaRequestDTO;
import com.example.demo.dto.response.AsignacionProyectoContratistaResponseDTO;
import com.example.demo.model.AsignacionProyectoContratista.EstatusContrato;

import java.util.List;

public interface AsignacionProyectoContratistaService {

    List<AsignacionProyectoContratistaResponseDTO> findAll();

    AsignacionProyectoContratistaResponseDTO findById(Integer id);

    List<AsignacionProyectoContratistaResponseDTO> findByProyecto(Integer idProyecto);

    List<AsignacionProyectoContratistaResponseDTO> findByContratista(Integer idContratista);

    List<AsignacionProyectoContratistaResponseDTO> findByEstatus(EstatusContrato estatus);

    AsignacionProyectoContratistaResponseDTO create(AsignacionProyectoContratistaRequestDTO dto);

    AsignacionProyectoContratistaResponseDTO update(Integer id, AsignacionProyectoContratistaRequestDTO dto);

    AsignacionProyectoContratistaResponseDTO cambiarEstatus(Integer id, EstatusContrato estatus);

    void delete(Integer id);
}
