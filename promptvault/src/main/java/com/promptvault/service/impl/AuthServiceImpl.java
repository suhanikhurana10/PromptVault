package com.promptvault.service.impl;

import com.promptvault.dto.request.LoginRequest;
import com.promptvault.dto.request.RegisterRequest;
import com.promptvault.dto.response.AuthResponse;
import com.promptvault.entity.User;
import com.promptvault.enums.Role;
import com.promptvault.exception.BadRequestException;
import com.promptvault.exception.DuplicateResourceException;
import com.promptvault.mapper.UserMapper;
import com.promptvault.repository.UserRepository;
import com.promptvault.security.JwtService;
import com.promptvault.security.UserPrincipal;
import com.promptvault.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final UserMapper userMapper;

    @Override
    @Transactional
    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateResourceException("An account with this email already exists");
        }
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new DuplicateResourceException("This username is already taken");
        }

        User user = User.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .fullName(request.getFullName())
                .role(Role.USER)
                .enabled(true)
                .build();

        User saved = userRepository.save(user);
        UserPrincipal principal = UserPrincipal.of(saved);
        String token = jwtService.generateToken(principal);

        return AuthResponse.builder()
                .accessToken(token)
                .expiresInMs(jwtService.getAccessTokenExpirationMs())
                .user(userMapper.toResponse(saved))
                .build();
    }

    @Override
    public AuthResponse login(LoginRequest request) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
            );
        } catch (BadCredentialsException e) {
            throw new BadRequestException("Invalid email or password");
        }

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new BadRequestException("Invalid email or password"));

        UserPrincipal principal = UserPrincipal.of(user);
        String token = jwtService.generateToken(principal);

        return AuthResponse.builder()
                .accessToken(token)
                .expiresInMs(jwtService.getAccessTokenExpirationMs())
                .user(userMapper.toResponse(user))
                .build();
    }
}
