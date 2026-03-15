package com.openclassrooms.etudiant.controller;

import com.openclassrooms.etudiant.dto.EtudiantDTO;
import com.openclassrooms.etudiant.entities.Etudiant;
import com.openclassrooms.etudiant.service.EtudiantService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/etudiants")
@RequiredArgsConstructor
public class EtudiantController {

    private final EtudiantService etudiantService;

    @GetMapping
    public ResponseEntity<List<Etudiant>> findAll() {
        return ResponseEntity.ok(etudiantService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Etudiant> findById(@PathVariable Long id) {
        Optional<Etudiant> etudiant = etudiantService.findById(id);
        return etudiant.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Etudiant> create(@Valid @RequestBody EtudiantDTO dto) {
        Etudiant etudiant = new Etudiant();
        etudiant.setFirstName(dto.getFirstName());
        etudiant.setLastName(dto.getLastName());
        etudiant.setEmail(dto.getEmail());
        return new ResponseEntity<>(etudiantService.save(etudiant), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Etudiant> update(@PathVariable Long id, @Valid @RequestBody EtudiantDTO dto) {
        Optional<Etudiant> existing = etudiantService.findById(id);
        if (existing.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        Etudiant etudiant = new Etudiant();
        etudiant.setFirstName(dto.getFirstName());
        etudiant.setLastName(dto.getLastName());
        etudiant.setEmail(dto.getEmail());
        return ResponseEntity.ok(etudiantService.update(id, etudiant));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteById(@PathVariable Long id) {
        Optional<Etudiant> existing = etudiantService.findById(id);
        if (existing.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        etudiantService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}