package com.promptvault.service;

import com.promptvault.dto.request.CollectionRequest;
import com.promptvault.dto.response.CollectionResponse;

import java.util.List;
import java.util.UUID;

public interface CollectionService {

    CollectionResponse create(UUID ownerId, CollectionRequest request);

    CollectionResponse rename(UUID ownerId, UUID collectionId, CollectionRequest request);

    void delete(UUID ownerId, UUID collectionId);

    List<CollectionResponse> listAll(UUID ownerId);

    CollectionResponse getOne(UUID ownerId, UUID collectionId);
}
