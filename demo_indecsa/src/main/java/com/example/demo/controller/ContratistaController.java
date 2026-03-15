package com.example.demo.controller;

import com.example.demo.dto.request.ContratistaRequestDTO;
import com.example.demo.dto.response.ContratistaResponseDTO;
import com.example.demo.model.Contratista.EstadoContratista;
import com.example.demo.service.ContratistaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/contratistas")
@RequiredArgsConstructor
public class ContratistaController {

    private final ContratistaService contratistaService;

    // ─── GET ALL ──────────────────────────────────────────────────────────────
    @GetMapping
    public ResponseEntity<List<ContratistaResponseDTO>> getAll() {
        return ResponseEntity.ok(contratistaService.findAll());
    }

    // ─── GET BY ID ────────────────────────────────────────────────────────────
    @GetMapping("/{id}")
    public ResponseEntity<ContratistaResponseDTO> getById(@PathVariable Integer id) {
        return ResponseEntity.ok(contratistaService.findById(id));
    }

    // ─── GET BY ESTADO ────────────────────────────────────────────────────────
    @GetMapping("/estado/{estado}")
    public ResponseEntity<List<ContratistaResponseDTO>> getByEstado(
            @PathVariable EstadoContratista estado) {
        return ResponseEntity.ok(contratistaService.findByEstado(estado));
    }

    // ─── CREATE ───────────────────────────────────────────────────────────────
    @PostMapping
    public ResponseEntity<ContratistaResponseDTO> create(@Valid @RequestBody ContratistaRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(contratistaService.create(dto));
    }

    // ─── UPDATE ───────────────────────────────────────────────────────────────
    @PutMapping("/{id}")
    public ResponseEntity<ContratistaResponseDTO> update(
            @PathVariable Integer id,
            @Valid @RequestBody ContratistaRequestDTO dto) {
        return ResponseEntity.ok(contratistaService.update(id, dto));
    }

    // ─── CAMBIAR ESTADO ───────────────────────────────────────────────────────
    @PatchMapping("/{id}/estado")
    public ResponseEntity<ContratistaResponseDTO> cambiarEstado(
            @PathVariable Integer id,
            @RequestParam EstadoContratista estado) {
        return ResponseEntity.ok(contratistaService.cambiarEstado(id, estado));
    }

    // ─── DELETE ───────────────────────────────────────────────────────────────
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        contratistaService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
