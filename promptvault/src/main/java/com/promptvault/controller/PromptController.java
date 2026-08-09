package com.promptvault.controller;

import com.promptvault.dto.request.PromptCreateRequest;
import com.promptvault.dto.request.PromptSearchRequest;
import com.promptvault.dto.request.PromptUpdateRequest;
import com.promptvault.dto.response.ApiResponse;
import com.promptvault.dto.response.PageResponse;
import com.promptvault.dto.response.PromptResponse;
import com.promptvault.dto.response.PromptSummaryResponse;
import com.promptvault.dto.response.PromptVersionResponse;
import com.promptvault.enums.PromptStatus;
import com.promptvault.enums.Visibility;
import com.promptvault.service.PromptService;
import com.promptvault.util.AppConstants;
import com.promptvault.util.SecurityUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/prompts")
@RequiredArgsConstructor
@Tag(name = "Prompts", description = "CRUD, search, versioning and lifecycle management for prompts")
@SecurityRequirement(name = "bearerAuth")
public class PromptController {

    private final PromptService promptService;

    @PostMapping
    @Operation(summary = "Create a new prompt")
    public ResponseEntity<ApiResponse<PromptResponse>> create(@Valid @RequestBody PromptCreateRequest request) {
        PromptResponse response = promptService.create(SecurityUtils.getCurrentUserId(), request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success("Prompt created", response));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update a prompt (automatically stores the previous version)")
    public ResponseEntity<ApiResponse<PromptResponse>> update(
            @PathVariable UUID id, @Valid @RequestBody PromptUpdateRequest request) {
        PromptResponse response = promptService.update(SecurityUtils.getCurrentUserId(), id, request);
        return ResponseEntity.ok(ApiResponse.success("Prompt updated", response));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get a single prompt by id")
    public ResponseEntity<ApiResponse<PromptResponse>> getOne(@PathVariable UUID id) {
        PromptResponse response = promptService.getOne(SecurityUtils.getCurrentUserId(), id);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping
    @Operation(
            summary = "Search and list prompts",
            description = "Supports filtering by title, content, collection, tag, favorite status, visibility, "
                    + "template flag, status, and a creation-date range, with pagination and sorting"
    )
    public ResponseEntity<ApiResponse<PageResponse<PromptSummaryResponse>>> search(
            @RequestParam(required = false) String title,
            @RequestParam(required = false) String content,
            @RequestParam(required = false) UUID collectionId,
            @RequestParam(required = false) UUID tagId,
            @RequestParam(required = false) Boolean favorite,
            @RequestParam(required = false) Boolean template,
            @RequestParam(required = false) Visibility visibility,
            @RequestParam(required = false, defaultValue = "ACTIVE") PromptStatus status,
            @RequestParam(required = false) LocalDateTime createdFrom,
            @RequestParam(required = false) LocalDateTime createdTo,
            @RequestParam(defaultValue = AppConstants.DEFAULT_PAGE_NUMBER) int page,
            @RequestParam(defaultValue = AppConstants.DEFAULT_PAGE_SIZE) int size,
            @RequestParam(defaultValue = AppConstants.DEFAULT_SORT_BY) String sortBy,
            @RequestParam(defaultValue = AppConstants.DEFAULT_SORT_DIRECTION) String sortDirection
    ) {
        int safeSize = Math.min(size, AppConstants.MAX_PAGE_SIZE);
        Sort.Direction direction = "ASC".equalsIgnoreCase(sortDirection) ? Sort.Direction.ASC : Sort.Direction.DESC;
        Pageable pageable = PageRequest.of(page, safeSize, Sort.by(direction, sortBy));

        PromptSearchRequest criteria = PromptSearchRequest.builder()
                .title(title).content(content).collectionId(collectionId).tagId(tagId)
                .favorite(favorite).template(template).visibility(visibility).status(status)
                .createdFrom(createdFrom).createdTo(createdTo)
                .build();

        Page<PromptSummaryResponse> results = promptService.search(SecurityUtils.getCurrentUserId(), criteria, pageable);
        return ResponseEntity.ok(ApiResponse.success(PageResponse.from(results)));
    }

    @PatchMapping("/{id}/archive")
    @Operation(summary = "Archive a prompt (soft delete)")
    public ResponseEntity<ApiResponse<Void>> archive(@PathVariable UUID id) {
        promptService.archive(SecurityUtils.getCurrentUserId(), id);
        return ResponseEntity.ok(ApiResponse.message("Prompt archived"));
    }

    @PatchMapping("/{id}/restore")
    @Operation(summary = "Restore an archived prompt")
    public ResponseEntity<ApiResponse<PromptResponse>> restore(@PathVariable UUID id) {
        PromptResponse response = promptService.restore(SecurityUtils.getCurrentUserId(), id);
        return ResponseEntity.ok(ApiResponse.success("Prompt restored", response));
    }

    @PostMapping("/{id}/duplicate")
    @Operation(summary = "Duplicate a prompt")
    public ResponseEntity<ApiResponse<PromptResponse>> duplicate(@PathVariable UUID id) {
        PromptResponse response = promptService.duplicate(SecurityUtils.getCurrentUserId(), id);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success("Prompt duplicated", response));
    }

    @PatchMapping("/{id}/favorite")
    @Operation(summary = "Mark a prompt as favorite")
    public ResponseEntity<ApiResponse<PromptResponse>> markFavorite(@PathVariable UUID id) {
        PromptResponse response = promptService.setFavorite(SecurityUtils.getCurrentUserId(), id, true);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PatchMapping("/{id}/unfavorite")
    @Operation(summary = "Unmark a prompt as favorite")
    public ResponseEntity<ApiResponse<PromptResponse>> unmarkFavorite(@PathVariable UUID id) {
        PromptResponse response = promptService.setFavorite(SecurityUtils.getCurrentUserId(), id, false);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PatchMapping("/{id}/move")
    @Operation(summary = "Move a prompt to a different collection (pass null collectionId to uncategorize)")
    public ResponseEntity<ApiResponse<PromptResponse>> move(
            @PathVariable UUID id, @RequestParam(required = false) UUID collectionId) {
        PromptResponse response = promptService.moveToCollection(SecurityUtils.getCurrentUserId(), id, collectionId);
        return ResponseEntity.ok(ApiResponse.success("Prompt moved", response));
    }

    @GetMapping("/{id}/versions")
    @Operation(summary = "Get the full version history of a prompt")
    public ResponseEntity<ApiResponse<List<PromptVersionResponse>>> versionHistory(@PathVariable UUID id) {
        List<PromptVersionResponse> versions = promptService.getVersionHistory(SecurityUtils.getCurrentUserId(), id);
        return ResponseEntity.ok(ApiResponse.success(versions));
    }

    @PostMapping("/{id}/versions/{versionNumber}/restore")
    @Operation(summary = "Restore a prompt to a previous version (creates a new version snapshot)")
    public ResponseEntity<ApiResponse<PromptResponse>> restoreVersion(
            @PathVariable UUID id, @PathVariable int versionNumber) {
        PromptResponse response = promptService.restoreVersion(SecurityUtils.getCurrentUserId(), id, versionNumber);
        return ResponseEntity.ok(ApiResponse.success("Prompt restored to version " + versionNumber, response));
    }

    @GetMapping("/public")
    @Operation(
            summary = "Browse public prompts",
            description = "Lists prompts marked PUBLIC by any user. Does not require authentication."
    )
    public ResponseEntity<ApiResponse<PageResponse<PromptSummaryResponse>>> listPublic(
            @RequestParam(defaultValue = AppConstants.DEFAULT_PAGE_NUMBER) int page,
            @RequestParam(defaultValue = AppConstants.DEFAULT_PAGE_SIZE) int size
    ) {
        int safeSize = Math.min(size, AppConstants.MAX_PAGE_SIZE);
        Pageable pageable = PageRequest.of(page, safeSize, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<PromptSummaryResponse> results = promptService.listPublicPrompts(pageable);
        return ResponseEntity.ok(ApiResponse.success(PageResponse.from(results)));
    }

    @PostMapping("/{id}/render")
    @Operation(summary = "Render a template prompt by substituting {{variable}} placeholders")
    public ResponseEntity<ApiResponse<String>> render(
            @PathVariable UUID id, @RequestBody Map<String, String> variables) {
        String rendered = promptService.renderTemplate(SecurityUtils.getCurrentUserId(), id, variables);
        return ResponseEntity.ok(ApiResponse.success(rendered));
    }
}
