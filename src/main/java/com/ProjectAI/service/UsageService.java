package com.ProjectAI.service;

import com.ProjectAI.dto.subscription.PlanLimitsResponse;
import com.ProjectAI.dto.subscription.UsageTodayResponse;
import org.jspecify.annotations.Nullable;

public interface UsageService {
    @Nullable UsageTodayResponse getTodayUsageOfUser(Long userId);

    @Nullable PlanLimitsResponse getCurrentSubscriptionLimitsOfUser(Long userId);
}
