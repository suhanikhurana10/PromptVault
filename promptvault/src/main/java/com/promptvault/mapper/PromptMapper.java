package com.promptvault.mapper;

import com.promptvault.dto.response.PromptResponse;
import com.promptvault.dto.response.PromptSummaryResponse;
import com.promptvault.entity.Prompt;
import org.mapstruct.Mapping;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", uses = {TagMapper.class})
public interface PromptMapper {

    @Mapping(target = "ownerId", source = "owner.id")
    @Mapping(target = "ownerUsername", source = "owner.username")
    @Mapping(target = "collectionId", source = "collection.id")
    @Mapping(target = "collectionName", source = "collection.name")
    PromptResponse toResponse(Prompt prompt);

    @Mapping(target = "collectionName", source = "collection.name")
    PromptSummaryResponse toSummary(Prompt prompt);
}
