package com.caronte.caronte.configuration.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import com.caronte.caronte.company.Company;
import com.caronte.caronte.configuration.services.StripeService;
import com.caronte.caronte.user.User;
import com.stripe.exception.StripeException;
import com.stripe.model.Customer;
import com.stripe.model.CustomerCollection;
import com.stripe.model.PaymentIntent;
import com.stripe.model.Price;
import com.stripe.model.PaymentMethod;
import com.stripe.model.Subscription;
import com.stripe.model.SubscriptionCollection;
import com.stripe.param.CustomerListParams;
import com.stripe.param.PaymentIntentCreateParams;
import com.stripe.param.PaymentMethodAttachParams;
import com.stripe.param.SubscriptionCreateParams;
import com.stripe.param.SubscriptionListParams;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class StripeServiceTest {

    private StripeService stripeService;

    // Valores dummy para las propiedades inyectadas
    private final String secretKey = "sk_test_dummy";
    private final String customerPremiumPriceId = "price_customer_dummy";
    private final String companyPremiumPriceId = "price_company_dummy";
    private final String obituaryPriceId = "price_obituary_dummy";

    @BeforeEach
    void setUp() {
        // Crea la instancia (se invoca el constructor que asigna Stripe.apiKey)
        stripeService = new StripeService(secretKey);
        // Inyecta los valores de las propiedades usando reflexión
        try {
            var field1 = StripeService.class.getDeclaredField("customerPremiumPriceId");
            field1.setAccessible(true);
            field1.set(stripeService, customerPremiumPriceId);
            var field2 = StripeService.class.getDeclaredField("companyPremiumPriceId");
            field2.setAccessible(true);
            field2.set(stripeService, companyPremiumPriceId);
            var field3 = StripeService.class.getDeclaredField("obituaryPriceId");
            field3.setAccessible(true);
            field3.set(stripeService, obituaryPriceId);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void testPay_success() throws StripeException {
        String paymentMethodId = "pm_123";
        String email = "user@example.com";

        // --- Simular Customer retrieval ---
        // Creamos un dummy stripe customer
        Customer dummyCustomer = mock(Customer.class);
        when(dummyCustomer.getId()).thenReturn("cus_123");

        // Creamos un dummy CustomerCollection cuyo getData() retorne el customer
        CustomerCollection dummyCustomerCollection = mock(CustomerCollection.class);
        when(dummyCustomerCollection.getData()).thenReturn(List.of(dummyCustomer));

        // --- Simular Price retrieval ---
        Price dummyPrice = mock(Price.class);
        when(dummyPrice.getUnitAmount()).thenReturn(1000L);
        when(dummyPrice.getCurrency()).thenReturn("usd");

        // --- Simular PaymentIntent creation ---
        PaymentIntent dummyPaymentIntent = mock(PaymentIntent.class);

        // Uso de mock estático para Customer, Price y PaymentIntent
        try (MockedStatic<Customer> customerStatic = Mockito.mockStatic(Customer.class);
             MockedStatic<Price> priceStatic = Mockito.mockStatic(Price.class);
             MockedStatic<PaymentIntent> paymentIntentStatic = Mockito.mockStatic(PaymentIntent.class)) {

            // Cuando se invoque Customer.list(...) se devuelve nuestra colección dummy
            customerStatic.when(() -> Customer.list(any(CustomerListParams.class)))
                          .thenReturn(dummyCustomerCollection);

            // Simula Price.retrieve sobre el precio del Obituary
            priceStatic.when(() -> Price.retrieve(obituaryPriceId))
                       .thenReturn(dummyPrice);

            // Simula la creación del PaymentIntent
            paymentIntentStatic.when(() -> PaymentIntent.create(any(PaymentIntentCreateParams.class)))
                               .thenReturn(dummyPaymentIntent);

            // Ejecuta el método
            assertDoesNotThrow(() -> stripeService.pay(paymentMethodId, email));

            // Verifica que se llamó a PaymentIntent.create
            paymentIntentStatic.verify(() -> PaymentIntent.create(any(PaymentIntentCreateParams.class)));
        }
    }

    @Test
    void testSubscription_nonCompany_success() throws StripeException {
        String paymentMethodId = "pm_456";
        // Creamos un User dummy (no Company)
        User dummyUser = new User();
        dummyUser.setEmail("dummy@example.com");

        // --- Simular Customer retrieval ---
        Customer dummyCustomer = mock(Customer.class);
        when(dummyCustomer.getId()).thenReturn("cus_456");

        CustomerCollection dummyCustomerCollection = mock(CustomerCollection.class);
        when(dummyCustomerCollection.getData()).thenReturn(List.of(dummyCustomer));

        // --- Simular PaymentMethod retrieval y attach ---
        PaymentMethod dummyPaymentMethod = mock(PaymentMethod.class);
        when(dummyPaymentMethod.attach(any(PaymentMethodAttachParams.class))).thenReturn(dummyPaymentMethod);
        // --- Simular Subscription creation ---
        Subscription dummySubscription = mock(Subscription.class);
        when(dummySubscription.getId()).thenReturn("sub_123");

        SubscriptionCollection dummySubscriptionCollection = mock(SubscriptionCollection.class);
        // Se retorna una lista con un único objeto (la suscripción creada),
        // de modo que no se cancele ninguna otra.
        when(dummySubscriptionCollection.getData()).thenReturn(List.of(dummySubscription));

        try (MockedStatic<Customer> customerStatic = Mockito.mockStatic(Customer.class);
             MockedStatic<PaymentMethod> paymentMethodStatic = Mockito.mockStatic(PaymentMethod.class);
             MockedStatic<Subscription> subscriptionStatic = Mockito.mockStatic(Subscription.class)) {

            // Stub para Customer.list(...)
            customerStatic.when(() -> Customer.list(any(CustomerListParams.class)))
                          .thenReturn(dummyCustomerCollection);

            // Stub para PaymentMethod.retrieve(...)
            paymentMethodStatic.when(() -> PaymentMethod.retrieve(paymentMethodId))
                               .thenReturn(dummyPaymentMethod);

            // Stub para Subscription.create(...)
            subscriptionStatic.when(() -> Subscription.create(any(SubscriptionCreateParams.class)))
                              .thenReturn(dummySubscription);

            // Stub para Subscription.list(...)
            subscriptionStatic.when(() -> Subscription.list(any(SubscriptionListParams.class)))
                              .thenReturn(dummySubscriptionCollection);

            String subId = stripeService.subscription(paymentMethodId, dummyUser);
            assertEquals("sub_123", subId);

            // Verifica que se haya llamado attach en el PaymentMethod
            verify(dummyPaymentMethod).attach(any(PaymentMethodAttachParams.class));
        }
    }

    @Test
    void testSubscription_company_success() throws StripeException {
        String paymentMethodId = "pm_789";
        // Creamos un Company dummy (subclase de User)
        Company dummyCompany = new Company();
        dummyCompany.setEmail("company@example.com");

        // --- Simular Customer retrieval ---
        Customer dummyCustomer = mock(Customer.class);
        when(dummyCustomer.getId()).thenReturn("cus_789");

        CustomerCollection dummyCustomerCollection = mock(CustomerCollection.class);
        when(dummyCustomerCollection.getData()).thenReturn(List.of(dummyCustomer));

        // --- Simular PaymentMethod retrieval y attach ---
        PaymentMethod dummyPaymentMethod = mock(PaymentMethod.class);
        when(dummyPaymentMethod.attach(any(PaymentMethodAttachParams.class))).thenReturn(dummyPaymentMethod);
        // --- Simular Subscription creation ---
        Subscription dummySubscription = mock(Subscription.class);
        when(dummySubscription.getId()).thenReturn("sub_789");

        SubscriptionCollection dummySubscriptionCollection = mock(SubscriptionCollection.class);
        when(dummySubscriptionCollection.getData()).thenReturn(List.of(dummySubscription));

        try (MockedStatic<Customer> customerStatic = Mockito.mockStatic(Customer.class);
             MockedStatic<PaymentMethod> paymentMethodStatic = Mockito.mockStatic(PaymentMethod.class);
             MockedStatic<Subscription> subscriptionStatic = Mockito.mockStatic(Subscription.class)) {

            customerStatic.when(() -> Customer.list(any(CustomerListParams.class)))
                          .thenReturn(dummyCustomerCollection);
            paymentMethodStatic.when(() -> PaymentMethod.retrieve(paymentMethodId))
                               .thenReturn(dummyPaymentMethod);
            subscriptionStatic.when(() -> Subscription.create(any(SubscriptionCreateParams.class)))
                              .thenReturn(dummySubscription);
            subscriptionStatic.when(() -> Subscription.list(any(SubscriptionListParams.class)))
                              .thenReturn(dummySubscriptionCollection);

            String subId = stripeService.subscription(paymentMethodId, dummyCompany);
            assertEquals("sub_789", subId);
            verify(dummyPaymentMethod).attach(any(PaymentMethodAttachParams.class));
        }
    }
}

