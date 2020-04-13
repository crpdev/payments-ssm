package com.crpdev.io.paymentsssm.services;

import com.crpdev.io.paymentsssm.domain.Payment;
import com.crpdev.io.paymentsssm.repository.PaymentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class PaymentServiceImplTest {

    @Autowired
    PaymentRepository paymentRepository;

    @Autowired
    PaymentService paymentService;

    Payment payment;

    @BeforeEach
    void setUp() {
        payment = new Payment().builder().amount(new BigDecimal(12.99)).build();
    }

    @Test
    @Transactional
    void preAuth() {

        Payment savedPayment = paymentService.newPayment(payment);

        paymentService.preAuth(savedPayment.getId());

        Payment preAuthPayment = paymentRepository.getOne(savedPayment.getId());
        System.out.println(preAuthPayment.getState());


    }
}

