package com.crpdev.io.paymentsssm.services;

import com.crpdev.io.paymentsssm.domain.Payment;
import com.crpdev.io.paymentsssm.domain.PaymentState;
import com.crpdev.io.paymentsssm.repository.PaymentRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Slf4j
@SpringBootTest
class PaymentServiceImplTest {

    @Autowired
    PaymentRepository paymentRepository;

    @Autowired
    PaymentService paymentService;

    Payment payment;

    @BeforeEach
    void setUp() {
        payment = new Payment().builder().amount(new BigDecimal(1000.00)).build();
    }


    /**
     * Test to verify the functionality of the State Machine
     * Step 1: Initiate a new Payment object
     * Step 2: Initiate Payment by calling the checkBalance method -> in turn triggers and event PaymentEvent.INIT_PAYMENT
     *         The outcome of INIT_PAYMENT can be either MAKE_PAYMENT or OVERDRAFT based on the balance
     * Step 3: If OverDraft, invoke the getOverDraftCode method -> in turn triggers and event PaymentEvent.GET_OVERDRAFT_CD
     *         The outcome of GET_OVERDRAFT_CD can be either MAKE_PAYMENT or DENY_PAYMENT based on the balance
     */
    @Test
    @Transactional
    void checkBalance() {


        Payment savedPayment = paymentService.newPayment(payment);
        log.info("Should be NEW_PAYMENT");
        log.info(savedPayment.getState().toString());


        paymentService.checkBalance(savedPayment.getId());

        Payment preAuthPayment = paymentRepository.getOne(savedPayment.getId());
        System.out.println(preAuthPayment.getState());

        if(PaymentState.OVERDRAFT.equals(preAuthPayment.getState())){
            paymentService.getOverDraftCode(savedPayment.getId());
            System.out.println(preAuthPayment.getState());
        }




    }
}

