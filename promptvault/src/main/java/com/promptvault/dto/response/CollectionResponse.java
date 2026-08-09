package com.promptvault.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CollectionResponse {
    private UUID id;
    private String name;
    private String description;
    private long promptCount;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
