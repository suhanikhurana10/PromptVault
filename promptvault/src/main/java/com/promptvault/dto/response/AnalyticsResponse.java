package com.promptvault.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AnalyticsResponse {
    private long totalPrompts;
    private long activePrompts;
    private long archivedPrompts;
    private long favoritePrompts;
    private long collectionCount;
    private long tagCount;
    private Map<String, Long> tagUsage;
    private List<PromptSummaryResponse> recentPrompts;
    private Map<String, Long> monthlyPromptCreation;
}
