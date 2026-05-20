package com.ProjectAI.service;

import com.ProjectAI.dto.subscription.PlanLimitsResponse;
import com.ProjectAI.dto.subscription.UsageTodayResponse;
import org.jspecify.annotations.Nullable;

public interface UsageService {
    UsageTodayResponse getTodayUsageOfUser(Long userId);

    PlanLimitsResponse getCurrentSubscriptionLimitsOfUser(Long userId);
}
