package com.promptvault.service.impl;

import com.promptvault.dto.request.CollectionRequest;
import com.promptvault.dto.response.CollectionResponse;
import com.promptvault.entity.Collection;
import com.promptvault.entity.User;
import com.promptvault.exception.DuplicateResourceException;
import com.promptvault.exception.ResourceNotFoundException;
import com.promptvault.mapper.CollectionMapper;
import com.promptvault.repository.CollectionRepository;
import com.promptvault.repository.PromptRepository;
import com.promptvault.repository.UserRepository;
import com.promptvault.service.CollectionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CollectionServiceImpl implements CollectionService {

    private final CollectionRepository collectionRepository;
    private final PromptRepository promptRepository;
    private final UserRepository userRepository;
    private final CollectionMapper collectionMapper;

    @Override
    @Transactional
    public CollectionResponse create(UUID ownerId, CollectionRequest request) {
        if (collectionRepository.existsByNameAndOwnerId(request.getName(), ownerId)) {
            throw new DuplicateResourceException("A collection named '" + request.getName() + "' already exists");
        }

        User owner = userRepository.getReferenceById(ownerId);
        Collection collection = Collection.builder()
                .name(request.getName())
                .description(request.getDescription())
                .owner(owner)
                .build();

        Collection saved = collectionRepository.save(collection);
        return toResponseWithCount(saved);
    }

    @Override
    @Transactional
    public CollectionResponse rename(UUID ownerId, UUID collectionId, CollectionRequest request) {
        Collection collection = findOwned(ownerId, collectionId);

        if (!collection.getName().equalsIgnoreCase(request.getName())
                && collectionRepository.existsByNameAndOwnerId(request.getName(), ownerId)) {
            throw new DuplicateResourceException("A collection named '" + request.getName() + "' already exists");
        }

        collection.setName(request.getName());
        collection.setDescription(request.getDescription());
        return toResponseWithCount(collection);
    }

    @Override
    @Transactional
    public void delete(UUID ownerId, UUID collectionId) {
        Collection collection = findOwned(ownerId, collectionId);
        // Prompts in this collection are not deleted; they simply become uncategorized
        // (Prompt.collection is nullable, so the FK is cleared automatically by the
        // database's ON DELETE behavior configured via Hibernate's default here,
        // since we don't cascade delete from Collection to Prompt).
        collectionRepository.delete(collection);
    }

    @Override
    public List<CollectionResponse> listAll(UUID ownerId) {
        return collectionRepository.findAllByOwnerIdOrderByNameAsc(ownerId).stream()
                .map(this::toResponseWithCount)
                .toList();
    }

    @Override
    public CollectionResponse getOne(UUID ownerId, UUID collectionId) {
        return toResponseWithCount(findOwned(ownerId, collectionId));
    }

    private Collection findOwned(UUID ownerId, UUID collectionId) {
        return collectionRepository.findByIdAndOwnerId(collectionId, ownerId)
                .orElseThrow(() -> ResourceNotFoundException.of("Collection", collectionId));
    }

    private CollectionResponse toResponseWithCount(Collection collection) {
        CollectionResponse response = collectionMapper.toResponse(collection);
        response.setPromptCount(promptRepository.countByCollectionId(collection.getId()));
        return response;
    }
}
