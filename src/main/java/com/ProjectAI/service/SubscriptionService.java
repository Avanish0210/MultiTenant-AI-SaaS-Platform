package com.ProjectAI.service;

import com.ProjectAI.dto.subscription.CheckoutRequest;
import com.ProjectAI.dto.subscription.CheckoutResponse;
import com.ProjectAI.dto.subscription.PortalResponse;
import com.ProjectAI.dto.subscription.SubscriptionResponse;
import org.jspecify.annotations.Nullable;

public interface SubscriptionService {

    @Nullable SubscriptionResponse getCurrentSubscription(Long userId);

    @Nullable CheckoutResponse createCheckoutSessionUrl(CheckoutRequest request, Long userId);

    @Nullable PortalResponse openCustomerPortal(Long userId);
}
