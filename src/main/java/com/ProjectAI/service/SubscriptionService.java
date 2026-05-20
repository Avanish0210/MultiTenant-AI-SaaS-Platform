package com.ProjectAI.service;

import com.ProjectAI.dto.subscription.CheckoutRequest;
import com.ProjectAI.dto.subscription.CheckoutResponse;
import com.ProjectAI.dto.subscription.PortalResponse;
import com.ProjectAI.dto.subscription.SubscriptionResponse;
import org.jspecify.annotations.Nullable;

public interface SubscriptionService {

    SubscriptionResponse getCurrentSubscription(Long userId);

    CheckoutResponse createCheckoutSessionUrl(CheckoutRequest request, Long userId);

    PortalResponse openCustomerPortal(Long userId);
}
