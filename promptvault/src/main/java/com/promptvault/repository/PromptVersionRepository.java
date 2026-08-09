package com.promptvault.repository;

import com.promptvault.entity.PromptVersion;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PromptVersionRepository extends JpaRepository<PromptVersion, UUID> {

    List<PromptVersion> findAllByPromptIdOrderByVersionNumberDesc(UUID promptId);

    Optional<PromptVersion> findByPromptIdAndVersionNumber(UUID promptId, int versionNumber);

    Optional<PromptVersion> findTopByPromptIdOrderByVersionNumberDesc(UUID promptId);
}
