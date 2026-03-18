package com.openclassrooms.etudiant.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests unitaires pour JwtService.
 * Vérifie la génération, l'extraction et la validation des tokens JWT.
 * Utilise ReflectionTestUtils pour injecter la clé secrète sans contexte Spring.
 */
@ExtendWith(MockitoExtension.class)
public class JwtServiceTest {

    @InjectMocks
    private JwtService jwtService;

    /**
     * Méthode utilitaire — crée un UserDetails de test
     * et injecte une clé secrète valide dans JwtService.
     */
    private UserDetails createUser() {
        ReflectionTestUtils.setField(jwtService, "secretKey",
                "testSecretKeyForJwtThatIsLongEnoughToBeValid1234567890");
        return new User("testuser", "password", Collections.emptyList());
    }

    /**
     * Vérifie que generateToken() retourne un token non null et non vide
     * pour un utilisateur valide.
     */
    @Test
    public void test_generateToken_returns_token() {
        // GIVEN : un utilisateur valide
        UserDetails user = createUser();

        // WHEN : on génère un token JWT
        String token = jwtService.generateToken(user);

        // THEN : le token est bien généré
        assertThat(token).isNotNull();
        assertThat(token).isNotEmpty();
    }

    /**
     * Vérifie que extractUsername() retourne bien le login
     * de l'utilisateur encodé dans le token JWT.
     */
    @Test
    public void test_extractUsername_returns_username() {
        // GIVEN : un token JWT généré pour "testuser"
        UserDetails user = createUser();
        String token = jwtService.generateToken(user);

        // WHEN : on extrait le username du token
        String username = jwtService.extractUsername(token);

        // THEN : le username extrait correspond bien à "testuser"
        assertThat(username).isEqualTo("testuser");
    }

    /**
     * Vérifie que isTokenValid() retourne true
     * pour un token généré pour le même utilisateur.
     */
    @Test
    public void test_isTokenValid_returns_true() {
        // GIVEN : un token JWT valide pour "testuser"
        UserDetails user = createUser();
        String token = jwtService.generateToken(user);

        // WHEN : on valide le token
        boolean valid = jwtService.isTokenValid(token, user);

        // THEN : le token est bien valide
        assertThat(valid).isTrue();
    }
}