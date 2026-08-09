package com.promptvault.repository;

import com.promptvault.entity.Collection;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CollectionRepository extends JpaRepository<Collection, UUID> {

    List<Collection> findAllByOwnerIdOrderByNameAsc(UUID ownerId);

    Optional<Collection> findByIdAndOwnerId(UUID id, UUID ownerId);

    boolean existsByNameAndOwnerId(String name, UUID ownerId);

    long countByOwnerId(UUID ownerId);
}
