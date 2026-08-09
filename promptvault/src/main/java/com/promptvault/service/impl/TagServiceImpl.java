package com.promptvault.service.impl;

import com.promptvault.dto.request.TagRequest;
import com.promptvault.dto.response.TagResponse;
import com.promptvault.entity.Tag;
import com.promptvault.entity.User;
import com.promptvault.exception.DuplicateResourceException;
import com.promptvault.exception.ResourceNotFoundException;
import com.promptvault.mapper.TagMapper;
import com.promptvault.repository.TagRepository;
import com.promptvault.repository.UserRepository;
import com.promptvault.service.TagService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TagServiceImpl implements TagService {

    private final TagRepository tagRepository;
    private final UserRepository userRepository;
    private final TagMapper tagMapper;

    @Override
    @Transactional
    public TagResponse create(UUID ownerId, TagRequest request) {
        String normalized = request.getName().trim();
        if (tagRepository.existsByNameIgnoreCaseAndOwnerId(normalized, ownerId)) {
            throw new DuplicateResourceException("A tag named '" + normalized + "' already exists");
        }

        User owner = userRepository.getReferenceById(ownerId);
        Tag tag = Tag.builder().name(normalized).owner(owner).build();
        return tagMapper.toResponse(tagRepository.save(tag));
    }

    @Override
    @Transactional
    public void delete(UUID ownerId, UUID tagId) {
        Tag tag = tagRepository.findByIdAndOwnerId(tagId, ownerId)
                .orElseThrow(() -> ResourceNotFoundException.of("Tag", tagId));
        // Removing the tag automatically detaches it from any prompts via the join table.
        tagRepository.delete(tag);
    }

    @Override
    public List<TagResponse> listAll(UUID ownerId) {
        return tagRepository.findAllByOwnerIdOrderByNameAsc(ownerId).stream()
                .map(tagMapper::toResponse)
                .toList();
    }

    @Override
    public List<TagResponse> searchByName(UUID ownerId, String name) {
        return tagRepository.findAllByOwnerIdOrderByNameAsc(ownerId).stream()
                .filter(t -> t.getName().toLowerCase().contains(name.toLowerCase()))
                .map(tagMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional
    public Set<Tag> resolveOwnedTags(UUID ownerId, Set<UUID> tagIds) {
        if (tagIds == null || tagIds.isEmpty()) {
            return new HashSet<>();
        }
        Set<Tag> tags = new HashSet<>();
        for (UUID tagId : tagIds) {
            Tag tag = tagRepository.findByIdAndOwnerId(tagId, ownerId)
                    .orElseThrow(() -> ResourceNotFoundException.of("Tag", tagId));
            tags.add(tag);
        }
        return tags;
    }
}
