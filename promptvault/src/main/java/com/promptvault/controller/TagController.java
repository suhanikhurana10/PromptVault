package com.promptvault.controller;

import com.promptvault.dto.request.TagRequest;
import com.promptvault.dto.response.ApiResponse;
import com.promptvault.dto.response.TagResponse;
import com.promptvault.service.TagService;
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
@RequestMapping("/api/v1/tags")
@RequiredArgsConstructor
@Tag(name = "Tags", description = "Label prompts with reusable tags")
@SecurityRequirement(name = "bearerAuth")
public class TagController {

    private final TagService tagService;

    @PostMapping
    @Operation(summary = "Create a tag")
    public ResponseEntity<ApiResponse<TagResponse>> create(@Valid @RequestBody TagRequest request) {
        TagResponse response = tagService.create(SecurityUtils.getCurrentUserId(), request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success("Tag created", response));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a tag (detaches it from any prompts)")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable UUID id) {
        tagService.delete(SecurityUtils.getCurrentUserId(), id);
        return ResponseEntity.ok(ApiResponse.message("Tag deleted"));
    }

    @GetMapping
    @Operation(summary = "List all tags")
    public ResponseEntity<ApiResponse<List<TagResponse>>> listAll() {
        return ResponseEntity.ok(ApiResponse.success(tagService.listAll(SecurityUtils.getCurrentUserId())));
    }

    @GetMapping("/search")
    @Operation(summary = "Search tags by name")
    public ResponseEntity<ApiResponse<List<TagResponse>>> search(@RequestParam String name) {
        return ResponseEntity.ok(ApiResponse.success(tagService.searchByName(SecurityUtils.getCurrentUserId(), name)));
    }
}
