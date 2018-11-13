package com.poscoict.cateringpass.jpa;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class CateringPassStore {
	@Autowired
	OrderRepository orderRepo;
	@Autowired
	UserRepository userRepo;
	@Autowired
	TakeOutRepository takeOutRepo;

	public List<TakeOutJpo> findTakeoutAll() {
		return takeOutRepo.findAll();
	}

	public String createOrder(String userId, String takeOutId) {
		OrderJpo jpo = new OrderJpo(userId, takeOutId);
		orderRepo.save(jpo); // 예약주문 정보 생성
		return jpo.getUuid();
	}

	public String getTakeOutId(String orderId) {
		Optional<OrderJpo> value = orderRepo.findById(orderId);
		OrderJpo jpo = value.get();
		return jpo.getTakeOutId(); // 예약주문시 사용한 take out ID 획득
	}

	public String isCancelAvailable(String takeOutId) {
		Optional<TakeOutJpo> value = takeOutRepo.findById(takeOutId);
		TakeOutJpo jpo = value.get();
		return jpo.isClose() ? "close" : "open"; // 마감여부 정보 획득
	}

	public boolean cancelOrder(String orderId) {
		Optional<OrderJpo> value = orderRepo.findById(orderId);
		if (value.isPresent()) {
			OrderJpo jpo = value.get();
			jpo.setOrderStatus("취소"); // 예약취소
			orderRepo.save(jpo);
			return true;
		}
		return false;
	}
}
