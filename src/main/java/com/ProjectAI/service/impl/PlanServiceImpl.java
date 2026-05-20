package com.ProjectAI.service.impl;

import com.ProjectAI.dto.subscription.PlanResponse;
import com.ProjectAI.service.PlanService;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
public class PlanServiceImpl implements PlanService {

    @Override
    public List<PlanResponse> getAllActivePlans() {
        return List.of();
    }
}
