package com.promptvault.mapper;

import com.promptvault.dto.response.UserResponse;
import com.promptvault.entity.User;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {
    UserResponse toResponse(User user);
}
