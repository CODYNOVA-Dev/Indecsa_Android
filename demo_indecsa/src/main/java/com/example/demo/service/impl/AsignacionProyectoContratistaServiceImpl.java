package com.example.demo.service.impl;

import com.example.demo.dto.request.AsignacionProyectoContratistaRequestDTO;
import com.example.demo.dto.response.AsignacionProyectoContratistaResponseDTO;
import com.example.demo.dto.response.ContratistaResponseDTO;
import com.example.demo.dto.response.ProyectoResponseDTO;
import com.example.demo.model.AsignacionProyectoContratista;
import com.example.demo.model.AsignacionProyectoContratista.EstatusContrato;
import com.example.demo.model.Contratista;
import com.example.demo.model.Proyecto;
import com.example.demo.repository.AsignacionProyectoContratistaRepository;
import com.example.demo.repository.ContratistaRepository;
import com.example.demo.repository.ProyectoRepository;
import com.example.demo.service.AsignacionProyectoContratistaService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AsignacionProyectoContratistaServiceImpl implements AsignacionProyectoContratistaService {

    private final AsignacionProyectoContratistaRepository asignacionRepository;
    private final ProyectoRepository proyectoRepository;
    private final ContratistaRepository contratistaRepository;

    // ─── FIND ALL ─────────────────────────────────────────────────────────────
    @Override
    @Transactional(readOnly = true)
    public List<AsignacionProyectoContratistaResponseDTO> findAll() {
        return asignacionRepository.findAll()
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    // ─── FIND BY ID ───────────────────────────────────────────────────────────
    @Override
    @Transactional(readOnly = true)
    public AsignacionProyectoContratistaResponseDTO findById(Integer id) {
        return toResponse(getAsignacionOrThrow(id));
    }

    // ─── FIND BY PROYECTO ─────────────────────────────────────────────────────
    @Override
    @Transactional(readOnly = true)
    public List<AsignacionProyectoContratistaResponseDTO> findByProyecto(Integer idProyecto) {
        return asignacionRepository.findByProyecto_IdProyecto(idProyecto)
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    // ─── FIND BY CONTRATISTA ──────────────────────────────────────────────────
    @Override
    @Transactional(readOnly = true)
    public List<AsignacionProyectoContratistaResponseDTO> findByContratista(Integer idContratista) {
        return asignacionRepository.findByContratista_IdContratista(idContratista)
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    // ─── FIND BY ESTATUS ──────────────────────────────────────────────────────
    @Override
    @Transactional(readOnly = true)
    public List<AsignacionProyectoContratistaResponseDTO> findByEstatus(EstatusContrato estatus) {
        return asignacionRepository.findByEstatusContrato(estatus)
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    // ─── CREATE ───────────────────────────────────────────────────────────────
    @Override
    @Transactional
    public AsignacionProyectoContratistaResponseDTO create(AsignacionProyectoContratistaRequestDTO dto) {
        Proyecto proyecto = proyectoRepository.findById(dto.getIdProyecto())
                .orElseThrow(() -> new EntityNotFoundException(
                        "Proyecto no encontrado con id: " + dto.getIdProyecto()));

        Contratista contratista = contratistaRepository.findById(dto.getIdContratista())
                .orElseThrow(() -> new EntityNotFoundException(
                        "Contratista no encontrado con id: " + dto.getIdContratista()));

        if (asignacionRepository.existsByProyecto_IdProyectoAndContratista_IdContratista(
                dto.getIdProyecto(), dto.getIdContratista())) {
            throw new IllegalArgumentException(
                    "El contratista ya está asignado a este proyecto.");
        }

        validarFechas(dto);

        AsignacionProyectoContratista asignacion = new AsignacionProyectoContratista();
        asignacion.setProyecto(proyecto);
        asignacion.setContratista(contratista);
        mapDtoToEntity(dto, asignacion);
        asignacion.setEstatusContrato(EstatusContrato.ACTIVO);
        return toResponse(asignacionRepository.save(asignacion));
    }

    // ─── UPDATE ───────────────────────────────────────────────────────────────
    @Override
    @Transactional
    public AsignacionProyectoContratistaResponseDTO update(Integer id, AsignacionProyectoContratistaRequestDTO dto) {
        AsignacionProyectoContratista asignacion = getAsignacionOrThrow(id);

        Proyecto proyecto = proyectoRepository.findById(dto.getIdProyecto())
                .orElseThrow(() -> new EntityNotFoundException(
                        "Proyecto no encontrado con id: " + dto.getIdProyecto()));

        Contratista contratista = contratistaRepository.findById(dto.getIdContratista())
                .orElseThrow(() -> new EntityNotFoundException(
                        "Contratista no encontrado con id: " + dto.getIdContratista()));

        boolean cambioRelacion = !asignacion.getProyecto().getIdProyecto().equals(dto.getIdProyecto())
                || !asignacion.getContratista().getIdContratista().equals(dto.getIdContratista());

        if (cambioRelacion && asignacionRepository.existsByProyecto_IdProyectoAndContratista_IdContratista(
                dto.getIdProyecto(), dto.getIdContratista())) {
            throw new IllegalArgumentException(
                    "El contratista ya está asignado a este proyecto.");
        }

        validarFechas(dto);
        asignacion.setProyecto(proyecto);
        asignacion.setContratista(contratista);
        mapDtoToEntity(dto, asignacion);
        return toResponse(asignacionRepository.save(asignacion));
    }

    // ─── CAMBIAR ESTATUS ──────────────────────────────────────────────────────
    @Override
    @Transactional
    public AsignacionProyectoContratistaResponseDTO cambiarEstatus(Integer id, EstatusContrato estatus) {
        AsignacionProyectoContratista asignacion = getAsignacionOrThrow(id);
        asignacion.setEstatusContrato(estatus);
        return toResponse(asignacionRepository.save(asignacion));
    }

    // ─── DELETE ───────────────────────────────────────────────────────────────
    @Override
    @Transactional
    public void delete(Integer id) {
        if (!asignacionRepository.existsById(id)) {
            throw new EntityNotFoundException("Asignación no encontrada con id: " + id);
        }
        asignacionRepository.deleteById(id);
    }

    // ─── HELPERS ──────────────────────────────────────────────────────────────
    private AsignacionProyectoContratista getAsignacionOrThrow(Integer id) {
        return asignacionRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Asignación proyecto-contratista no encontrada con id: " + id));
    }

    private void validarFechas(AsignacionProyectoContratistaRequestDTO dto) {
        if (dto.getFechaInicio() != null && dto.getFechaFinEstimada() != null
                && dto.getFechaFinEstimada().isBefore(dto.getFechaInicio())) {
            throw new IllegalArgumentException(
                    "La fecha fin del contrato no puede ser anterior a la fecha de inicio.");
        }
    }

    private void mapDtoToEntity(AsignacionProyectoContratistaRequestDTO dto,
                                AsignacionProyectoContratista asignacion) {
        asignacion.setNumeroContrato(dto.getNumeroContrato());
        asignacion.setFechaInicio(dto.getFechaInicio());
        asignacion.setFechaFinEstimada(dto.getFechaFinEstimada());
        asignacion.setMontoContratado(dto.getMontoContratado());
        asignacion.setObservaciones(dto.getObservaciones());
    }

    // ─── MAPPER ───────────────────────────────────────────────────────────────
    private AsignacionProyectoContratistaResponseDTO toResponse(AsignacionProyectoContratista a) {
        Proyecto p = a.getProyecto();
        Contratista c = a.getContratista();

        ProyectoResponseDTO proyectoDTO = ProyectoResponseDTO.builder()
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

        ContratistaResponseDTO contratistaDTO = ContratistaResponseDTO.builder()
                .idContratista(c.getIdContratista())
                .rfcContratista(c.getRfcContratista())
                .telefonoContratista(c.getTelefonoContratista())
                .correoContratista(c.getCorreoContratista())
                .descripcionContratista(c.getDescripcionContratista())
                .experiencia(c.getExperiencia())
                .calificacionContratista(c.getCalificacionContratista())
                .estadoContratista(c.getEstadoContratista())
                .build();

        return AsignacionProyectoContratistaResponseDTO.builder()
                .idAsignacionPc(a.getIdAsignacionPc())
                .proyecto(proyectoDTO)
                .contratista(contratistaDTO)
                .numeroContrato(a.getNumeroContrato())
                .fechaInicio(a.getFechaInicio())
                .fechaFinEstimada(a.getFechaFinEstimada())
                .montoContratado(a.getMontoContratado())
                .estatusContrato(a.getEstatusContrato())
                .observaciones(a.getObservaciones())
                .build();
    }
}
