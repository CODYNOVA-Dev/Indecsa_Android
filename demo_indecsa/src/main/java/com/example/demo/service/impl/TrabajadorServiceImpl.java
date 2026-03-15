package com.example.demo.service.impl;

import com.example.demo.dto.request.TrabajadorRequestDTO;
import com.example.demo.dto.response.TrabajadorResponseDTO;
import com.example.demo.model.Trabajador;
import com.example.demo.model.Trabajador.EstadoTrabajador;
import com.example.demo.repository.TrabajadorRepository;
import com.example.demo.service.TrabajadorService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TrabajadorServiceImpl implements TrabajadorService {

    private final TrabajadorRepository trabajadorRepository;
    private final PasswordEncoder passwordEncoder;

    // ─── FIND ALL ─────────────────────────────────────────────────────────────
    @Override
    @Transactional(readOnly = true)
    public List<TrabajadorResponseDTO> findAll() {
        return trabajadorRepository.findAll()
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    // ─── FIND BY ID ───────────────────────────────────────────────────────────
    @Override
    @Transactional(readOnly = true)
    public TrabajadorResponseDTO findById(Integer id) {
        return toResponse(getTrabajadorOrThrow(id));
    }

    // ─── FIND BY ESTADO ───────────────────────────────────────────────────────
    @Transactional(readOnly = true)
    public List<TrabajadorResponseDTO> findByEstado(EstadoTrabajador estado) {
        return trabajadorRepository.findByEstadoTrabajador(estado)
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    // ─── FIND BY ESPECIALIDAD ─────────────────────────────────────────────────
    @Override
    @Transactional(readOnly = true)
    public List<TrabajadorResponseDTO> findByEspecialidad(String especialidad) {
        return trabajadorRepository.findByEspecialidadTrabajadorContainingIgnoreCase(especialidad)
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    // ─── CREATE ───────────────────────────────────────────────────────────────
    @Override
    @Transactional
    public TrabajadorResponseDTO create(TrabajadorRequestDTO dto) {
        if (trabajadorRepository.existsByCorreoTrabajador(dto.getCorreoTrabajador())) {
            throw new IllegalArgumentException(
                    "Ya existe un trabajador con el correo: " + dto.getCorreoTrabajador());
        }
        if (dto.getNssTrabajador() != null
                && trabajadorRepository.existsByNssTrabajador(dto.getNssTrabajador())) {
            throw new IllegalArgumentException(
                    "Ya existe un trabajador con el NSS: " + dto.getNssTrabajador());
        }
        Trabajador trabajador = new Trabajador();
        mapDtoToEntity(dto, trabajador);
        trabajador.setContraseniaTrabajador(passwordEncoder.encode(dto.getContraseniaTrabajador()));
        trabajador.setEstadoTrabajador(EstadoTrabajador.ACTIVO);
        return toResponse(trabajadorRepository.save(trabajador));
    }

    // ─── UPDATE ───────────────────────────────────────────────────────────────
    @Override
    @Transactional
    public TrabajadorResponseDTO update(Integer id, TrabajadorRequestDTO dto) {
        Trabajador trabajador = getTrabajadorOrThrow(id);

        if (!trabajador.getCorreoTrabajador().equals(dto.getCorreoTrabajador())
                && trabajadorRepository.existsByCorreoTrabajador(dto.getCorreoTrabajador())) {
            throw new IllegalArgumentException(
                    "Ya existe un trabajador con el correo: " + dto.getCorreoTrabajador());
        }
        if (dto.getNssTrabajador() != null
                && !dto.getNssTrabajador().equals(trabajador.getNssTrabajador())
                && trabajadorRepository.existsByNssTrabajador(dto.getNssTrabajador())) {
            throw new IllegalArgumentException(
                    "Ya existe un trabajador con el NSS: " + dto.getNssTrabajador());
        }
        mapDtoToEntity(dto, trabajador);
        trabajador.setContraseniaTrabajador(passwordEncoder.encode(dto.getContraseniaTrabajador()));
        return toResponse(trabajadorRepository.save(trabajador));
    }

    // ─── CAMBIAR ESTADO ───────────────────────────────────────────────────────
    @Override
    @Transactional
    public TrabajadorResponseDTO cambiarEstado(Integer id, EstadoTrabajador estado) {
        Trabajador trabajador = getTrabajadorOrThrow(id);
        trabajador.setEstadoTrabajador(estado);
        return toResponse(trabajadorRepository.save(trabajador));
    }

    // ─── DELETE ───────────────────────────────────────────────────────────────
    @Override
    @Transactional
    public void delete(Integer id) {
        if (!trabajadorRepository.existsById(id)) {
            throw new EntityNotFoundException("Trabajador no encontrado con id: " + id);
        }
        trabajadorRepository.deleteById(id);
    }

    // ─── HELPERS ──────────────────────────────────────────────────────────────
    private Trabajador getTrabajadorOrThrow(Integer id) {
        return trabajadorRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Trabajador no encontrado con id: " + id));
    }

    private void mapDtoToEntity(TrabajadorRequestDTO dto, Trabajador trabajador) {
        trabajador.setNombreTrabajador(dto.getNombreTrabajador());
        trabajador.setNssTrabajador(dto.getNssTrabajador());
        trabajador.setExperiencia(dto.getExperiencia());
        trabajador.setTelefonoTrabajador(dto.getTelefonoTrabajador());
        trabajador.setCorreoTrabajador(dto.getCorreoTrabajador().toLowerCase().trim());
        trabajador.setEspecialidadTrabajador(dto.getEspecialidadTrabajador());
        trabajador.setDescripcionTrabajador(dto.getDescripcionTrabajador());
        trabajador.setCalificacionTrabajador(dto.getCalificacionTrabajador());
        trabajador.setFechaIngreso(dto.getFechaIngreso());
    }

    // ─── MAPPER ───────────────────────────────────────────────────────────────
    private TrabajadorResponseDTO toResponse(Trabajador t) {
        return TrabajadorResponseDTO.builder()
                .idTrabajador(t.getIdTrabajador())
                .nombreTrabajador(t.getNombreTrabajador())
                .nssTrabajador(t.getNssTrabajador())
                .experiencia(t.getExperiencia())
                .telefonoTrabajador(t.getTelefonoTrabajador())
                .correoTrabajador(t.getCorreoTrabajador())
                .especialidadTrabajador(t.getEspecialidadTrabajador())
                .estadoTrabajador(t.getEstadoTrabajador())
                .descripcionTrabajador(t.getDescripcionTrabajador())
                .calificacionTrabajador(t.getCalificacionTrabajador())
                .fechaIngreso(t.getFechaIngreso())
                .build();
    }
}
