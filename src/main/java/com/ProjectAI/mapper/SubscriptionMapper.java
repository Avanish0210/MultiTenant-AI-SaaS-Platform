package com.ProjectAI.mapper;

import com.ProjectAI.dto.subscription.PlanResponse;
import com.ProjectAI.dto.subscription.SubscriptionResponse;
import com.ProjectAI.entity.Plan;
import com.ProjectAI.entity.Subscription;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface SubscriptionMapper {

    SubscriptionResponse toSubscriptionResponse(Subscription subscription);

    PlanResponse toPlanResponse(Plan plan);
}
