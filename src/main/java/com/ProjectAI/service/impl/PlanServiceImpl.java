package com.ProjectAI.service.impl;

import com.ProjectAI.dto.subscription.PlanResponse;
import com.ProjectAI.mapper.SubscriptionMapper;
import com.ProjectAI.repository.PlanRepository;
import com.ProjectAI.service.PlanService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
@RequiredArgsConstructor
public class PlanServiceImpl implements PlanService {
    private final PlanRepository planRepository;
    private final SubscriptionMapper subscriptionMapper;
    @Override
    public List<PlanResponse> getAllActivePlans() {
        return planRepository.
                findAllByActiveTrue()
                .stream()
                .map(subscriptionMapper::toPlanResponse)
                .toList();
    }
}
