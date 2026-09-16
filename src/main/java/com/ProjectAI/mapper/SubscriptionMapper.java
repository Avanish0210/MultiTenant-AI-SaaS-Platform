package com.ProjectAI.mapper;

import com.ProjectAI.dto.subscription.PlanResponse;
import com.ProjectAI.dto.subscription.SubscriptionResponse;
import com.ProjectAI.entity.Plan;
import com.ProjectAI.entity.Subscription;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface SubscriptionMapper {

    SubscriptionResponse toSubscriptionResponse(Subscription subscription);

    @Mapping(source = "maxProduct", target = "maxProjects")
    PlanResponse toPlanResponse(Plan plan);
}
