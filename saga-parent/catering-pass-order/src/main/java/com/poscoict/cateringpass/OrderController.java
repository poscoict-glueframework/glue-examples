package com.poscoict.cateringpass;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.poscoict.cateringpass.order.OrderHistoryJpo;
import com.poscoict.cateringpass.order.OrderHistoryRepository;
import com.poscoict.cateringpass.order.OrderJpo;
import com.poscoict.cateringpass.order.OrderRepository;
import com.poscoict.glueframework.biz.control.GlueBizController;
import com.poscoict.glueframework.context.GlueContext;
import com.poscoict.glueframework.context.GlueDefaultContext;

@RequestMapping("/order")
@RestController
public class OrderController {
	private final Logger logger = LoggerFactory.getLogger(getClass());
	@Autowired
	private OrderRepository orderRepository;
	@Autowired
	private OrderHistoryRepository orderHistoryRepository;

	@Autowired
	private GlueBizController bizController;

	@PostMapping(params = { "userId", "takeOutId", "testCase" })
	public void makeOrder(@RequestParam String takeOutId, @RequestParam String userId, @RequestParam String testCase) {
		this.logger.info("");
		this.logger.info("");
		this.logger.info("");
		System.out.println("POST /order ");
		this.logger.info("##### [Order Service] #####");

		OrderJpo orderJpo = new OrderJpo(userId, takeOutId);
		this.orderRepository.save(orderJpo);
		OrderHistoryJpo orderHistoryJpo = new OrderHistoryJpo(orderJpo);
		this.orderHistoryRepository.save(orderHistoryJpo);

		GlueContext ctx = new GlueDefaultContext("order-saga-service");
		ctx.put("userId", userId);
		ctx.put("takeOutId", takeOutId);
		ctx.put("orderId", orderJpo.getUuid());
		ctx.put("case", testCase);
		bizController.doAction(ctx);

		@SuppressWarnings("unchecked")
		Map<String, Object> result = (Map<String, Object>) ctx.get("jms-message");
		orderJpo.setStatus(result.get("command") + " CMD 보냄");
		this.orderRepository.save(orderJpo);
		orderHistoryJpo = new OrderHistoryJpo(orderJpo);
		this.orderHistoryRepository.save(orderHistoryJpo);

		this.logger.info("##### [Order Service] #####");
	}
}
