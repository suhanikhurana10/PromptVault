package com.promptvault.dto.response;

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
public class PromptVersionResponse {
    private UUID id;
    private int versionNumber;
    private String title;
    private String content;
    private String changeNote;
    private LocalDateTime createdAt;
}
