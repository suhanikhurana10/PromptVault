package com.promptvault.dto.response;

import com.promptvault.enums.PromptStatus;
import com.promptvault.enums.Visibility;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PromptResponse {
    private UUID id;
    private String title;
    private String content;
    private String description;
    private PromptStatus status;
    private Visibility visibility;
    private boolean favorite;
    private boolean template;
    private int currentVersion;
    private UUID ownerId;
    private String ownerUsername;
    private UUID collectionId;
    private String collectionName;
    private Set<TagResponse> tags;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
