package com.openclassrooms.etudiant.service;

import com.openclassrooms.etudiant.entities.Etudiant;
import com.openclassrooms.etudiant.repository.EtudiantRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class EtudiantService {

    private final EtudiantRepository etudiantRepository;

    public List<Etudiant> findAll() {
        log.info("Fetching all etudiants");
        return etudiantRepository.findAll();
    }

    public Optional<Etudiant> findById(Long id) {
        log.info("Fetching etudiant with id {}", id);
        return etudiantRepository.findById(id);
    }

    public Etudiant save(Etudiant etudiant) {
        Assert.notNull(etudiant, "Etudiant must not be null");
        log.info("Saving etudiant");
        return etudiantRepository.save(etudiant);
    }

    public Etudiant update(Long id, Etudiant etudiant) {
        Assert.notNull(etudiant, "Etudiant must not be null");
        log.info("Updating etudiant with id {}", id);
        etudiant.setId(id);
        return etudiantRepository.save(etudiant);
    }

    public void deleteById(Long id) {
        log.info("Deleting etudiant with id {}", id);
        etudiantRepository.deleteById(id);
    }
}