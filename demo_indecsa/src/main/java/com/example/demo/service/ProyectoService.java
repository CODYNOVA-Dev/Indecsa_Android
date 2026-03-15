package com.example.demo.service;

import com.example.demo.dto.request.ProyectoRequestDTO;
import com.example.demo.dto.response.ProyectoResponseDTO;
import com.example.demo.model.Proyecto.EstatusProyecto;

import java.util.List;

public interface ProyectoService {

    List<ProyectoResponseDTO> findAll();

    ProyectoResponseDTO findById(Integer id);

    List<ProyectoResponseDTO> findByEstatus(EstatusProyecto estatus);

    List<ProyectoResponseDTO> findByMunicipio(String municipio);

    ProyectoResponseDTO create(ProyectoRequestDTO dto);

    ProyectoResponseDTO update(Integer id, ProyectoRequestDTO dto);

    ProyectoResponseDTO cambiarEstatus(Integer id, EstatusProyecto estatus);

    void delete(Integer id);
}
