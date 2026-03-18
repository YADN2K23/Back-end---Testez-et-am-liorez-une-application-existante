package com.openclassrooms.etudiant.service;

import com.openclassrooms.etudiant.entities.Etudiant;
import com.openclassrooms.etudiant.repository.EtudiantRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Tests unitaires pour EtudiantService.
 * Utilise Mockito pour simuler le Repository — aucune BDD réelle n'est utilisée.
 */
@ExtendWith(SpringExtension.class)
public class EtudiantServiceTest {

    private static final String FIRST_NAME = "Jean";
    private static final String LAST_NAME = "Dupont";
    private static final String EMAIL = "jean.dupont@test.com";

    @Mock
    private EtudiantRepository etudiantRepository;

    @InjectMocks
    private EtudiantService etudiantService;

    /**
     * Vérifie que findAll() retourne bien la liste des étudiants
     * lorsque le repository contient un étudiant.
     */
    @Test
    public void test_findAll_returns_list() {
        // GIVEN : un étudiant existe en base simulée
        Etudiant etudiant = new Etudiant();
        etudiant.setFirstName(FIRST_NAME);
        etudiant.setLastName(LAST_NAME);
        etudiant.setEmail(EMAIL);
        when(etudiantRepository.findAll()).thenReturn(List.of(etudiant));

        // WHEN : on appelle findAll()
        List<Etudiant> result = etudiantService.findAll();

        // THEN : la liste contient bien l'étudiant attendu
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getEmail()).isEqualTo(EMAIL);
    }

    /**
     * Vérifie que findById() retourne l'étudiant correspondant à l'id fourni.
     */
    @Test
    public void test_findById_returns_etudiant() {
        // GIVEN : un étudiant avec id=1 existe en base simulée
        Etudiant etudiant = new Etudiant();
        etudiant.setFirstName(FIRST_NAME);
        etudiant.setLastName(LAST_NAME);
        etudiant.setEmail(EMAIL);
        when(etudiantRepository.findById(1L)).thenReturn(Optional.of(etudiant));

        // WHEN : on cherche l'étudiant avec id=1
        Optional<Etudiant> result = etudiantService.findById(1L);

        // THEN : l'étudiant est bien retourné avec le bon email
        assertThat(result).isPresent();
        assertThat(result.get().getEmail()).isEqualTo(EMAIL);
    }

    /**
     * Vérifie que save() appelle bien repository.save() avec l'étudiant fourni.
     */
    @Test
    public void test_save_etudiant() {
        // GIVEN : un étudiant valide à sauvegarder
        Etudiant etudiant = new Etudiant();
        etudiant.setFirstName(FIRST_NAME);
        etudiant.setLastName(LAST_NAME);
        etudiant.setEmail(EMAIL);
        when(etudiantRepository.save(any())).thenReturn(etudiant);

        // WHEN : on sauvegarde l'étudiant
        etudiantService.save(etudiant);

        // THEN : le repository a bien été appelé avec le bon étudiant
        ArgumentCaptor<Etudiant> captor = ArgumentCaptor.forClass(Etudiant.class);
        verify(etudiantRepository).save(captor.capture());
        assertThat(captor.getValue().getEmail()).isEqualTo(EMAIL);
    }

    /**
     * Vérifie que update() met bien à jour l'id de l'étudiant
     * avant de l'enregistrer en base.
     */
    @Test
    public void test_update_etudiant() {
        // GIVEN : un étudiant à modifier avec id=1
        Etudiant etudiant = new Etudiant();
        etudiant.setFirstName(FIRST_NAME);
        etudiant.setLastName(LAST_NAME);
        etudiant.setEmail(EMAIL);
        when(etudiantRepository.save(any())).thenReturn(etudiant);

        // WHEN : on met à jour l'étudiant avec id=1
        etudiantService.update(1L, etudiant);

        // THEN : l'étudiant sauvegardé a bien l'id=1
        ArgumentCaptor<Etudiant> captor = ArgumentCaptor.forClass(Etudiant.class);
        verify(etudiantRepository).save(captor.capture());
        assertThat(captor.getValue().getId()).isEqualTo(1L);
    }

    /**
     * Vérifie que deleteById() appelle bien repository.deleteById() avec le bon id.
     */
    @Test
    public void test_deleteById() {
        // GIVEN : id=1 à supprimer
        // WHEN : on supprime l'étudiant avec id=1
        etudiantService.deleteById(1L);

        // THEN : le repository a bien été appelé avec id=1
        verify(etudiantRepository).deleteById(1L);
    }

    /**
     * Vérifie que save() lève une IllegalArgumentException
     * lorsqu'on lui passe un étudiant null.
     */
    @Test
    public void test_save_null_etudiant_throws_IllegalArgumentException() {
        // GIVEN : étudiant null
        // THEN : une exception est levée
        Assertions.assertThrows(IllegalArgumentException.class,
                () -> etudiantService.save(null));
    }
}