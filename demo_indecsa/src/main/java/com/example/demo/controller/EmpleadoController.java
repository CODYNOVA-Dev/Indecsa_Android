package com.example.demo.controller;

import com.example.demo.dto.request.EmpleadoRequestDTO;
import com.example.demo.dto.response.EmpleadoResponseDTO;
import com.example.demo.service.EmpleadoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/empleados")
@RequiredArgsConstructor
public class EmpleadoController {

    private final EmpleadoService empleadoService;

    // ─── GET ALL ──────────────────────────────────────────────────────────────
    @GetMapping
    public ResponseEntity<List<EmpleadoResponseDTO>> getAll() {
        return ResponseEntity.ok(empleadoService.findAll());
    }

    // ─── GET BY ID ────────────────────────────────────────────────────────────
    @GetMapping("/{id}")
    public ResponseEntity<EmpleadoResponseDTO> getById(@PathVariable Integer id) {
        return ResponseEntity.ok(empleadoService.findById(id));
    }

    // ─── GET BY ROL ───────────────────────────────────────────────────────────
    @GetMapping("/rol/{idRol}")
    public ResponseEntity<List<EmpleadoResponseDTO>> getByRol(@PathVariable Integer idRol) {
        return ResponseEntity.ok(empleadoService.findByRol(idRol));
    }

    // ─── CREATE ───────────────────────────────────────────────────────────────
    @PostMapping
    public ResponseEntity<EmpleadoResponseDTO> create(@Valid @RequestBody EmpleadoRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(empleadoService.create(dto));
    }

    // ─── UPDATE ───────────────────────────────────────────────────────────────
    @PutMapping("/{id}")
    public ResponseEntity<EmpleadoResponseDTO> update(
            @PathVariable Integer id,
            @Valid @RequestBody EmpleadoRequestDTO dto) {
        return ResponseEntity.ok(empleadoService.update(id, dto));
    }

    // ─── DELETE ───────────────────────────────────────────────────────────────
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        empleadoService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
