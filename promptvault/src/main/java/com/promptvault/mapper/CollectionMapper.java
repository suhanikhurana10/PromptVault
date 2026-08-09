package com.promptvault.mapper;

import com.promptvault.dto.response.CollectionResponse;
import com.promptvault.entity.Collection;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CollectionMapper {

    // promptCount is not a direct entity field; it's populated in the service
    // layer after mapping (requires a repository count query).
    @Mapping(target = "promptCount", ignore = true)
    CollectionResponse toResponse(Collection collection);
}
