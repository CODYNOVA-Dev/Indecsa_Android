package com.example.demo.controller;

import com.example.demo.dto.request.AsignacionTrabajadorProyectoRequestDTO;
import com.example.demo.dto.response.AsignacionTrabajadorProyectoResponseDTO;
import com.example.demo.model.AsignacionTrabajadorProyecto.EstatusAsignacion;
import com.example.demo.service.AsignacionTrabajadorProyectoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/asignaciones/trabajador-proyecto")
@RequiredArgsConstructor
public class AsignacionTrabajadorProyectoController {

    private final AsignacionTrabajadorProyectoService asignacionService;

    // ─── GET ALL ──────────────────────────────────────────────────────────────
    @GetMapping
    public ResponseEntity<List<AsignacionTrabajadorProyectoResponseDTO>> getAll() {
        return ResponseEntity.ok(asignacionService.findAll());
    }

    // ─── GET BY ID ────────────────────────────────────────────────────────────
    @GetMapping("/{id}")
    public ResponseEntity<AsignacionTrabajadorProyectoResponseDTO> getById(
            @PathVariable Integer id) {
        return ResponseEntity.ok(asignacionService.findById(id));
    }

    // ─── GET BY PROYECTO ──────────────────────────────────────────────────────
    @GetMapping("/proyecto/{idProyecto}")
    public ResponseEntity<List<AsignacionTrabajadorProyectoResponseDTO>> getByProyecto(
            @PathVariable Integer idProyecto) {
        return ResponseEntity.ok(asignacionService.findByProyecto(idProyecto));
    }

    // ─── GET BY TRABAJADOR ────────────────────────────────────────────────────
    @GetMapping("/trabajador/{idTrabajador}")
    public ResponseEntity<List<AsignacionTrabajadorProyectoResponseDTO>> getByTrabajador(
            @PathVariable Integer idTrabajador) {
        return ResponseEntity.ok(asignacionService.findByTrabajador(idTrabajador));
    }

    // ─── GET BY ASIGNACION PC ─────────────────────────────────────────────────
    @GetMapping("/contrato/{idAsignacionPc}")
    public ResponseEntity<List<AsignacionTrabajadorProyectoResponseDTO>> getByAsignacionPc(
            @PathVariable Integer idAsignacionPc) {
        return ResponseEntity.ok(asignacionService.findByAsignacionPc(idAsignacionPc));
    }

    // ─── GET BY ESTATUS ───────────────────────────────────────────────────────
    @GetMapping("/estatus/{estatus}")
    public ResponseEntity<List<AsignacionTrabajadorProyectoResponseDTO>> getByEstatus(
            @PathVariable EstatusAsignacion estatus) {
        return ResponseEntity.ok(asignacionService.findByEstatus(estatus));
    }

    // ─── CREATE ───────────────────────────────────────────────────────────────
    @PostMapping
    public ResponseEntity<AsignacionTrabajadorProyectoResponseDTO> create(
            @Valid @RequestBody AsignacionTrabajadorProyectoRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(asignacionService.create(dto));
    }

    // ─── UPDATE ───────────────────────────────────────────────────────────────
    @PutMapping("/{id}")
    public ResponseEntity<AsignacionTrabajadorProyectoResponseDTO> update(
            @PathVariable Integer id,
            @Valid @RequestBody AsignacionTrabajadorProyectoRequestDTO dto) {
        return ResponseEntity.ok(asignacionService.update(id, dto));
    }

    // ─── CAMBIAR ESTATUS ASIGNACIÓN ───────────────────────────────────────────
    @PatchMapping("/{id}/estatus")
    public ResponseEntity<AsignacionTrabajadorProyectoResponseDTO> cambiarEstatus(
            @PathVariable Integer id,
            @RequestParam EstatusAsignacion estatus) {
        return ResponseEntity.ok(asignacionService.cambiarEstatus(id, estatus));
    }

    // ─── DELETE ───────────────────────────────────────────────────────────────
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        asignacionService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
