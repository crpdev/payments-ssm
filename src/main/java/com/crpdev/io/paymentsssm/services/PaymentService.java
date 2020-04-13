package com.crpdev.io.paymentsssm.services;

import com.crpdev.io.paymentsssm.domain.Payment;
import com.crpdev.io.paymentsssm.domain.PaymentEvent;
import com.crpdev.io.paymentsssm.domain.PaymentState;
import org.springframework.statemachine.StateMachine;

public interface PaymentService {

    Payment newPayment(Payment payment);

    StateMachine<PaymentState, PaymentEvent> checkBalance(Long paymentId);

    StateMachine<PaymentState, PaymentEvent> getOverDraftCode(Long paymentId);

}
