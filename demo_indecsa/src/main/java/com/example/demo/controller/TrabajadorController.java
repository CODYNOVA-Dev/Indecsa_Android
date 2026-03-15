package com.example.demo.controller;

import com.example.demo.dto.request.TrabajadorRequestDTO;
import com.example.demo.dto.response.TrabajadorResponseDTO;
import com.example.demo.model.Trabajador.EstadoTrabajador;
import com.example.demo.service.TrabajadorService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/trabajadores")
@RequiredArgsConstructor
public class TrabajadorController {

    private final TrabajadorService trabajadorService;

    // ─── GET ALL ──────────────────────────────────────────────────────────────
    @GetMapping
    public ResponseEntity<List<TrabajadorResponseDTO>> getAll() {
        return ResponseEntity.ok(trabajadorService.findAll());
    }

    // ─── GET BY ID ────────────────────────────────────────────────────────────
    @GetMapping("/{id}")
    public ResponseEntity<TrabajadorResponseDTO> getById(@PathVariable Integer id) {
        return ResponseEntity.ok(trabajadorService.findById(id));
    }

    // ─── GET BY ESTADO ────────────────────────────────────────────────────────
    @GetMapping("/estado/{estado}")
    public ResponseEntity<List<TrabajadorResponseDTO>> getByEstado(
            @PathVariable EstadoTrabajador estado) {
        return ResponseEntity.ok(trabajadorService.findByEstado(estado));
    }

    // ─── GET BY ESPECIALIDAD ──────────────────────────────────────────────────
    @GetMapping("/especialidad/{especialidad}")
    public ResponseEntity<List<TrabajadorResponseDTO>> getByEspecialidad(
            @PathVariable String especialidad) {
        return ResponseEntity.ok(trabajadorService.findByEspecialidad(especialidad));
    }

    // ─── CREATE ───────────────────────────────────────────────────────────────
    @PostMapping
    public ResponseEntity<TrabajadorResponseDTO> create(@Valid @RequestBody TrabajadorRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(trabajadorService.create(dto));
    }

    // ─── UPDATE ───────────────────────────────────────────────────────────────
    @PutMapping("/{id}")
    public ResponseEntity<TrabajadorResponseDTO> update(
            @PathVariable Integer id,
            @Valid @RequestBody TrabajadorRequestDTO dto) {
        return ResponseEntity.ok(trabajadorService.update(id, dto));
    }

    // ─── CAMBIAR ESTADO ───────────────────────────────────────────────────────
    @PatchMapping("/{id}/estado")
    public ResponseEntity<TrabajadorResponseDTO> cambiarEstado(
            @PathVariable Integer id,
            @RequestParam EstadoTrabajador estado) {
        return ResponseEntity.ok(trabajadorService.cambiarEstado(id, estado));
    }

    // ─── DELETE ───────────────────────────────────────────────────────────────
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        trabajadorService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
