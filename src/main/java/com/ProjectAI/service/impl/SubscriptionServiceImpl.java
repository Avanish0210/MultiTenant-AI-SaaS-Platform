package com.ProjectAI.service.impl;

import com.ProjectAI.dto.subscription.CheckoutRequest;
import com.ProjectAI.dto.subscription.CheckoutResponse;
import com.ProjectAI.dto.subscription.PortalResponse;
import com.ProjectAI.dto.subscription.SubscriptionResponse;
import com.ProjectAI.service.SubscriptionService;
import org.springframework.stereotype.Service;

@Service
public class SubscriptionServiceImpl implements SubscriptionService {
    @Override
    public SubscriptionResponse getCurrentSubscription(Long userId) {
        return null;
    }

    @Override
    public CheckoutResponse createCheckoutSessionUrl(CheckoutRequest request, Long userId) {
        return null;
    }

    @Override
    public PortalResponse openCustomerPortal(Long userId) {
        return null;
    }
}
