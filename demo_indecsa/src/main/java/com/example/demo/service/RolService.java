package com.example.demo.service;

import com.example.demo.dto.request.RolRequestDTO;
import com.example.demo.dto.response.RolResponseDTO;

import java.util.List;

public interface RolService {

    List<RolResponseDTO> findAll();

    RolResponseDTO findById(Integer id);

    RolResponseDTO create(RolRequestDTO dto);

    RolResponseDTO update(Integer id, RolRequestDTO dto);

    void delete(Integer id);
}
