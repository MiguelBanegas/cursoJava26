package com.punto_venta.service;

import com.punto_venta.dto.AuthResponseDTO;
import com.punto_venta.dto.LoginRequestDTO;
import com.punto_venta.dto.RegisterRequestDTO;
import com.punto_venta.exception.InvalidCredentialsException;
import com.punto_venta.exception.UsernameAlreadyExistsException;
import com.punto_venta.model.User;
import com.punto_venta.repository.UserRepository;
import com.punto_venta.security.JwtService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtService jwtService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    public AuthResponseDTO login(LoginRequestDTO request) {
        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new InvalidCredentialsException("Credenciales inválidas"));

        if (!user.isActivo() || !passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new InvalidCredentialsException("Credenciales inválidas");
        }

        String token = jwtService.generateToken(user);
        return new AuthResponseDTO(token, user.getUsername(), user.getRole());
    }

    public AuthResponseDTO register(RegisterRequestDTO request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new UsernameAlreadyExistsException("El username ya está en uso: " + request.getUsername());
        }

        User user = new User(
                request.getUsername(),
                passwordEncoder.encode(request.getPassword()),
                request.getRole()
        );

        User savedUser = userRepository.save(user);
        String token = jwtService.generateToken(savedUser);
        return new AuthResponseDTO(token, savedUser.getUsername(), savedUser.getRole());
    }
}
