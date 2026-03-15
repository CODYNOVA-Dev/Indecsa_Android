package com.example.demo.service.impl;

import com.example.demo.dto.request.ProyectoRequestDTO;
import com.example.demo.dto.response.ProyectoResponseDTO;
import com.example.demo.model.Proyecto;
import com.example.demo.model.Proyecto.EstatusProyecto;
import com.example.demo.repository.ProyectoRepository;
import com.example.demo.service.ProyectoService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProyectoServiceImpl implements ProyectoService {

    private final ProyectoRepository proyectoRepository;

    // ─── FIND ALL ─────────────────────────────────────────────────────────────
    @Override
    @Transactional(readOnly = true)
    public List<ProyectoResponseDTO> findAll() {
        return proyectoRepository.findAll()
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    // ─── FIND BY ID ───────────────────────────────────────────────────────────
    @Override
    @Transactional(readOnly = true)
    public ProyectoResponseDTO findById(Integer id) {
        return toResponse(getProyectoOrThrow(id));
    }

    // ─── FIND BY ESTATUS ──────────────────────────────────────────────────────
    @Override
    @Transactional(readOnly = true)
    public List<ProyectoResponseDTO> findByEstatus(EstatusProyecto estatus) {
        return proyectoRepository.findByEstatusProyecto(estatus)
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    // ─── FIND BY MUNICIPIO ────────────────────────────────────────────────────
    @Override
    @Transactional(readOnly = true)
    public List<ProyectoResponseDTO> findByMunicipio(String municipio) {
        return proyectoRepository.findByMunicipioProyectoIgnoreCase(municipio)
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    // ─── CREATE ───────────────────────────────────────────────────────────────
    @Override
    @Transactional
    public ProyectoResponseDTO create(ProyectoRequestDTO dto) {
        validarFechas(dto);
        Proyecto proyecto = new Proyecto();
        mapDtoToEntity(dto, proyecto);
        proyecto.setEstatusProyecto(EstatusProyecto.PLANEACION);
        return toResponse(proyectoRepository.save(proyecto));
    }

    // ─── UPDATE ───────────────────────────────────────────────────────────────
    @Override
    @Transactional
    public ProyectoResponseDTO update(Integer id, ProyectoRequestDTO dto) {
        Proyecto proyecto = getProyectoOrThrow(id);
        validarFechas(dto);
        mapDtoToEntity(dto, proyecto);
        return toResponse(proyectoRepository.save(proyecto));
    }

    // ─── CAMBIAR ESTATUS ──────────────────────────────────────────────────────
    @Override
    @Transactional
    public ProyectoResponseDTO cambiarEstatus(Integer id, EstatusProyecto estatus) {
        Proyecto proyecto = getProyectoOrThrow(id);
        proyecto.setEstatusProyecto(estatus);
        return toResponse(proyectoRepository.save(proyecto));
    }

    // ─── DELETE ───────────────────────────────────────────────────────────────
    @Override
    @Transactional
    public void delete(Integer id) {
        if (!proyectoRepository.existsById(id)) {
            throw new EntityNotFoundException("Proyecto no encontrado con id: " + id);
        }
        proyectoRepository.deleteById(id);
    }

    // ─── HELPERS ──────────────────────────────────────────────────────────────
    private Proyecto getProyectoOrThrow(Integer id) {
        return proyectoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Proyecto no encontrado con id: " + id));
    }

    private void validarFechas(ProyectoRequestDTO dto) {
        if (dto.getFechaEstimadaInicio() != null && dto.getFechaEstimadaFin() != null
                && dto.getFechaEstimadaFin().isBefore(dto.getFechaEstimadaInicio())) {
            throw new IllegalArgumentException(
                    "La fecha de fin estimada no puede ser anterior a la fecha de inicio.");
        }
    }

    private void mapDtoToEntity(ProyectoRequestDTO dto, Proyecto proyecto) {
        proyecto.setNombreProyecto(dto.getNombreProyecto());
        proyecto.setTipoProyecto(dto.getTipoProyecto());
        proyecto.setLugarProyecto(dto.getLugarProyecto());
        proyecto.setMunicipioProyecto(dto.getMunicipioProyecto());
        proyecto.setEstadoProyectoGeo(dto.getEstadoProyectoGeo());
        proyecto.setFechaEstimadaInicio(dto.getFechaEstimadaInicio());
        proyecto.setFechaEstimadaFin(dto.getFechaEstimadaFin());
        proyecto.setCalificacionProyecto(dto.getCalificacionProyecto());
        proyecto.setDescripcionProyecto(dto.getDescripcionProyecto());
    }

    // ─── MAPPER ───────────────────────────────────────────────────────────────
    private ProyectoResponseDTO toResponse(Proyecto p) {
        return ProyectoResponseDTO.builder()
                .idProyecto(p.getIdProyecto())
                .nombreProyecto(p.getNombreProyecto())
                .tipoProyecto(p.getTipoProyecto())
                .lugarProyecto(p.getLugarProyecto())
                .municipioProyecto(p.getMunicipioProyecto())
                .estadoProyectoGeo(p.getEstadoProyectoGeo())
                .fechaEstimadaInicio(p.getFechaEstimadaInicio())
                .fechaEstimadaFin(p.getFechaEstimadaFin())
                .calificacionProyecto(p.getCalificacionProyecto())
                .estatusProyecto(p.getEstatusProyecto())
                .descripcionProyecto(p.getDescripcionProyecto())
                .build();
    }
}
