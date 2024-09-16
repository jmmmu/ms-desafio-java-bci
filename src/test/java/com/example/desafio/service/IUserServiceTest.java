package com.example.desafio.service;

import com.example.desafio.dto.UserRequestDTO;
import com.example.desafio.exception.EmailFormatException;
import com.example.desafio.exception.EmailRegisteredException;
import com.example.desafio.exception.PasswordFormatException;
import com.example.desafio.model.UserModel;
import com.example.desafio.repository.UserRepository;
import com.example.desafio.security.JwtProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ContextConfiguration(classes = {IUserService.class})
@ExtendWith(SpringExtension.class)
@TestPropertySource(properties = "validation.password.pattern=^([A-Z]{1})+(.*[a-z])+([0-9]{2})$")
class IUserServiceTest {
    @Autowired
    private IUserService iUserService;

    @MockBean
    private JwtProvider jwtProvider;

    @MockBean
    private PasswordEncoder passwordEncoder;

    @MockBean
    private UserRepository userRepository;

    private UserModel userModel;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        userModel = UserModel.builder()
                .id("b750edfc-9826-403c-9653-67d79544b079")
                .name("Jose Mellado")
                .email("jose@mellado.org")
                .token("eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJhbHZhcm9AbWFpbC5jb20iLCJpYXQiOjE3MDA3NDM0OTgsImV4cCI6MTcwMDc0NTI5OH0.i46CsOL_bhnBfpX-YiHEDPysK3QeP1zXDSS7MqlYFP0")
                .build();
    }

    @Test
    void testCreateUser() {
        when(userRepository.existsByEmail(Mockito.<String>any())).thenReturn(false);
        when(userRepository.save(any(UserModel.class))).thenReturn(userModel);
        assertNotNull(iUserService.createUser(UserRequestDTO.builder()
                .name("jose")
                .email("jose@mellado.org")
                .password("hunter2")
                .phones(new ArrayList<>())
                .build()));

    }

    @Test
    void testCreateUserEmailRegisteredException() {
        when(userRepository.existsByEmail(Mockito.<String>any())).thenReturn(true);
        EmailRegisteredException ex = assertThrows(EmailRegisteredException.class, () -> {
            iUserService.createUser(UserRequestDTO.builder()
                    .name("jose")
                    .email("jose@mellado.org")
                    .password("hunter2")
                    .phones(new ArrayList<>())
                    .build());
        });
        assertEquals("El correo ya esta registrado.", ex.getMessage());
    }

    @Test
    void testCreateUserEmailFormatException() {
        when(userRepository.existsByEmail(Mockito.<String>any())).thenReturn(false);
        EmailFormatException ex = assertThrows(EmailFormatException.class, () -> {
            iUserService.createUser(UserRequestDTO.builder()
                    .name("jose")
                    .email("josemellado.org")
                    .password("hunter2")
                    .phones(new ArrayList<>())
                    .build());
        });
        assertEquals("El correo no cumple formato correcto (aaaaaaa@dominio.cl).", ex.getMessage());
    }

    @Test
    void testCreateUserPasswordFormatException() {
        when(userRepository.existsByEmail(Mockito.<String>any())).thenReturn(false);
        PasswordFormatException ex = assertThrows(PasswordFormatException.class, () -> {
            iUserService.createUser(UserRequestDTO.builder()
                    .name("jose")
                    .email("jose@mellado.com")
                    .password("123456")
                    .phones(new ArrayList<>())
                    .build());
        });
        assertEquals("La clave no cumple formato correcto.", ex.getMessage());
    }
}