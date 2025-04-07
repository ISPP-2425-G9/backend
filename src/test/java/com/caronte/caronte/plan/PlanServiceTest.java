package com.caronte.caronte.plan;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.caronte.caronte.user.User;
import com.caronte.caronte.user.UserRepository;
import com.stripe.exception.StripeException;
import com.stripe.model.Subscription;

@ExtendWith(MockitoExtension.class)
public class PlanServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PlanRepository planRepository;

    @InjectMocks
    private PlanService planService;

    private User user;
    private Plan plan;

    @BeforeEach
    void setUp() {
        user = new User();
        plan = new Plan();
        user.setPlan(plan);
    }

    @Test
    void testChangePlan_PremiumToFree_ShouldCancelSubscription() throws StripeException {
        plan.setPlanType(PlanType.PREMIUM);
        plan.setSubscriptionId("sub_123");

        Subscription mockSubscription = mock(Subscription.class);
        when(mockSubscription.cancel()).thenReturn(mockSubscription);
        mockStatic(Subscription.class);
        when(Subscription.retrieve("sub_123")).thenReturn(mockSubscription);

        when(planRepository.save(any(Plan.class))).thenReturn(plan);

        Plan updatedPlan = planService.changePlan(user, PlanType.FREE, null);

        assertEquals(PlanType.FREE, updatedPlan.getPlanType());
        verify(mockSubscription).cancel();
    }

    @Test
    void testChangePlan_FreeToPremium_ShouldUpdateSubscription() throws StripeException {
        plan.setPlanType(PlanType.FREE);

        when(planRepository.save(any(Plan.class))).thenReturn(plan);

        Plan updatedPlan = planService.changePlan(user, PlanType.PREMIUM, "sub_456");

        assertEquals(PlanType.PREMIUM, updatedPlan.getPlanType());
        assertEquals("sub_456", updatedPlan.getSubscriptionId());
    }

    @Test
    void testGetPlanInfo_ShouldReturnPlanResponse() throws StripeException {
        // user.setId(1L);
        // plan.setPlanType(PlanType.PREMIUM);

        // when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        // PlanResponse response = planService.getPlanInfo(1L);

        // assertNotNull(response);
        // assertEquals(PlanType.PREMIUM, response.getPlanType());
    }

    
}
