package com.promptvault.service;

import com.promptvault.dto.request.PromptCreateRequest;
import com.promptvault.dto.request.PromptSearchRequest;
import com.promptvault.dto.request.PromptUpdateRequest;
import com.promptvault.dto.response.PromptResponse;
import com.promptvault.dto.response.PromptSummaryResponse;
import com.promptvault.dto.response.PromptVersionResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface PromptService {

    PromptResponse create(UUID ownerId, PromptCreateRequest request);

    PromptResponse update(UUID ownerId, UUID promptId, PromptUpdateRequest request);

    PromptResponse getOne(UUID ownerId, UUID promptId);

    Page<PromptSummaryResponse> search(UUID ownerId, PromptSearchRequest criteria, Pageable pageable);

    /** Archives a prompt (soft delete) instead of permanently removing it. */
    void archive(UUID ownerId, UUID promptId);

    PromptResponse restore(UUID ownerId, UUID promptId);

    PromptResponse duplicate(UUID ownerId, UUID promptId);

    PromptResponse setFavorite(UUID ownerId, UUID promptId, boolean favorite);

    PromptResponse moveToCollection(UUID ownerId, UUID promptId, UUID collectionId);

    List<PromptVersionResponse> getVersionHistory(UUID ownerId, UUID promptId);

    PromptResponse restoreVersion(UUID ownerId, UUID promptId, int versionNumber);

    /** Renders a template prompt's content by substituting {{variable}} placeholders with supplied values. */
    String renderTemplate(UUID ownerId, UUID promptId, Map<String, String> variableValues);

    /** Publicly browsable prompts (visibility = PUBLIC, status = ACTIVE) — no ownership/auth required. */
    Page<PromptSummaryResponse> listPublicPrompts(Pageable pageable);
}
