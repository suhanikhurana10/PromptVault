package com.promptvault.repository;

import com.promptvault.entity.Tag;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TagRepository extends JpaRepository<Tag, UUID> {

    List<Tag> findAllByOwnerIdOrderByNameAsc(UUID ownerId);

    Optional<Tag> findByIdAndOwnerId(UUID id, UUID ownerId);

    Optional<Tag> findByNameIgnoreCaseAndOwnerId(String name, UUID ownerId);

    boolean existsByNameIgnoreCaseAndOwnerId(String name, UUID ownerId);
}
