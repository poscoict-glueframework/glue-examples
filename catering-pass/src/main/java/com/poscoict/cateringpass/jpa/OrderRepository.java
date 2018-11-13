package com.poscoict.cateringpass.jpa;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<OrderJpo, String> {
	List<OrderJpo> findByUserId(String userId);

	List<OrderJpo> findByUserIdAndOrderStatusAndOrderDayBetween(String userId, String orderStatus, Date from, Date to);

	Optional<OrderJpo> findByUuidAndOrderStatus(String uuid, String orderStatus);

	Long countByTakeOutIdAndOrderDay(String takeOutId, String orderDay);

	Long countByTakeOutIdAndOrderStatus(String takeOutId, String orderStatus);

	List<OrderJpo> findByTakeOutIdAndOrderStatus(String takeOutId, String orderStatus);
}
