package com.poscoict.cateringpass;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.poscoict.cateringpass.delivery.DeliveryJpo;
import com.poscoict.cateringpass.delivery.DeliveryRepository;

import brave.Tracer;

@RestController
@RequestMapping("/delivery")
public class OrderCommnadController {
	private final Logger logger = LoggerFactory.getLogger(getClass());

	@Value("${catering.order.base-uri:http://localhost:9201}")
	private String uri;

	@Autowired
	private DeliveryRepository deliveryRepository;

	@Autowired
	private RestTemplate restTemplate;

	@Autowired
	private Tracer tracer;

	@PostMapping(path = "/{command}")
	public Object receiveMessageFromDeliveryChannel(@PathVariable String command,
			@RequestBody Map<String, Object> message) {
		System.out.println("");
		System.out.println("");
		System.out.println("");
		System.out.println("");
		System.out.println("");
		this.logger.info("POST /delivery/{}", command);
		for (Entry<String, Object> entry : message.entrySet()) {
			this.logger.info("@RequestBody {}={}", entry.getKey(), entry.getValue());
		}
		this.logger.info("{}", tracer.toString());
		this.logger.info("{}", tracer.currentSpan().toString());

		String channel = (String) message.get("channel");
		String testCase = (String) message.get("case");
		Map<String, Object> msg = new HashMap<>();
		if ("배송".equals(command)) {
			DeliveryJpo jpo = this.executeCommnd((String) message.get("orderId"));
			if ("error-delivery".equals(testCase)) {
				msg.put("channel-message", "오류:" + command);
			} else {
				msg.put("channel-message", "정상:" + command);
			}
			msg.put("deliveryId", jpo.getUuid());
		} else {
			this.logger.info("정의되지 않는 커맨드입니다.");
		}

		System.out.println("");
		if (!msg.isEmpty()) {
			msg.put("channel", "reply-channel");
			msg.put("source-channel", channel);
			msg.put("orderId", message.get("orderId"));
			String uri = this.uri + "/order/{orderId}";
			Object data = msg;
			this.logger.info("{}", uri);
			for (Entry<String, Object> entry : msg.entrySet()) {
				this.logger.info("{} {}={}", uri, entry.getKey(), entry.getValue());
			}
			return restTemplate.postForObject(uri, data, data.getClass(), msg);
		}
		return msg;
	}

	private DeliveryJpo executeCommnd(String orderId) {
		DeliveryJpo jpo = new DeliveryJpo(orderId);
		deliveryRepository.save(jpo);
		return jpo;
	}
}
