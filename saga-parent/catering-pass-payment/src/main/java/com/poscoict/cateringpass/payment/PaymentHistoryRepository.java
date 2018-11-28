package com.poscoict.cateringpass.payment;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentHistoryRepository extends JpaRepository<PaymentHistoryJpo, String> {
	List<PaymentHistoryJpo> findByPaymentId(String paymentId);
}
