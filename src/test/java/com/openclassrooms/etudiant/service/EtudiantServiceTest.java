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

@ExtendWith(SpringExtension.class)
public class EtudiantServiceTest {

    private static final String FIRST_NAME = "Jean";
    private static final String LAST_NAME = "Dupont";
    private static final String EMAIL = "jean.dupont@test.com";

    @Mock
    private EtudiantRepository etudiantRepository;

    @InjectMocks
    private EtudiantService etudiantService;

    @Test
    public void test_findAll_returns_list() {
        // GIVEN
        Etudiant etudiant = new Etudiant();
        etudiant.setFirstName(FIRST_NAME);
        etudiant.setLastName(LAST_NAME);
        etudiant.setEmail(EMAIL);
        when(etudiantRepository.findAll()).thenReturn(List.of(etudiant));

        // WHEN
        List<Etudiant> result = etudiantService.findAll();

        // THEN
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getEmail()).isEqualTo(EMAIL);
    }

    @Test
    public void test_findById_returns_etudiant() {
        // GIVEN
        Etudiant etudiant = new Etudiant();
        etudiant.setFirstName(FIRST_NAME);
        etudiant.setLastName(LAST_NAME);
        etudiant.setEmail(EMAIL);
        when(etudiantRepository.findById(1L)).thenReturn(Optional.of(etudiant));

        // WHEN
        Optional<Etudiant> result = etudiantService.findById(1L);

        // THEN
        assertThat(result).isPresent();
        assertThat(result.get().getEmail()).isEqualTo(EMAIL);
    }

    @Test
    public void test_save_etudiant() {
        // GIVEN
        Etudiant etudiant = new Etudiant();
        etudiant.setFirstName(FIRST_NAME);
        etudiant.setLastName(LAST_NAME);
        etudiant.setEmail(EMAIL);
        when(etudiantRepository.save(any())).thenReturn(etudiant);

        // WHEN
        etudiantService.save(etudiant);

        // THEN
        ArgumentCaptor<Etudiant> captor = ArgumentCaptor.forClass(Etudiant.class);
        verify(etudiantRepository).save(captor.capture());
        assertThat(captor.getValue().getEmail()).isEqualTo(EMAIL);
    }

    @Test
    public void test_update_etudiant() {
        // GIVEN
        Etudiant etudiant = new Etudiant();
        etudiant.setFirstName(FIRST_NAME);
        etudiant.setLastName(LAST_NAME);
        etudiant.setEmail(EMAIL);
        when(etudiantRepository.save(any())).thenReturn(etudiant);

        // WHEN
        etudiantService.update(1L, etudiant);

        // THEN
        ArgumentCaptor<Etudiant> captor = ArgumentCaptor.forClass(Etudiant.class);
        verify(etudiantRepository).save(captor.capture());
        assertThat(captor.getValue().getId()).isEqualTo(1L);
    }

    @Test
    public void test_deleteById() {
        // GIVEN - WHEN
        etudiantService.deleteById(1L);

        // THEN
        verify(etudiantRepository).deleteById(1L);
    }

    @Test
    public void test_save_null_etudiant_throws_IllegalArgumentException() {
        // GIVEN - THEN
        Assertions.assertThrows(IllegalArgumentException.class,
                () -> etudiantService.save(null));
    }
}