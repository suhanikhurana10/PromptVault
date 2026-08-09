package com.promptvault.dto.request;

import com.promptvault.enums.Visibility;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Set;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PromptUpdateRequest {

    @NotBlank(message = "Title is required")
    @Size(max = 200)
    private String title;

    @NotBlank(message = "Content is required")
    private String content;

    @Size(max = 500)
    private String description;

    private UUID collectionId;

    private Set<UUID> tagIds;

    private Visibility visibility;

    private boolean template;

    /** Optional short note describing what changed, stored with the new version. */
    @Size(max = 300)
    private String changeNote;
}
