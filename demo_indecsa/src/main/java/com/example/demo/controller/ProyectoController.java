package com.example.demo.controller;

import com.example.demo.dto.request.ProyectoRequestDTO;
import com.example.demo.dto.response.ProyectoResponseDTO;
import com.example.demo.model.Proyecto.EstatusProyecto;
import com.example.demo.service.ProyectoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/proyectos")
@RequiredArgsConstructor
public class ProyectoController {

    private final ProyectoService proyectoService;

    // ─── GET ALL ──────────────────────────────────────────────────────────────
    @GetMapping
    public ResponseEntity<List<ProyectoResponseDTO>> getAll() {
        return ResponseEntity.ok(proyectoService.findAll());
    }

    // ─── GET BY ID ────────────────────────────────────────────────────────────
    @GetMapping("/{id}")
    public ResponseEntity<ProyectoResponseDTO> getById(@PathVariable Integer id) {
        return ResponseEntity.ok(proyectoService.findById(id));
    }

    // ─── GET BY ESTATUS ───────────────────────────────────────────────────────
    @GetMapping("/estatus/{estatus}")
    public ResponseEntity<List<ProyectoResponseDTO>> getByEstatus(
            @PathVariable EstatusProyecto estatus) {
        return ResponseEntity.ok(proyectoService.findByEstatus(estatus));
    }

    // ─── GET BY MUNICIPIO ─────────────────────────────────────────────────────
    @GetMapping("/municipio/{municipio}")
    public ResponseEntity<List<ProyectoResponseDTO>> getByMunicipio(
            @PathVariable String municipio) {
        return ResponseEntity.ok(proyectoService.findByMunicipio(municipio));
    }

    // ─── CREATE ───────────────────────────────────────────────────────────────
    @PostMapping
    public ResponseEntity<ProyectoResponseDTO> create(@Valid @RequestBody ProyectoRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(proyectoService.create(dto));
    }

    // ─── UPDATE ───────────────────────────────────────────────────────────────
    @PutMapping("/{id}")
    public ResponseEntity<ProyectoResponseDTO> update(
            @PathVariable Integer id,
            @Valid @RequestBody ProyectoRequestDTO dto) {
        return ResponseEntity.ok(proyectoService.update(id, dto));
    }

    // ─── CAMBIAR ESTATUS ──────────────────────────────────────────────────────
    @PatchMapping("/{id}/estatus")
    public ResponseEntity<ProyectoResponseDTO> cambiarEstatus(
            @PathVariable Integer id,
            @RequestParam EstatusProyecto estatus) {
        return ResponseEntity.ok(proyectoService.cambiarEstatus(id, estatus));
    }

    // ─── DELETE ───────────────────────────────────────────────────────────────
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        proyectoService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
