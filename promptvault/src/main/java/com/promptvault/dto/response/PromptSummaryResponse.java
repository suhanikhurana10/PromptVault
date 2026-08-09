package com.promptvault.dto.response;

import com.promptvault.enums.PromptStatus;
import com.promptvault.enums.Visibility;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PromptSummaryResponse {
    private UUID id;
    private String title;
    private String description;
    private PromptStatus status;
    private Visibility visibility;
    private boolean favorite;
    private String collectionName;
    private LocalDateTime createdAt;
}
