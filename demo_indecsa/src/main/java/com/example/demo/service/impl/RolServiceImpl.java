package com.example.demo.service.impl;

import com.example.demo.dto.request.RolRequestDTO;
import com.example.demo.dto.response.RolResponseDTO;
import com.example.demo.model.Rol;
import com.example.demo.repository.RolRepository;
import com.example.demo.service.RolService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RolServiceImpl implements RolService {

    private final RolRepository rolRepository;

    // ─── FIND ALL ─────────────────────────────────────────────────────────────
    @Override
    @Transactional(readOnly = true)
    public List<RolResponseDTO> findAll() {
        return rolRepository.findAll()
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    // ─── FIND BY ID ───────────────────────────────────────────────────────────
    @Override
    @Transactional(readOnly = true)
    public RolResponseDTO findById(Integer id) {
        Rol rol = rolRepository.findById(id)
                .orElseThrow(() -> new jakarta.persistence.EntityNotFoundException(
                        "Rol no encontrado con id: " + id));
        return toResponse(rol);
    }

    // ─── CREATE ───────────────────────────────────────────────────────────────
    @Override
    @Transactional
    public RolResponseDTO create(RolRequestDTO dto) {
        if (rolRepository.existsByNombreRol(dto.getNombreRol())) {
            throw new IllegalArgumentException(
                    "Ya existe un rol con el nombre: " + dto.getNombreRol());
        }
        Rol rol = new Rol();
        rol.setNombreRol(dto.getNombreRol());
        rol.setDescripcionRol(dto.getDescripcionRol());
        return toResponse(rolRepository.save(rol));
    }

    // ─── UPDATE ───────────────────────────────────────────────────────────────
    @Override
    @Transactional
    public RolResponseDTO update(Integer id, RolRequestDTO dto) {
        Rol rol = rolRepository.findById(id)
                .orElseThrow(() -> new jakarta.persistence.EntityNotFoundException(
                        "Rol no encontrado con id: " + id));

        if (!rol.getNombreRol().equals(dto.getNombreRol())
                && rolRepository.existsByNombreRol(dto.getNombreRol())) {
            throw new IllegalArgumentException(
                    "Ya existe un rol con el nombre: " + dto.getNombreRol());
        }
        rol.setNombreRol(dto.getNombreRol());
        rol.setDescripcionRol(dto.getDescripcionRol());
        return toResponse(rolRepository.save(rol));
    }

    // ─── DELETE ───────────────────────────────────────────────────────────────
    @Override
    @Transactional
    public void delete(Integer id) {
        if (!rolRepository.existsById(id)) {
            throw new jakarta.persistence.EntityNotFoundException(
                    "Rol no encontrado con id: " + id);
        }
        rolRepository.deleteById(id);
    }

    // ─── MAPPER ───────────────────────────────────────────────────────────────
    private RolResponseDTO toResponse(Rol rol) {
        return RolResponseDTO.builder()
                .idRol(rol.getIdRol())
                .nombreRol(rol.getNombreRol())
                .descripcionRol(rol.getDescripcionRol())
                .build();
    }
}
