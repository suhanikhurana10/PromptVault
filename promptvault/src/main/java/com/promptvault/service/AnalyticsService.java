package com.promptvault.service;

import com.promptvault.dto.response.AnalyticsResponse;

import java.util.UUID;

public interface AnalyticsService {
    AnalyticsResponse getAnalytics(UUID ownerId);
}
