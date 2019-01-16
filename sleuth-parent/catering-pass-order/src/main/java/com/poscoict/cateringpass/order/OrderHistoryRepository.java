package com.poscoict.cateringpass.order;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderHistoryRepository extends JpaRepository<OrderHistoryJpo, String> {
	List<OrderHistoryJpo> findByOrderId(String orderId);
}
