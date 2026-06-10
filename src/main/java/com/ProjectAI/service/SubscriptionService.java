package com.ProjectAI.service;

import com.ProjectAI.dto.subscription.SubscriptionResponse;

public interface SubscriptionService {

    SubscriptionResponse getCurrentSubscription(Long userId);

}
