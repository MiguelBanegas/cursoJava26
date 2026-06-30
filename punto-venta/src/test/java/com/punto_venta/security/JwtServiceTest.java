package com.punto_venta.security;

import com.punto_venta.config.JwtProperties;
import com.punto_venta.model.Role;
import com.punto_venta.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.userdetails.UserDetails;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class JwtServiceTest {

    private JwtService jwtService;

    @BeforeEach
    void setUp() {
        JwtProperties properties = new JwtProperties();
        properties.setSecret("CursoJava26PuntoVentaSecretKeyParaHS256Minimo256Bits==");
        properties.setExpirationMs(3600000);
        jwtService = new JwtService(properties);
    }

    @Test
    void generateAndValidateToken() {
        User user = new User("admin", "encoded", Role.ADMIN);
        String token = jwtService.generateToken(user);

        UserDetails userDetails = org.springframework.security.core.userdetails.User
                .withUsername("admin")
                .password("encoded")
                .roles("ADMIN")
                .build();

        assertEquals("admin", jwtService.extractUsername(token));
        assertEquals(Role.ADMIN, jwtService.extractRole(token));
        assertTrue(jwtService.isTokenValid(token, userDetails));
        assertFalse(jwtService.isTokenExpired(token));
    }

    @Test
    void expiredTokenIsInvalid() {
        JwtProperties properties = new JwtProperties();
        properties.setSecret("CursoJava26PuntoVentaSecretKeyParaHS256Minimo256Bits==");
        properties.setExpirationMs(-1000);
        JwtService shortLivedJwtService = new JwtService(properties);

        User user = new User("admin", "encoded", Role.ADMIN);
        String token = shortLivedJwtService.generateToken(user);

        UserDetails userDetails = org.springframework.security.core.userdetails.User
                .withUsername("admin")
                .password("encoded")
                .roles("ADMIN")
                .build();

        assertTrue(shortLivedJwtService.isTokenExpired(token));
        assertFalse(shortLivedJwtService.isTokenValid(token, userDetails));
    }
}
