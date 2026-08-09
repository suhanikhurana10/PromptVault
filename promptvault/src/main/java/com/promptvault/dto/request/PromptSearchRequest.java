package com.promptvault.dto.request;

import com.promptvault.enums.PromptStatus;
import com.promptvault.enums.Visibility;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PromptSearchRequest {
    private String title;
    private String content;
    private UUID collectionId;
    private UUID tagId;
    private Boolean favorite;
    private Boolean template;
    private Visibility visibility;
    private PromptStatus status;
    private LocalDateTime createdFrom;
    private LocalDateTime createdTo;
}
