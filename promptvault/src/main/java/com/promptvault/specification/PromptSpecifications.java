package com.promptvault.specification;

import com.promptvault.entity.Prompt;
import com.promptvault.entity.Tag;
import com.promptvault.enums.PromptStatus;
import com.promptvault.enums.Visibility;
import jakarta.persistence.criteria.Join;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;
import java.util.UUID;


public final class PromptSpecifications {

    private PromptSpecifications() {
    }

    public static Specification<Prompt> ownedBy(UUID ownerId) {
        if (ownerId == null) return null;
        return (root, query, cb) -> cb.equal(root.get("owner").get("id"), ownerId);
    }

    public static Specification<Prompt> hasStatus(PromptStatus status) {
        if (status == null) return null;
        return (root, query, cb) -> cb.equal(root.get("status"), status);
    }

    public static Specification<Prompt> titleContains(String title) {
        if (title == null || title.isBlank()) return null;
        return (root, query, cb) -> cb.like(cb.lower(root.get("title")), "%" + title.toLowerCase() + "%");
    }

    public static Specification<Prompt> contentContains(String content) {
        if (content == null || content.isBlank()) return null;
        return (root, query, cb) -> cb.like(cb.lower(root.get("content")), "%" + content.toLowerCase() + "%");
    }

    public static Specification<Prompt> inCollection(UUID collectionId) {
        if (collectionId == null) return null;
        return (root, query, cb) -> cb.equal(root.get("collection").get("id"), collectionId);
    }

    public static Specification<Prompt> hasTag(UUID tagId) {
        if (tagId == null) return null;
        return (root, query, cb) -> {
            query.distinct(true);
            Join<Prompt, Tag> tags = root.join("tags");
            return cb.equal(tags.get("id"), tagId);
        };
    }

    public static Specification<Prompt> isFavorite(Boolean favorite) {
        if (favorite == null) return null;
        return (root, query, cb) -> cb.equal(root.get("favorite"), favorite);
    }

    public static Specification<Prompt> isTemplate(Boolean template) {
        if (template == null) return null;
        return (root, query, cb) -> cb.equal(root.get("template"), template);
    }

    public static Specification<Prompt> hasVisibility(Visibility visibility) {
        if (visibility == null) return null;
        return (root, query, cb) -> cb.equal(root.get("visibility"), visibility);
    }

    public static Specification<Prompt> createdAfter(LocalDateTime from) {
        if (from == null) return null;
        return (root, query, cb) -> cb.greaterThanOrEqualTo(root.get("createdAt"), from);
    }

    public static Specification<Prompt> createdBefore(LocalDateTime to) {
        if (to == null) return null;
        return (root, query, cb) -> cb.lessThanOrEqualTo(root.get("createdAt"), to);
    }
}
