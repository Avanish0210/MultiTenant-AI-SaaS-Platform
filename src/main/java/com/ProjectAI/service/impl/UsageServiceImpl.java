package com.ProjectAI.service.impl;

import com.ProjectAI.dto.subscription.PlanLimitsResponse;
import com.ProjectAI.dto.subscription.UsageTodayResponse;
import com.ProjectAI.service.UsageService;
import org.springframework.stereotype.Service;

@Service
public class UsageServiceImpl implements UsageService {
    @Override
    public UsageTodayResponse getTodayUsageOfUser(Long userId) {
        return null;
    }

    @Override
    public PlanLimitsResponse getCurrentSubscriptionLimitsOfUser(Long userId) {
        return null;
    }
}
