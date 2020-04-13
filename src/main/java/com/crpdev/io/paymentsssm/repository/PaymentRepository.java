package com.crpdev.io.paymentsssm.repository;

import com.crpdev.io.paymentsssm.domain.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
}
