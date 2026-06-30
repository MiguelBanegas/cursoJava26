package com.punto_venta.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.punto_venta.dto.AuthResponseDTO;
import com.punto_venta.dto.LoginRequestDTO;
import com.punto_venta.dto.RegisterRequestDTO;
import com.punto_venta.exception.InvalidCredentialsException;
import com.punto_venta.exception.UsernameAlreadyExistsException;
import com.punto_venta.model.Role;
import com.punto_venta.service.AuthService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @SuppressWarnings("removal")
    @MockBean
    private AuthService authService;

    @SuppressWarnings("null")
@Test
    void loginSuccess() throws Exception {
        LoginRequestDTO request = new LoginRequestDTO();
        request.setUsername("admin");
        request.setPassword("admin123");

        when(authService.login(any(LoginRequestDTO.class)))
                .thenReturn(new AuthResponseDTO("token-jwt", "admin", Role.ADMIN));

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token", is("token-jwt")))
                .andExpect(jsonPath("$.username", is("admin")))
                .andExpect(jsonPath("$.role", is("ADMIN")));
    }

    @SuppressWarnings("null")
@Test
    void loginInvalidCredentials() throws Exception {
        LoginRequestDTO request = new LoginRequestDTO();
        request.setUsername("admin");
        request.setPassword("wrong");

        when(authService.login(any(LoginRequestDTO.class)))
                .thenThrow(new InvalidCredentialsException("Credenciales inválidas"));

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.status", is(401)))
                .andExpect(jsonPath("$.message", is("Credenciales inválidas")));
    }

    @SuppressWarnings("null")
@Test
    void registerRequiresAdmin() throws Exception {
        RegisterRequestDTO request = new RegisterRequestDTO();
        request.setUsername("cajero");
        request.setPassword("cajero123");
        request.setRole(Role.USER);

        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized());
    }

    @SuppressWarnings("null")
@Test
    @WithMockUser(roles = "ADMIN")
    void registerSuccessAsAdmin() throws Exception {
        RegisterRequestDTO request = new RegisterRequestDTO();
        request.setUsername("cajero");
        request.setPassword("cajero123");
        request.setRole(Role.USER);

        when(authService.register(any(RegisterRequestDTO.class)))
                .thenReturn(new AuthResponseDTO("token-nuevo", "cajero", Role.USER));

        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.username", is("cajero")))
                .andExpect(jsonPath("$.role", is("USER")));
    }

    @SuppressWarnings("null")
@Test
    @WithMockUser(roles = "ADMIN")
    void registerUsernameAlreadyExists() throws Exception {
        RegisterRequestDTO request = new RegisterRequestDTO();
        request.setUsername("admin");
        request.setPassword("admin123");
        request.setRole(Role.ADMIN);

        when(authService.register(any(RegisterRequestDTO.class)))
                .thenThrow(new UsernameAlreadyExistsException("El username ya está en uso: admin"));

        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status", is(409)));
    }
}
