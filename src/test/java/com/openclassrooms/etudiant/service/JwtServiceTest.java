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

@ExtendWith(MockitoExtension.class)
public class JwtServiceTest {

    @InjectMocks
    private JwtService jwtService;

    private UserDetails createUser() {
        ReflectionTestUtils.setField(jwtService, "secretKey",
                "testSecretKeyForJwtThatIsLongEnoughToBeValid1234567890");
        return new User("testuser", "password", Collections.emptyList());
    }

    @Test
    public void test_generateToken_returns_token() {
        // GIVEN
        UserDetails user = createUser();

        // WHEN
        String token = jwtService.generateToken(user);

        // THEN
        assertThat(token).isNotNull();
        assertThat(token).isNotEmpty();
    }

    @Test
    public void test_extractUsername_returns_username() {
        // GIVEN
        UserDetails user = createUser();
        String token = jwtService.generateToken(user);

        // WHEN
        String username = jwtService.extractUsername(token);

        // THEN
        assertThat(username).isEqualTo("testuser");
    }

    @Test
    public void test_isTokenValid_returns_true() {
        // GIVEN
        UserDetails user = createUser();
        String token = jwtService.generateToken(user);

        // WHEN
        boolean valid = jwtService.isTokenValid(token, user);

        // THEN
        assertThat(valid).isTrue();
    }
}