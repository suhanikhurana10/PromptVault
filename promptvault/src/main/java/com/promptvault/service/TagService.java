package com.promptvault.service;

import com.promptvault.dto.request.TagRequest;
import com.promptvault.dto.response.TagResponse;
import com.promptvault.entity.Tag;

import java.util.List;
import java.util.Set;
import java.util.UUID;

public interface TagService {

    TagResponse create(UUID ownerId, TagRequest request);

    void delete(UUID ownerId, UUID tagId);

    List<TagResponse> listAll(UUID ownerId);

    List<TagResponse> searchByName(UUID ownerId, String name);


    Set<Tag> resolveOwnedTags(UUID ownerId, Set<UUID> tagIds);
}
