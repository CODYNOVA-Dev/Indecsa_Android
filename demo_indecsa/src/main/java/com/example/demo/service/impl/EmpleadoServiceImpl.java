package com.example.demo.service.impl;

import com.example.demo.dto.request.EmpleadoRequestDTO;
import com.example.demo.dto.response.EmpleadoResponseDTO;
import com.example.demo.dto.response.RolResponseDTO;
import com.example.demo.model.Empleado;
import com.example.demo.model.Rol;
import com.example.demo.repository.EmpleadoRepository;
import com.example.demo.repository.RolRepository;
import com.example.demo.service.EmpleadoService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EmpleadoServiceImpl implements EmpleadoService {

    private final EmpleadoRepository empleadoRepository;
    private final RolRepository rolRepository;
    private final PasswordEncoder passwordEncoder;

    // ─── FIND ALL ─────────────────────────────────────────────────────────────
    @Override
    @Transactional(readOnly = true)
    public List<EmpleadoResponseDTO> findAll() {
        return empleadoRepository.findAll()
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    // ─── FIND BY ID ───────────────────────────────────────────────────────────
    @Override
    @Transactional(readOnly = true)
    public EmpleadoResponseDTO findById(Integer id) {
        return toResponse(getEmpleadoOrThrow(id));
    }

    // ─── FIND BY ROL ──────────────────────────────────────────────────────────
    @Override
    @Transactional(readOnly = true)
    public List<EmpleadoResponseDTO> findByRol(Integer idRol) {
        if (!rolRepository.existsById(idRol)) {
            throw new EntityNotFoundException("Rol no encontrado con id: " + idRol);
        }
        return empleadoRepository.findByRol_IdRol(idRol)
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    // ─── CREATE ───────────────────────────────────────────────────────────────
    @Override
    @Transactional
    public EmpleadoResponseDTO create(EmpleadoRequestDTO dto) {
        if (empleadoRepository.existsByCorreoEmpleado(dto.getCorreoEmpleado())) {
            throw new IllegalArgumentException(
                    "Ya existe un empleado con el correo: " + dto.getCorreoEmpleado());
        }
        Rol rol = rolRepository.findById(dto.getIdRol())
                .orElseThrow(() -> new EntityNotFoundException(
                        "Rol no encontrado con id: " + dto.getIdRol()));

        Empleado empleado = new Empleado();
        empleado.setNombreEmpleado(dto.getNombreEmpleado());
        empleado.setCorreoEmpleado(dto.getCorreoEmpleado().toLowerCase().trim());
        empleado.setContrasena(passwordEncoder.encode(dto.getContrasena()));
        empleado.setRol(rol);
        return toResponse(empleadoRepository.save(empleado));
    }

    // ─── UPDATE ───────────────────────────────────────────────────────────────
    @Override
    @Transactional
    public EmpleadoResponseDTO update(Integer id, EmpleadoRequestDTO dto) {
        Empleado empleado = getEmpleadoOrThrow(id);

        if (!empleado.getCorreoEmpleado().equals(dto.getCorreoEmpleado())
                && empleadoRepository.existsByCorreoEmpleado(dto.getCorreoEmpleado())) {
            throw new IllegalArgumentException(
                    "Ya existe un empleado con el correo: " + dto.getCorreoEmpleado());
        }
        Rol rol = rolRepository.findById(dto.getIdRol())
                .orElseThrow(() -> new EntityNotFoundException(
                        "Rol no encontrado con id: " + dto.getIdRol()));

        empleado.setNombreEmpleado(dto.getNombreEmpleado());
        empleado.setCorreoEmpleado(dto.getCorreoEmpleado().toLowerCase().trim());
        empleado.setContrasena(passwordEncoder.encode(dto.getContrasena()));
        empleado.setRol(rol);
        return toResponse(empleadoRepository.save(empleado));
    }

    // ─── DELETE ───────────────────────────────────────────────────────────────
    @Override
    @Transactional
    public void delete(Integer id) {
        if (!empleadoRepository.existsById(id)) {
            throw new EntityNotFoundException("Empleado no encontrado con id: " + id);
        }
        empleadoRepository.deleteById(id);
    }

    // ─── HELPERS ──────────────────────────────────────────────────────────────
    private Empleado getEmpleadoOrThrow(Integer id) {
        return empleadoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Empleado no encontrado con id: " + id));
    }

    // ─── MAPPER ───────────────────────────────────────────────────────────────
    private EmpleadoResponseDTO toResponse(Empleado empleado) {
        RolResponseDTO rolDTO = RolResponseDTO.builder()
                .idRol(empleado.getRol().getIdRol())
                .nombreRol(empleado.getRol().getNombreRol())
                .descripcionRol(empleado.getRol().getDescripcionRol())
                .build();

        return EmpleadoResponseDTO.builder()
                .idEmpleado(empleado.getIdEmpleado())
                .nombreEmpleado(empleado.getNombreEmpleado())
                .correoEmpleado(empleado.getCorreoEmpleado())
                .rol(rolDTO)
                .build();
    }
}
