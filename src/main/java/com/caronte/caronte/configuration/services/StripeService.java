package com.caronte.caronte.configuration.services;

import java.util.List;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.caronte.caronte.company.Company;
import com.caronte.caronte.user.User;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.Customer;
import com.stripe.model.PaymentIntent;
import com.stripe.model.PaymentMethod;
import com.stripe.model.Price;
import com.stripe.model.Subscription;
import com.stripe.param.CustomerCreateParams;
import com.stripe.param.CustomerListParams;
import com.stripe.param.PaymentIntentCreateParams;
import com.stripe.param.PaymentMethodAttachParams;
import com.stripe.param.SubscriptionCreateParams;
import com.stripe.param.SubscriptionListParams;

@Service
public class StripeService {

    @Value("${stripe.customer.premium.price.id}")
    private String customerPremiumPriceId;

    @Value("${stripe.company.premium.price.id}")
    private String companyPremiumPriceId;
    
    @Value("${stripe.obituary.price.id}")
    private String obituaryPriceId;

    public StripeService(@Value("${stripe.api.key}") String secretKey){
        Stripe.apiKey = secretKey;
    }

    @Transactional
    public void pay(String paymentMethodId, String email) throws StripeException {
        Customer customer = getCustomerByEmail(email);

        Price price = Price.retrieve(obituaryPriceId);

        PaymentIntentCreateParams params = PaymentIntentCreateParams.builder()
                .setAmount(price.getUnitAmount())
                .setCurrency(price.getCurrency())
                .setCustomer(customer.getId()) 
                .setPaymentMethod(paymentMethodId)
                .setConfirm(true) 
                .setAutomaticPaymentMethods(
                        PaymentIntentCreateParams.AutomaticPaymentMethods.builder()
                                .setEnabled(true)
                                .setAllowRedirects(
                                        PaymentIntentCreateParams.AutomaticPaymentMethods.AllowRedirects.NEVER)
                                .build())
                .build();

        PaymentIntent.create(params);
    }
    
    @Transactional
    public String subscription(String paymentMethodId, User user) throws StripeException {
        Customer customer = getCustomerByEmail(user.getEmail());
        String customerId = customer.getId();

        PaymentMethodAttachParams attachParams = PaymentMethodAttachParams.builder()
                .setCustomer(customerId)
                .build();

        PaymentMethod paymentMethod = PaymentMethod.retrieve(paymentMethodId);
        paymentMethod.attach(attachParams);

        String priceId = user instanceof Company ? companyPremiumPriceId : customerPremiumPriceId;
        SubscriptionCreateParams params = SubscriptionCreateParams.builder()
                .setCustomer(customerId)
                .addItem(
                        SubscriptionCreateParams.Item.builder()
                                .setPrice(priceId)
                                .build())
                .setDefaultPaymentMethod(paymentMethodId)
                .build();

        
        
        Subscription subscription = Subscription.create(params);

        SubscriptionListParams subscriptionListParams = SubscriptionListParams.builder()
                .setCustomer(customerId)
                .build();

        List<Subscription> subscriptions = Subscription.list(subscriptionListParams).getData();
        for (Subscription otherSubscription : subscriptions) {
                if(!Objects.equals(otherSubscription.getId(),  subscription.getId())){
                        otherSubscription.cancel();
                }
        }
        return subscription.getId();
    }

    private static Customer getCustomerByEmail(String email) throws StripeException {
        List<Customer> customers = Customer.list(CustomerListParams.builder()
                .setEmail(email)
                .setLimit(1L)
                .build()).getData();

        // This "Customer" refers to Stripe's Customer class, not Caronte's.
        Customer customer = !customers.isEmpty() ? customers.getFirst()
                : Customer.create(
                        CustomerCreateParams.builder()
                                .setEmail(email)
                                .build());

        return customer;
    }
}
