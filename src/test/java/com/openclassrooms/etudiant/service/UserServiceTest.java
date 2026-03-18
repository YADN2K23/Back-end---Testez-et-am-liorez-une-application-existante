package com.openclassrooms.etudiant.service;

import com.openclassrooms.etudiant.entities.User;
import com.openclassrooms.etudiant.repository.UserRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Tests unitaires pour UserService.
 * Utilise Mockito pour simuler UserRepository et PasswordEncoder.
 * Aucune BDD réelle n'est utilisée.
 */
@ExtendWith(SpringExtension.class)
public class UserServiceTest {

    private static final String FIRST_NAME = "John";
    private static final String LAST_NAME = "Doe";
    private static final String LOGIN = "LOGIN";
    private static final String PASSWORD = "PASSWORD";

    @Mock
    private UserRepository userRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @InjectMocks
    private UserService userService;

    /**
     * Vérifie que register() lève une IllegalArgumentException
     * lorsqu'on lui passe un utilisateur null.
     */
    @Test
    public void test_create_null_user_throws_IllegalArgumentException() {
        // GIVEN : utilisateur null
        // THEN : une exception est levée
        Assertions.assertThrows(IllegalArgumentException.class,
                () -> userService.register(null));
    }

    /**
     * Vérifie que register() lève une IllegalArgumentException
     * lorsque le login existe déjà en base.
     */
    @Test
    public void test_create_already_exist_user_throws_IllegalArgumentException() {
        // GIVEN : un utilisateur avec le même login existe déjà
        User user = new User();
        user.setFirstName(FIRST_NAME);
        user.setLastName(LAST_NAME);
        user.setLogin(LOGIN);
        user.setPassword(PASSWORD);
        when(passwordEncoder.encode(PASSWORD)).thenReturn(PASSWORD);
        when(userRepository.findByLogin(any())).thenReturn(Optional.of(user));

        // THEN : une exception est levée car le login est déjà pris
        Assertions.assertThrows(IllegalArgumentException.class,
                () -> userService.register(user));
    }

    /**
     * Vérifie que register() sauvegarde bien l'utilisateur en base
     * avec le mot de passe encodé.
     */
    @Test
    public void test_create_user() {
        // GIVEN : un nouvel utilisateur valide
        User user = new User();
        user.setFirstName(FIRST_NAME);
        user.setLastName(LAST_NAME);
        user.setLogin(LOGIN);
        user.setPassword(PASSWORD);
        when(passwordEncoder.encode(PASSWORD)).thenReturn(PASSWORD);
        when(userRepository.findByLogin(any())).thenReturn(Optional.empty());

        // WHEN : on enregistre l'utilisateur
        userService.register(user);

        // THEN : le repository a bien été appelé avec le bon utilisateur
        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(userCaptor.capture());
        assertThat(userCaptor.getValue()).isEqualTo(user);
    }
}