package com.promptvault.service;

import com.promptvault.dto.request.ChangePasswordRequest;
import com.promptvault.dto.response.UserResponse;

import java.util.UUID;

public interface UserService {

    UserResponse getCurrentUser(UUID userId);

    void changePassword(UUID userId, ChangePasswordRequest request);
}
