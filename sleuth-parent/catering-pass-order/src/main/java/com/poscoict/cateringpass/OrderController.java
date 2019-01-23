package com.poscoict.cateringpass;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.poscoict.cateringpass.order.OrderHistoryJpo;
import com.poscoict.cateringpass.order.OrderHistoryRepository;
import com.poscoict.cateringpass.order.OrderJpo;
import com.poscoict.cateringpass.order.OrderRepository;
import com.poscoict.glueframework.biz.control.GlueBizController;
import com.poscoict.glueframework.context.GlueContext;
import com.poscoict.glueframework.context.GlueDefaultContext;

import brave.Tracer;

@RequestMapping("/order")
@RestController
public class OrderController {
	private final Logger logger = LoggerFactory.getLogger(getClass());

	@Value("${catering.payment.base-uri:http://localhost:9202}")
	private String uri;

	@Autowired
	private OrderRepository orderRepository;

	@Autowired
	private OrderHistoryRepository orderHistoryRepository;

	@Autowired
	private GlueBizController bizController;

	@Autowired
	private RestTemplate restTemplate;

	@Autowired
	private Tracer tracer;

	@PostMapping(params = { "userId", "takeOutId", "testCase" })
	public void makeOrderSagaPattern(@RequestParam String takeOutId, @RequestParam String userId,
			@RequestParam String testCase) {
		System.out.println("");
		System.out.println("");
		System.out.println("");
		System.out.println("");
		System.out.println("");
		this.logger.info("POST /order");
		this.logger.info("@RequestParam {}={}", "userId", userId);
		this.logger.info("@RequestParam {}={}", "takeOutId", takeOutId);
		this.logger.info("@RequestParam {}={}", "testCase", testCase);
		this.logger.info("{}", tracer.toString());
		this.logger.info("{}", tracer.currentSpan().toString());

		this.logger.info("POST /order");
		this.logger.info("##### [Order Service] #####");
		System.out.println("POST /order ");
		System.out.println(tracer.toString());
		System.out.println(tracer.currentSpan().toString());

		OrderJpo orderJpo = new OrderJpo(userId, takeOutId);
		this.orderRepository.save(orderJpo);
		OrderHistoryJpo orderHistoryJpo = new OrderHistoryJpo(orderJpo);
		this.orderHistoryRepository.save(orderHistoryJpo);

		GlueContext ctx = new GlueDefaultContext("order-sleuth-service");
		ctx.put("userId", userId);
		ctx.put("takeOutId", takeOutId);
		ctx.put("orderId", orderJpo.getUuid());
		ctx.put("case", testCase);
		ctx.put("payment-uri", this.uri + "/payment/{command}");
		bizController.doAction(ctx);

		@SuppressWarnings("unchecked")
		Map<String, Object> result = (Map<String, Object>) ctx.get("response");
		this.logger.info("{}", result);
//		orderJpo.setStatus( ctx.get("response").toString() );
//		this.orderRepository.save(orderJpo);
//		orderHistoryJpo = new OrderHistoryJpo(orderJpo);
//		this.orderHistoryRepository.save(orderHistoryJpo);

//		this.logger.info("##### [Order Service] #####");
	}

	@PostMapping(path = "/chaining/{testCase}")
	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public void makeOrderSequence(@PathVariable String testCase, @RequestParam String userId,
			@RequestParam String takeOutId) {
		System.out.println("");
		System.out.println("");
		System.out.println("");
		System.out.println("");
		System.out.println("");
		this.logger.info("POST /order/chaining/{}", testCase);
		this.logger.info("@RequestParam {}={}", "userId", userId);
		this.logger.info("@RequestParam {}={}", "takeOutId", takeOutId);
		this.logger.info("{}", tracer.toString());
		this.logger.info("{}", tracer.currentSpan().toString());

		// 1. 저장하기
		OrderJpo orderJpo = new OrderJpo(userId, takeOutId);
		this.orderRepository.save(orderJpo);

		OrderHistoryJpo orderHistoryJpo = new OrderHistoryJpo(orderJpo);
		this.orderHistoryRepository.save(orderHistoryJpo);

		// 2. 마이크로 서비스 호출하기
		String uri = this.uri + "/payment/chaining/{testCase}";
		Map<String, String> params = new HashMap<>();
		params.put("orderId", orderJpo.getUuid());
		params.put("testCase", testCase);
		params.put("userId", userId);
		params.put("takeOutId", takeOutId);
		this.logger.info("{}", uri);
		for (Entry<String, String> entry : params.entrySet()) {
			this.logger.info("{} {}={}", uri, entry.getKey(), entry.getValue());
		}
		try {
			restTemplate.postForObject(uri, params, params.getClass(), params);
		} catch (Exception e) {
			this.logger.info("{} {}", uri, e.getMessage());
		}
	}
}
