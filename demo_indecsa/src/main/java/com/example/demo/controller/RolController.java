package com.example.demo.controller;

import com.example.demo.dto.request.RolRequestDTO;
import com.example.demo.dto.response.RolResponseDTO;
import com.example.demo.service.RolService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/roles")
@RequiredArgsConstructor
public class RolController {

    private final RolService rolService;

    // ─── GET ALL ──────────────────────────────────────────────────────────────
    @GetMapping
    public ResponseEntity<List<RolResponseDTO>> getAll() {
        return ResponseEntity.ok(rolService.findAll());
    }

    // ─── GET BY ID ────────────────────────────────────────────────────────────
    @GetMapping("/{id}")
    public ResponseEntity<RolResponseDTO> getById(@PathVariable Integer id) {
        return ResponseEntity.ok(rolService.findById(id));
    }

    // ─── CREATE ───────────────────────────────────────────────────────────────
    @PostMapping
    public ResponseEntity<RolResponseDTO> create(@Valid @RequestBody RolRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(rolService.create(dto));
    }

    // ─── UPDATE ───────────────────────────────────────────────────────────────
    @PutMapping("/{id}")
    public ResponseEntity<RolResponseDTO> update(
            @PathVariable Integer id,
            @Valid @RequestBody RolRequestDTO dto) {
        return ResponseEntity.ok(rolService.update(id, dto));
    }

    // ─── DELETE ───────────────────────────────────────────────────────────────
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        rolService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
