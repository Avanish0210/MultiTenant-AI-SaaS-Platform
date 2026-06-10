package com.ProjectAI.service.impl;

import com.ProjectAI.dto.subscription.CheckoutRequest;
import com.ProjectAI.dto.subscription.CheckoutResponse;
import com.ProjectAI.dto.subscription.PortalResponse;
import com.ProjectAI.entity.Plan;
import com.ProjectAI.entity.User;
import com.ProjectAI.error.ResourceNotFoundException;
import com.ProjectAI.repository.PlanRepository;
import com.ProjectAI.repository.UserRepository;
import com.ProjectAI.security.AuthUtil;
import com.ProjectAI.service.PaymentProcessor;
import com.stripe.exception.StripeException;
import com.stripe.model.StripeObject;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class StripePaymentProcessor implements PaymentProcessor {
    private final AuthUtil authUtil;
    private final PlanRepository planRepository;
    private final UserRepository userRepository;

    @Value("${client.url}")
    private String frontendUrl;

    @Override
    public CheckoutResponse createCheckoutSessionUrl(CheckoutRequest request) {
        Plan plan = planRepository.findById(request.planId()).orElseThrow(()->
                new ResourceNotFoundException("plan" , request.planId().toString()));

        Long userId = authUtil.getCurrentUserId();
        User user = userRepository.findById(userId).orElseThrow(()->
                new ResourceNotFoundException("user" , userId.toString()));

         var params = SessionCreateParams.builder()
                 .addLineItem(
                         SessionCreateParams.LineItem.builder().setPrice(plan.getStipePriceId()).setQuantity(1L).build()
                 )
                 .setMode(SessionCreateParams.Mode.SUBSCRIPTION)
                 .setSubscriptionData(
                         new SessionCreateParams.SubscriptionData.Builder()
                                 .setBillingMode(SessionCreateParams.SubscriptionData.BillingMode.builder()
                                         .setType(SessionCreateParams.SubscriptionData.BillingMode.Type.FLEXIBLE)
                                         .build()

                                 )
                                 .build()
                 )

                 .setSuccessUrl(frontendUrl + "/success.html?session_id={CHECKOUT_SESSION_ID}")
                 .setCancelUrl(frontendUrl + "/cancel.html")
                 .putMetadata("user_id", userId.toString())
                 .putMetadata("plan_id", plan.getId().toString());

        try {
            String stripeCustomerId = user.getStripeCustomerId();
            if(stripeCustomerId == null || stripeCustomerId.isEmpty()) {
                params.setCustomerEmail(user.getUsername());
            } else {
                params.setCustomer(stripeCustomerId); // stripe customer Id
            }
            Session session = Session.create(params.build()); // making api call to the Strip Backend
            return new CheckoutResponse(session.getUrl());
        } catch (StripeException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public PortalResponse openCustomerPortal(Long userId) {
        return null;
    }

    @Override
    public void handleWebhookEvent(String type, StripeObject stripeObject, Map<String, String> metadata) {
        log.info("type");
    }
}
