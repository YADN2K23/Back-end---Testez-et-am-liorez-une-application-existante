package com.openclassrooms.etudiant.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.openclassrooms.etudiant.dto.EtudiantDTO;
import com.openclassrooms.etudiant.entities.Etudiant;
import com.openclassrooms.etudiant.repository.EtudiantRepository;
import com.openclassrooms.etudiant.service.EtudiantService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

/**
 * Tests d'intégration pour EtudiantController.
 * Utilise TestContainers pour démarrer une vraie BDD MySQL temporaire.
 * Teste toute la chaîne : Controller → Service → Repository → BDD.
 * @WithMockUser simule un utilisateur authentifié pour bypasser Spring Security.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@Testcontainers
@ActiveProfiles("test")
public class EtudiantControllerTest {

    private static final String URL = "/api/etudiants";
    private static final String FIRST_NAME = "Jean";
    private static final String LAST_NAME = "Dupont";
    private static final String EMAIL = "jean.dupont@test.com";

    @Container
    static MySQLContainer mySQLContainer = new MySQLContainer("mysql:8.0");

    @Autowired
    private EtudiantService etudiantService;
    @Autowired
    private EtudiantRepository etudiantRepository;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private MockMvc mockMvc;

    @DynamicPropertySource
    static void configureTestProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", () -> mySQLContainer.getJdbcUrl());
        registry.add("spring.datasource.username", () -> mySQLContainer.getUsername());
        registry.add("spring.datasource.password", () -> mySQLContainer.getPassword());
        registry.add("spring.jpa.hibernate.ddl-auto", () -> "create");
    }

    /**
     * Nettoie la BDD après chaque test pour garantir l'isolation des tests.
     */
    @AfterEach
    public void afterEach() {
        etudiantRepository.deleteAll();
    }

    /**
     * Vérifie que GET /api/etudiants retourne 200
     * lorsqu'un étudiant existe en base.
     */
    @Test
    @WithMockUser
    public void getAllEtudiants_returns_200() throws Exception {
        // GIVEN : un étudiant en base
        Etudiant etudiant = new Etudiant();
        etudiant.setFirstName(FIRST_NAME);
        etudiant.setLastName(LAST_NAME);
        etudiant.setEmail(EMAIL);
        etudiantService.save(etudiant);

        // WHEN - THEN : GET retourne 200
        mockMvc.perform(MockMvcRequestBuilders.get(URL)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    /**
     * Vérifie que GET /api/etudiants/{id} retourne 200
     * pour un étudiant existant.
     */
    @Test
    @WithMockUser
    public void getEtudiantById_returns_200() throws Exception {
        // GIVEN : un étudiant sauvegardé avec un id généré
        Etudiant etudiant = new Etudiant();
        etudiant.setFirstName(FIRST_NAME);
        etudiant.setLastName(LAST_NAME);
        etudiant.setEmail(EMAIL);
        Etudiant saved = etudiantService.save(etudiant);

        // WHEN - THEN : GET /{id} retourne 200
        mockMvc.perform(MockMvcRequestBuilders.get(URL + "/" + saved.getId())
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    /**
     * Vérifie que POST /api/etudiants retourne 201
     * lors de la création d'un nouvel étudiant valide.
     */
    @Test
    @WithMockUser
    public void createEtudiant_returns_201() throws Exception {
        // GIVEN : un DTO avec les données d'un nouvel étudiant
        EtudiantDTO dto = new EtudiantDTO();
        dto.setFirstName(FIRST_NAME);
        dto.setLastName(LAST_NAME);
        dto.setEmail(EMAIL);

        // WHEN - THEN : POST retourne 201 Created
        mockMvc.perform(MockMvcRequestBuilders.post(URL)
                        .content(objectMapper.writeValueAsString(dto))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(MockMvcResultMatchers.status().isCreated());
    }

    /**
     * Vérifie que PUT /api/etudiants/{id} retourne 200
     * lors de la modification d'un étudiant existant.
     */
    @Test
    @WithMockUser
    public void updateEtudiant_returns_200() throws Exception {
        // GIVEN : un étudiant existant et un DTO avec les nouvelles données
        Etudiant etudiant = new Etudiant();
        etudiant.setFirstName(FIRST_NAME);
        etudiant.setLastName(LAST_NAME);
        etudiant.setEmail(EMAIL);
        Etudiant saved = etudiantService.save(etudiant);

        EtudiantDTO dto = new EtudiantDTO();
        dto.setFirstName("Pierre");
        dto.setLastName("Martin");
        dto.setEmail("pierre.martin@test.com");

        // WHEN - THEN : PUT /{id} retourne 200
        mockMvc.perform(MockMvcRequestBuilders.put(URL + "/" + saved.getId())
                        .content(objectMapper.writeValueAsString(dto))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    /**
     * Vérifie que DELETE /api/etudiants/{id} retourne 204
     * lors de la suppression d'un étudiant existant.
     */
    @Test
    @WithMockUser
    public void deleteEtudiant_returns_204() throws Exception {
        // GIVEN : un étudiant existant à supprimer
        Etudiant etudiant = new Etudiant();
        etudiant.setFirstName(FIRST_NAME);
        etudiant.setLastName(LAST_NAME);
        etudiant.setEmail(EMAIL);
        Etudiant saved = etudiantService.save(etudiant);

        // WHEN - THEN : DELETE /{id} retourne 204 No Content
        mockMvc.perform(MockMvcRequestBuilders.delete(URL + "/" + saved.getId()))
                .andDo(print())
                .andExpect(MockMvcResultMatchers.status().isNoContent());
    }
}