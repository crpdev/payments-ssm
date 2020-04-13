package com.crpdev.io.paymentsssm.config;

import com.crpdev.io.paymentsssm.domain.PaymentEvent;
import com.crpdev.io.paymentsssm.domain.PaymentState;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.config.StateMachineFactory;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class StateMachineConfigTest {

    @Autowired
    StateMachineFactory<PaymentState, PaymentEvent> factory;

    @Test
    void testNewStateMachine(){
        StateMachine<PaymentState, PaymentEvent> sm = factory.getStateMachine(UUID.randomUUID());
        sm.start();
        System.out.println(sm.getState().toString());

        sm.sendEvent(PaymentEvent.PRE_AUTH);
        System.out.println(sm.getState().toString());
        sm.sendEvent(PaymentEvent.PRE_AUTH_OK);
        System.out.println(sm.getState().toString());

    }
}
