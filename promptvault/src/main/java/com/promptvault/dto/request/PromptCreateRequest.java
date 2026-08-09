package com.promptvault.dto.request;

import com.promptvault.enums.Visibility;
import io.swagger.v3.oas.annotations.media.Schema;
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
public class PromptCreateRequest {

    @NotBlank(message = "Title is required")
    @Size(max = 200)
    @Schema(example = "Cold Outreach Email")
    private String title;

    @NotBlank(message = "Content is required")
    @Schema(example = "Write a cold outreach email for {{role}} at {{company}} about {{technology}}.")
    private String content;

    @Size(max = 500)
    private String description;

    private UUID collectionId;

    private Set<UUID> tagIds;

    @Builder.Default
    private Visibility visibility = Visibility.PRIVATE;

    @Builder.Default
    private boolean template = false;
}
