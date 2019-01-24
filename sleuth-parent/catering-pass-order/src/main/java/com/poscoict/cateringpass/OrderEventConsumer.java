package com.poscoict.cateringpass;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.cloud.stream.messaging.Sink;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.handler.annotation.Headers;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.web.client.RestTemplate;

import com.poscoict.cateringpass.order.OrderHistoryJpo;
import com.poscoict.cateringpass.order.OrderHistoryRepository;
import com.poscoict.cateringpass.order.OrderJpo;
import com.poscoict.cateringpass.order.OrderRepository;

@EnableBinding(Sink.class)
public class OrderEventConsumer {
	private final Logger logger = LoggerFactory.getLogger(getClass());

	@Value("${catering.order.base-uri:http://localhost:9201}")
	private String uri;

	@Autowired
	private RestTemplate restTemplate;

	@Autowired
	private OrderRepository orderRepository;

	@Autowired
	private OrderHistoryRepository orderHistoryRepository;

	@StreamListener(target = Sink.INPUT) // , condition = "header['type']=='payment-service'")
	public void receivedPayment(@Payload Map<String, String> event, @Headers MessageHeaders headers) {
		System.out.println("");
		System.out.println("");
		System.out.println("");
		System.out.println("");
		System.out.println("");
		this.logger.info("KAFKA /payment-service");
		for (Entry<String, String> entry : event.entrySet()) {
			this.logger.info("@Payload {}={}", entry.getKey(), entry.getValue());
		}
		for (Entry<String, Object> entry : headers.entrySet()) {
			this.logger.info("@Headers {}={}", entry.getKey(), entry.getValue());
		}

		String orderId = event.get("orderId");
		Optional<OrderJpo> value = this.orderRepository.findById(orderId);
		if (value.isPresent()) {
			OrderJpo jpo = value.get();
			jpo.setStatus(event.get("channel-message"));
			this.orderRepository.save(jpo);

			jpo.setStatus(event.get("channel-message") + "(KAFKA)");
			OrderHistoryJpo historyJpo = new OrderHistoryJpo(jpo);
			historyJpo.setReceivedMessage("" + event.entrySet());
			this.orderHistoryRepository.save(historyJpo);
		}

		if (!"지불 성공".equals(event.get("channel-message"))) {
			// 1. 마이크로 서비스 호출하기
			String uri = this.uri + "/order/rollback/{testCase}";
			Map<String, String> params = new HashMap<>();
			params.put("orderId", event.get("orderId"));
			params.put("testCase", "error-payment");
			params.put("userId", event.get("userId"));
			params.put("takeOutId", event.get("takeOutId"));
			this.logger.info("{}", uri);
			for (Entry<String, String> entry : params.entrySet()) {
				this.logger.info("{} {}={}", uri, entry.getKey(), entry.getValue());
			}
			try {
				this.restTemplate.put(uri, params, params);
			} catch (Exception e) {
				this.logger.info("{} {}", uri, e.getMessage());
			}
		}
	}
}
