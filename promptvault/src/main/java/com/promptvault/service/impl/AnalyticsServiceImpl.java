package com.promptvault.service.impl;

import com.promptvault.dto.response.AnalyticsResponse;
import com.promptvault.entity.Prompt;
import com.promptvault.entity.Tag;
import com.promptvault.enums.PromptStatus;
import com.promptvault.mapper.PromptMapper;
import com.promptvault.repository.CollectionRepository;
import com.promptvault.repository.PromptRepository;
import com.promptvault.repository.TagRepository;
import com.promptvault.service.AnalyticsService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AnalyticsServiceImpl implements AnalyticsService {

    private final PromptRepository promptRepository;
    private final CollectionRepository collectionRepository;
    private final TagRepository tagRepository;
    private final PromptMapper promptMapper;

    private static final DateTimeFormatter MONTH_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM");

    @Override
    public AnalyticsResponse getAnalytics(UUID ownerId) {
        long activePrompts = promptRepository.countByOwnerIdAndStatus(ownerId, PromptStatus.ACTIVE);
        long archivedPrompts = promptRepository.countByOwnerIdAndStatus(ownerId, PromptStatus.ARCHIVED);
        long favoritePrompts = promptRepository.countByOwnerIdAndFavoriteTrue(ownerId);
        long collectionCount = collectionRepository.countByOwnerId(ownerId);

        List<Tag> tags = tagRepository.findAllByOwnerIdOrderByNameAsc(ownerId);
        Map<String, Long> tagUsage = new LinkedHashMap<>();
        for (Tag tag : tags) {
            Specification<Prompt> spec = Specification
                    .<Prompt>where((root, query, cb) -> cb.equal(root.get("owner").get("id"), ownerId))
                    .and((root, query, cb) -> {
                        query.distinct(true);
                        return cb.equal(root.join("tags").get("id"), tag.getId());
                    });
            long count = promptRepository.count(spec);
            tagUsage.put(tag.getName(), count);
        }

        List<Prompt> recent = promptRepository.findTop5ByOwnerIdAndStatusOrderByCreatedAtDesc(ownerId, PromptStatus.ACTIVE);

        // Monthly prompt creation for the last 6 months (including the current month).
        Map<String, Long> monthly = new LinkedHashMap<>();
        YearMonth current = YearMonth.now();
        for (int i = 5; i >= 0; i--) {
            YearMonth month = current.minusMonths(i);
            LocalDateTime start = month.atDay(1).atStartOfDay();
            LocalDateTime end = month.atEndOfMonth().atTime(23, 59, 59);
            long count = promptRepository.countByOwnerIdAndCreatedAtBetween(ownerId, start, end);
            monthly.put(month.format(MONTH_FORMAT), count);
        }

        return AnalyticsResponse.builder()
                .totalPrompts(activePrompts + archivedPrompts)
                .activePrompts(activePrompts)
                .archivedPrompts(archivedPrompts)
                .favoritePrompts(favoritePrompts)
                .collectionCount(collectionCount)
                .tagCount(tags.size())
                .tagUsage(tagUsage)
                .recentPrompts(recent.stream().map(promptMapper::toSummary).toList())
                .monthlyPromptCreation(monthly)
                .build();
    }
}
