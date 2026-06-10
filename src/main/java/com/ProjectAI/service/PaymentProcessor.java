package com.ProjectAI.service;

import com.ProjectAI.dto.subscription.CheckoutRequest;
import com.ProjectAI.dto.subscription.CheckoutResponse;
import com.ProjectAI.dto.subscription.PortalResponse;
import com.stripe.model.StripeObject;
import org.jspecify.annotations.Nullable;

import java.util.Map;

public interface PaymentProcessor {
    CheckoutResponse createCheckoutSessionUrl(CheckoutRequest request);

    PortalResponse openCustomerPortal(Long userId);

    void handleWebhookEvent(String type, StripeObject stripeObject, Map<String, String> metadata);
}
