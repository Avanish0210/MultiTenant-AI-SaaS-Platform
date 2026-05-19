package com.ProjectAI.service;

import com.ProjectAI.dto.subscription.PlanResponse;
import org.jspecify.annotations.Nullable;

import java.util.List;

public interface PlanService {
    @Nullable List<PlanResponse> getAllActivePlans();
}
