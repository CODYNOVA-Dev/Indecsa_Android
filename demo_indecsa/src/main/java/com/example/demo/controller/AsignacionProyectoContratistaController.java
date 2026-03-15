package com.example.demo.controller;

import com.example.demo.dto.request.AsignacionProyectoContratistaRequestDTO;
import com.example.demo.dto.response.AsignacionProyectoContratistaResponseDTO;
import com.example.demo.model.AsignacionProyectoContratista.EstatusContrato;
import com.example.demo.service.AsignacionProyectoContratistaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/asignaciones/proyecto-contratista")
@RequiredArgsConstructor
public class AsignacionProyectoContratistaController {

    private final AsignacionProyectoContratistaService asignacionService;

    // ─── GET ALL ──────────────────────────────────────────────────────────────
    @GetMapping
    public ResponseEntity<List<AsignacionProyectoContratistaResponseDTO>> getAll() {
        return ResponseEntity.ok(asignacionService.findAll());
    }

    // ─── GET BY ID ────────────────────────────────────────────────────────────
    @GetMapping("/{id}")
    public ResponseEntity<AsignacionProyectoContratistaResponseDTO> getById(
            @PathVariable Integer id) {
        return ResponseEntity.ok(asignacionService.findById(id));
    }

    // ─── GET BY PROYECTO ──────────────────────────────────────────────────────
    @GetMapping("/proyecto/{idProyecto}")
    public ResponseEntity<List<AsignacionProyectoContratistaResponseDTO>> getByProyecto(
            @PathVariable Integer idProyecto) {
        return ResponseEntity.ok(asignacionService.findByProyecto(idProyecto));
    }

    // ─── GET BY CONTRATISTA ───────────────────────────────────────────────────
    @GetMapping("/contratista/{idContratista}")
    public ResponseEntity<List<AsignacionProyectoContratistaResponseDTO>> getByContratista(
            @PathVariable Integer idContratista) {
        return ResponseEntity.ok(asignacionService.findByContratista(idContratista));
    }

    // ─── GET BY ESTATUS ───────────────────────────────────────────────────────
    @GetMapping("/estatus/{estatus}")
    public ResponseEntity<List<AsignacionProyectoContratistaResponseDTO>> getByEstatus(
            @PathVariable EstatusContrato estatus) {
        return ResponseEntity.ok(asignacionService.findByEstatus(estatus));
    }

    // ─── CREATE ───────────────────────────────────────────────────────────────
    @PostMapping
    public ResponseEntity<AsignacionProyectoContratistaResponseDTO> create(
            @Valid @RequestBody AsignacionProyectoContratistaRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(asignacionService.create(dto));
    }

    // ─── UPDATE ───────────────────────────────────────────────────────────────
    @PutMapping("/{id}")
    public ResponseEntity<AsignacionProyectoContratistaResponseDTO> update(
            @PathVariable Integer id,
            @Valid @RequestBody AsignacionProyectoContratistaRequestDTO dto) {
        return ResponseEntity.ok(asignacionService.update(id, dto));
    }

    // ─── CAMBIAR ESTATUS CONTRATO ─────────────────────────────────────────────
    @PatchMapping("/{id}/estatus")
    public ResponseEntity<AsignacionProyectoContratistaResponseDTO> cambiarEstatus(
            @PathVariable Integer id,
            @RequestParam EstatusContrato estatus) {
        return ResponseEntity.ok(asignacionService.cambiarEstatus(id, estatus));
    }

    // ─── DELETE ───────────────────────────────────────────────────────────────
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        asignacionService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
