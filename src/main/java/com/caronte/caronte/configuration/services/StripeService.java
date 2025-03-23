package com.caronte.caronte.configuration.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.caronte.caronte.company.Company;
import com.caronte.caronte.user.User;
import com.stripe.exception.StripeException;
import com.stripe.model.Customer;
import com.stripe.model.Subscription;
import com.stripe.param.CustomerCreateParams;
import com.stripe.param.CustomerListParams;
import com.stripe.param.SubscriptionCreateParams;

@Service
public class StripeService {

    @Value("${stripe.customer.premium.price.id}")
    private String customerPremiumPriceId;

    @Value("${stripe.company.premium.price.id}")
    private String companyPremiumPriceId;

    @Transactional
    public String subscription(String paymentMethodId, User user) throws StripeException {
        List<Customer> customers = Customer.list(CustomerListParams.builder()
                .setEmail(user.getEmail())
                .setLimit(1L)
                .build()).getData();

        // This "Customer" refers to Stripe's Customer class, not Caronte's.
        Customer customer = !customers.isEmpty() ? customers.getFirst()
                : Customer.create(
                        CustomerCreateParams.builder()
                                .setEmail(user.getEmail())
                                .build());

        String priceId = user instanceof Company ? companyPremiumPriceId : customerPremiumPriceId;
        SubscriptionCreateParams params = SubscriptionCreateParams.builder()
                .setCustomer(customer.getId())
                .addItem(
                        SubscriptionCreateParams.Item.builder()
                                .setPrice(priceId)
                                .build())
                .setDefaultPaymentMethod(paymentMethodId)
                .build();

        Subscription subscription = Subscription.create(params);
        return subscription.getId();
    }
}
