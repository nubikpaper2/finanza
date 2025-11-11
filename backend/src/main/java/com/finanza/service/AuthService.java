package com.finanza.service;

import com.finanza.dto.AuthResponse;
import com.finanza.dto.LoginRequest;
import com.finanza.dto.RegisterRequest;
import com.finanza.model.Organization;
import com.finanza.model.User;
import com.finanza.repository.OrganizationRepository;
import com.finanza.repository.UserRepository;
import com.finanza.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final OrganizationRepository organizationRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        // Verificar si el usuario ya existe
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("El email ya está registrado");
        }

        // Crear o buscar organización
        Organization organization = null;
        if (request.getOrganizationName() != null && !request.getOrganizationName().isEmpty()) {
            organization = organizationRepository.findByName(request.getOrganizationName())
                    .orElseGet(() -> {
                        Organization newOrg = new Organization();
                        newOrg.setName(request.getOrganizationName());
                        newOrg.setDescription("Organización creada durante el registro");
                        return organizationRepository.save(newOrg);
                    });
        }

        // Crear usuario
        User user = new User();
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setPhone(request.getPhone());
        user.setOrganization(organization);
        user.addRole("USER");

        user = userRepository.save(user);

        // Generar token
        String token = jwtUtil.generateToken(user.getEmail(), null);

        // Crear respuesta
        AuthResponse response = AuthResponse.builder()
                .token(token)
                .type("Bearer")
                .id(user.getId())
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .roles(user.getRoles())
                .build();

        if (organization != null) {
            response.setOrganizationId(organization.getId());
            response.setOrganizationName(organization.getName());
        }

        return response;
    }

    public AuthResponse login(LoginRequest request) {
        // Autenticar
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );

        // Buscar usuario
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        // Generar token
        String token = jwtUtil.generateToken(user.getEmail(), null);

        // Crear respuesta
        AuthResponse response = AuthResponse.builder()
                .token(token)
                .type("Bearer")
                .id(user.getId())
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .roles(user.getRoles())
                .build();

        if (user.getOrganization() != null) {
            response.setOrganizationId(user.getOrganization().getId());
            response.setOrganizationName(user.getOrganization().getName());
        }

        return response;
    }
}
