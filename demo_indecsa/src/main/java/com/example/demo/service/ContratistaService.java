package com.example.demo.service;

import com.example.demo.dto.request.ContratistaRequestDTO;
import com.example.demo.dto.response.ContratistaResponseDTO;
import com.example.demo.model.Contratista.EstadoContratista;

import java.util.List;

public interface ContratistaService {

    List<ContratistaResponseDTO> findAll();

    ContratistaResponseDTO findById(Integer id);

    List<ContratistaResponseDTO> findByEstado(EstadoContratista estado);

    ContratistaResponseDTO create(ContratistaRequestDTO dto);

    ContratistaResponseDTO update(Integer id, ContratistaRequestDTO dto);

    ContratistaResponseDTO cambiarEstado(Integer id, EstadoContratista estado);

    void delete(Integer id);
}
