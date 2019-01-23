package com.poscoict.cateringpass;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.messaging.Source;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.poscoict.cateringpass.delivery.DeliveryJpo;
import com.poscoict.cateringpass.delivery.DeliveryRepository;

import brave.Tracer;

@RestController
@RequestMapping("/delivery")
@EnableBinding(Source.class)
public class DeliveryController {
	private final Logger logger = LoggerFactory.getLogger(getClass());

	@Autowired
	private DeliveryRepository deliveryRepository;

	@Autowired
	private Tracer tracer;

	@Autowired
	private Source source;

	@GetMapping
	public String hi() {
		logger.info("GET /delivery");
		return "hi";
	}

	@PostMapping(path = "/chaining/{testCase}")
	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public void makeOrderSequence(@PathVariable String testCase, @RequestBody Map<String, String> message) {
		System.out.println("");
		System.out.println("");
		System.out.println("");
		System.out.println("");
		System.out.println("");
		this.logger.info("POST /delivery/chaining/{}", testCase);
		for (Entry<String, String> entry : message.entrySet()) {
			this.logger.info("@RequestBody {}={}", entry.getKey(), entry.getValue());
		}
		this.logger.info("{}", tracer.toString());
		this.logger.info("{}", tracer.currentSpan().toString());

		String orderId = (String) message.get("orderId");

		// 1. 저장하기
		DeliveryJpo jpo = new DeliveryJpo(orderId);
		deliveryRepository.save(jpo);

		Map<String, Object> event = new HashMap<>();
		event.put("deliveryId", jpo.getUuid());
		event.put("channel-message", "success".equals(testCase) ? "배송 가능" : "배송 불가");

		// 2. 마지막 마이크로 서비스임.

		// 3. Event Sourcing : 저장후 보내기
		event.putAll(message);
		Message<?> eventMessage = MessageBuilder.withPayload(event).setHeader("type", "delivery-service").build();
		this.logger.info("EventSourcing {}", event);
		this.logger.info("EventSourcing {}", eventMessage);
		source.output().send(eventMessage);
	}
}
