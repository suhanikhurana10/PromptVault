package com.promptvault.controller;

import com.promptvault.dto.response.AnalyticsResponse;
import com.promptvault.dto.response.ApiResponse;
import com.promptvault.service.AnalyticsService;
import com.promptvault.util.SecurityUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/analytics")
@RequiredArgsConstructor
@Tag(name = "Analytics", description = "Usage statistics across prompts, collections and tags")
@SecurityRequirement(name = "bearerAuth")
public class AnalyticsController {

    private final AnalyticsService analyticsService;

    @GetMapping
    @Operation(
            summary = "Get analytics overview",
            description = "Returns total/archived/favorite prompt counts, collection count, tag usage, "
                    + "the 5 most recently created prompts, and prompt-creation counts for the last 6 months"
    )
    public ResponseEntity<ApiResponse<AnalyticsResponse>> getAnalytics() {
        AnalyticsResponse response = analyticsService.getAnalytics(SecurityUtils.getCurrentUserId());
        return ResponseEntity.ok(ApiResponse.success(response));
    }
}
