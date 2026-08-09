package com.promptvault.mapper;

import com.promptvault.dto.response.PromptVersionResponse;
import com.promptvault.entity.PromptVersion;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface PromptVersionMapper {
    PromptVersionResponse toResponse(PromptVersion version);
    List<PromptVersionResponse> toResponseList(List<PromptVersion> versions);
}
