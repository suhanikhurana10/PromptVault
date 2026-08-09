package com.promptvault.repository;

import com.promptvault.entity.Prompt;
import com.promptvault.enums.PromptStatus;
import com.promptvault.enums.Visibility;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;


public interface PromptRepository extends JpaRepository<Prompt, UUID>, JpaSpecificationExecutor<Prompt> {

    Optional<Prompt> findByIdAndOwnerId(UUID id, UUID ownerId);

    Page<Prompt> findAllByOwnerIdAndStatus(UUID ownerId, PromptStatus status, Pageable pageable);

    long countByOwnerIdAndStatus(UUID ownerId, PromptStatus status);

    long countByOwnerIdAndFavoriteTrue(UUID ownerId);

    List<Prompt> findTop5ByOwnerIdAndStatusOrderByCreatedAtDesc(UUID ownerId, PromptStatus status);

    long countByOwnerIdAndCreatedAtBetween(UUID ownerId, LocalDateTime start, LocalDateTime end);

    long countByCollectionId(UUID collectionId);

    Page<Prompt> findAllByVisibilityAndStatus(Visibility visibility, PromptStatus status, Pageable pageable);
}
