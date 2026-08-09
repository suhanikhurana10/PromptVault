package com.promptvault.mapper;

import com.promptvault.dto.response.TagResponse;
import com.promptvault.entity.Tag;
import org.mapstruct.Mapper;

import java.util.Set;

@Mapper(componentModel = "spring")
public interface TagMapper {
    TagResponse toResponse(Tag tag);
    Set<TagResponse> toResponseSet(Set<Tag> tags);
}
