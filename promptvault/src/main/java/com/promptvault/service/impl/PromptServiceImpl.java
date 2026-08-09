package com.promptvault.service.impl;

import com.promptvault.dto.request.PromptCreateRequest;
import com.promptvault.dto.request.PromptSearchRequest;
import com.promptvault.dto.request.PromptUpdateRequest;
import com.promptvault.dto.response.PromptResponse;
import com.promptvault.dto.response.PromptSummaryResponse;
import com.promptvault.dto.response.PromptVersionResponse;
import com.promptvault.entity.Collection;
import com.promptvault.entity.Prompt;
import com.promptvault.entity.PromptVersion;
import com.promptvault.entity.Tag;
import com.promptvault.entity.User;
import com.promptvault.enums.PromptStatus;
import com.promptvault.exception.BadRequestException;
import com.promptvault.exception.ResourceNotFoundException;
import com.promptvault.mapper.PromptMapper;
import com.promptvault.mapper.PromptVersionMapper;
import com.promptvault.repository.CollectionRepository;
import com.promptvault.repository.PromptRepository;
import com.promptvault.repository.PromptVersionRepository;
import com.promptvault.repository.UserRepository;
import com.promptvault.service.PromptService;
import com.promptvault.service.TagService;
import com.promptvault.specification.PromptSpecifications;
import com.promptvault.util.TemplateUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PromptServiceImpl implements PromptService {

    private final PromptRepository promptRepository;
    private final PromptVersionRepository promptVersionRepository;
    private final CollectionRepository collectionRepository;
    private final UserRepository userRepository;
    private final TagService tagService;
    private final PromptMapper promptMapper;
    private final PromptVersionMapper promptVersionMapper;

    @Override
    @Transactional
    public PromptResponse create(UUID ownerId, PromptCreateRequest request) {
        User owner = userRepository.getReferenceById(ownerId);
        Collection collection = resolveCollection(ownerId, request.getCollectionId());
        Set<Tag> tags = tagService.resolveOwnedTags(ownerId, request.getTagIds());

        Prompt prompt = Prompt.builder()
                .title(request.getTitle())
                .content(request.getContent())
                .description(request.getDescription())
                .visibility(request.getVisibility())
                .template(request.isTemplate())
                .owner(owner)
                .collection(collection)
                .tags(tags)
                .currentVersion(1)
                .build();

        Prompt saved = promptRepository.save(prompt);
        saveVersionSnapshot(saved, "Initial version");

        return promptMapper.toResponse(saved);
    }

    @Override
    @Transactional
    public PromptResponse update(UUID ownerId, UUID promptId, PromptUpdateRequest request) {
        Prompt prompt = findOwned(ownerId, promptId);

        prompt.setTitle(request.getTitle());
        prompt.setContent(request.getContent());
        prompt.setDescription(request.getDescription());
        prompt.setTemplate(request.isTemplate());
        if (request.getVisibility() != null) {
            prompt.setVisibility(request.getVisibility());
        }
        if (request.getCollectionId() != null) {
            prompt.setCollection(resolveCollection(ownerId, request.getCollectionId()));
        }
        if (request.getTagIds() != null) {
            prompt.setTags(tagService.resolveOwnedTags(ownerId, request.getTagIds()));
        }

        // Every edit creates a new version snapshot of the *new* state, and bumps currentVersion.
        prompt.setCurrentVersion(prompt.getCurrentVersion() + 1);
        Prompt saved = promptRepository.save(prompt);
        saveVersionSnapshot(saved, request.getChangeNote());

        return promptMapper.toResponse(saved);
    }

    @Override
    public PromptResponse getOne(UUID ownerId, UUID promptId) {
        return promptMapper.toResponse(findOwned(ownerId, promptId));
    }

    @Override
    public Page<PromptSummaryResponse> search(UUID ownerId, PromptSearchRequest c, Pageable pageable) {
        Specification<Prompt> spec = Specification
                .where(PromptSpecifications.ownedBy(ownerId))
                .and(PromptSpecifications.titleContains(c.getTitle()))
                .and(PromptSpecifications.contentContains(c.getContent()))
                .and(PromptSpecifications.inCollection(c.getCollectionId()))
                .and(PromptSpecifications.hasTag(c.getTagId()))
                .and(PromptSpecifications.isFavorite(c.getFavorite()))
                .and(PromptSpecifications.isTemplate(c.getTemplate()))
                .and(PromptSpecifications.hasVisibility(c.getVisibility()))
                .and(PromptSpecifications.hasStatus(c.getStatus() != null ? c.getStatus() : PromptStatus.ACTIVE))
                .and(PromptSpecifications.createdAfter(c.getCreatedFrom()))
                .and(PromptSpecifications.createdBefore(c.getCreatedTo()));

        return promptRepository.findAll(spec, pageable).map(promptMapper::toSummary);
    }

    @Override
    @Transactional
    public void archive(UUID ownerId, UUID promptId) {
        Prompt prompt = findOwned(ownerId, promptId);
        prompt.setStatus(PromptStatus.ARCHIVED);
        promptRepository.save(prompt);
    }

    @Override
    @Transactional
    public PromptResponse restore(UUID ownerId, UUID promptId) {
        Prompt prompt = findOwned(ownerId, promptId);
        prompt.setStatus(PromptStatus.ACTIVE);
        return promptMapper.toResponse(promptRepository.save(prompt));
    }

    @Override
    @Transactional
    public PromptResponse duplicate(UUID ownerId, UUID promptId) {
        Prompt original = findOwned(ownerId, promptId);

        Prompt copy = Prompt.builder()
                .title(original.getTitle() + " (Copy)")
                .content(original.getContent())
                .description(original.getDescription())
                .status(PromptStatus.ACTIVE)
                .visibility(original.getVisibility())
                .template(original.isTemplate())
                .favorite(false)
                .owner(original.getOwner())
                .collection(original.getCollection())
                .tags(new HashSet<>(original.getTags()))
                .currentVersion(1)
                .build();

        Prompt saved = promptRepository.save(copy);
        saveVersionSnapshot(saved, "Duplicated from '" + original.getTitle() + "'");

        return promptMapper.toResponse(saved);
    }

    @Override
    @Transactional
    public PromptResponse setFavorite(UUID ownerId, UUID promptId, boolean favorite) {
        Prompt prompt = findOwned(ownerId, promptId);
        prompt.setFavorite(favorite);
        return promptMapper.toResponse(promptRepository.save(prompt));
    }

    @Override
    @Transactional
    public PromptResponse moveToCollection(UUID ownerId, UUID promptId, UUID collectionId) {
        Prompt prompt = findOwned(ownerId, promptId);
        prompt.setCollection(resolveCollection(ownerId, collectionId));
        return promptMapper.toResponse(promptRepository.save(prompt));
    }

    @Override
    public List<PromptVersionResponse> getVersionHistory(UUID ownerId, UUID promptId) {
        findOwned(ownerId, promptId); // ownership check
        return promptVersionMapper.toResponseList(
                promptVersionRepository.findAllByPromptIdOrderByVersionNumberDesc(promptId));
    }

    @Override
    @Transactional
    public PromptResponse restoreVersion(UUID ownerId, UUID promptId, int versionNumber) {
        Prompt prompt = findOwned(ownerId, promptId);
        PromptVersion version = promptVersionRepository.findByPromptIdAndVersionNumber(promptId, versionNumber)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Version " + versionNumber + " not found for this prompt"));

        prompt.setTitle(version.getTitle());
        prompt.setContent(version.getContent());
        prompt.setCurrentVersion(prompt.getCurrentVersion() + 1);

        Prompt saved = promptRepository.save(prompt);
        saveVersionSnapshot(saved, "Restored from version " + versionNumber);

        return promptMapper.toResponse(saved);
    }

    @Override
    public String renderTemplate(UUID ownerId, UUID promptId, Map<String, String> variableValues) {
        Prompt prompt = findOwned(ownerId, promptId);
        if (!prompt.isTemplate()) {
            throw new BadRequestException("This prompt is not marked as a template");
        }
        return TemplateUtil.render(prompt.getContent(), variableValues);
    }

    @Override
    public Page<PromptSummaryResponse> listPublicPrompts(Pageable pageable) {
        return promptRepository
                .findAllByVisibilityAndStatus(com.promptvault.enums.Visibility.PUBLIC, PromptStatus.ACTIVE, pageable)
                .map(promptMapper::toSummary);
    }

    // ---- helpers ----

    private Prompt findOwned(UUID ownerId, UUID promptId) {
        return promptRepository.findByIdAndOwnerId(promptId, ownerId)
                .orElseThrow(() -> ResourceNotFoundException.of("Prompt", promptId));
    }

    private Collection resolveCollection(UUID ownerId, UUID collectionId) {
        if (collectionId == null) {
            return null;
        }
        return collectionRepository.findByIdAndOwnerId(collectionId, ownerId)
                .orElseThrow(() -> ResourceNotFoundException.of("Collection", collectionId));
    }

    private void saveVersionSnapshot(Prompt prompt, String changeNote) {
        PromptVersion version = PromptVersion.builder()
                .prompt(prompt)
                .versionNumber(prompt.getCurrentVersion())
                .title(prompt.getTitle())
                .content(prompt.getContent())
                .changeNote(changeNote)
                .build();
        promptVersionRepository.save(version);
    }
}
