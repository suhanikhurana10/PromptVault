package com.promptvault.controller;

import com.promptvault.dto.request.CollectionRequest;
import com.promptvault.dto.response.ApiResponse;
import com.promptvault.dto.response.CollectionResponse;
import com.promptvault.service.CollectionService;
import com.promptvault.util.SecurityUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/collections")
@RequiredArgsConstructor
@Tag(name = "Collections", description = "Organize prompts into named collections")
@SecurityRequirement(name = "bearerAuth")
public class CollectionController {

    private final CollectionService collectionService;

    @PostMapping
    @Operation(summary = "Create a collection")
    public ResponseEntity<ApiResponse<CollectionResponse>> create(@Valid @RequestBody CollectionRequest request) {
        CollectionResponse response = collectionService.create(SecurityUtils.getCurrentUserId(), request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success("Collection created", response));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Rename / update a collection")
    public ResponseEntity<ApiResponse<CollectionResponse>> rename(
            @PathVariable UUID id, @Valid @RequestBody CollectionRequest request) {
        CollectionResponse response = collectionService.rename(SecurityUtils.getCurrentUserId(), id, request);
        return ResponseEntity.ok(ApiResponse.success("Collection updated", response));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a collection (prompts inside become uncategorized)")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable UUID id) {
        collectionService.delete(SecurityUtils.getCurrentUserId(), id);
        return ResponseEntity.ok(ApiResponse.message("Collection deleted"));
    }

    @GetMapping
    @Operation(summary = "List all collections with prompt counts")
    public ResponseEntity<ApiResponse<List<CollectionResponse>>> listAll() {
        List<CollectionResponse> response = collectionService.listAll(SecurityUtils.getCurrentUserId());
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get a single collection with its prompt count")
    public ResponseEntity<ApiResponse<CollectionResponse>> getOne(@PathVariable UUID id) {
        CollectionResponse response = collectionService.getOne(SecurityUtils.getCurrentUserId(), id);
        return ResponseEntity.ok(ApiResponse.success(response));
    }
}
