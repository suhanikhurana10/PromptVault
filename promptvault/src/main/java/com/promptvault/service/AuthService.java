package com.promptvault.service;

import com.promptvault.dto.request.LoginRequest;
import com.promptvault.dto.request.RegisterRequest;
import com.promptvault.dto.response.AuthResponse;

public interface AuthService {

    AuthResponse register(RegisterRequest request);

    AuthResponse login(LoginRequest request);
}
