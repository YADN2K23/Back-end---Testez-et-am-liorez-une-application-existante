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
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.springframework.security.test.context.support.WithMockUser;

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

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

    @AfterEach
    public void afterEach() {
        etudiantRepository.deleteAll();
    }

    @Test
    @WithMockUser
    public void getAllEtudiants_returns_200() throws Exception {
        // GIVEN
        Etudiant etudiant = new Etudiant();
        etudiant.setFirstName(FIRST_NAME);
        etudiant.setLastName(LAST_NAME);
        etudiant.setEmail(EMAIL);
        etudiantService.save(etudiant);

        // WHEN
        mockMvc.perform(MockMvcRequestBuilders.get(URL)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    @WithMockUser
    public void getEtudiantById_returns_200() throws Exception {
        // GIVEN
        Etudiant etudiant = new Etudiant();
        etudiant.setFirstName(FIRST_NAME);
        etudiant.setLastName(LAST_NAME);
        etudiant.setEmail(EMAIL);
        Etudiant saved = etudiantService.save(etudiant);

        // WHEN
        mockMvc.perform(MockMvcRequestBuilders.get(URL + "/" + saved.getId())
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    @WithMockUser
    public void createEtudiant_returns_201() throws Exception {
        // GIVEN
        EtudiantDTO dto = new EtudiantDTO();
        dto.setFirstName(FIRST_NAME);
        dto.setLastName(LAST_NAME);
        dto.setEmail(EMAIL);

        // WHEN
        mockMvc.perform(MockMvcRequestBuilders.post(URL)
                        .content(objectMapper.writeValueAsString(dto))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(MockMvcResultMatchers.status().isCreated());
    }

    @Test
    @WithMockUser
    public void updateEtudiant_returns_200() throws Exception {
        // GIVEN
        Etudiant etudiant = new Etudiant();
        etudiant.setFirstName(FIRST_NAME);
        etudiant.setLastName(LAST_NAME);
        etudiant.setEmail(EMAIL);
        Etudiant saved = etudiantService.save(etudiant);

        EtudiantDTO dto = new EtudiantDTO();
        dto.setFirstName("Pierre");
        dto.setLastName("Martin");
        dto.setEmail("pierre.martin@test.com");

        // WHEN
        mockMvc.perform(MockMvcRequestBuilders.put(URL + "/" + saved.getId())
                        .content(objectMapper.writeValueAsString(dto))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    @WithMockUser
    public void deleteEtudiant_returns_204() throws Exception {
        // GIVEN
        Etudiant etudiant = new Etudiant();
        etudiant.setFirstName(FIRST_NAME);
        etudiant.setLastName(LAST_NAME);
        etudiant.setEmail(EMAIL);
        Etudiant saved = etudiantService.save(etudiant);

        // WHEN
        mockMvc.perform(MockMvcRequestBuilders.delete(URL + "/" + saved.getId()))
                .andDo(print())
                .andExpect(MockMvcResultMatchers.status().isNoContent());
    }
}