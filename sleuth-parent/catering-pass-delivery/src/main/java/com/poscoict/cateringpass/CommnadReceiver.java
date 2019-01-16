package com.poscoict.cateringpass;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.poscoict.cateringpass.delivery.DeliveryJpo;
import com.poscoict.cateringpass.delivery.DeliveryRepository;

@RestController
@RequestMapping("/delivery")
public class CommnadReceiver {
	private final Logger logger = LoggerFactory.getLogger(getClass());

	@Autowired
	private DeliveryRepository deliveryRepository;

	@GetMapping
	public String hi() {
		logger.info("GET /delivery");
		return "hi";
	}

	@PostMapping(path = "/{command}")
	public void receiveMessageFromDeliveryChannel(@PathVariable String command,
			@RequestBody Map<String, Object> message) {
		logger.info("POST /delivery/{}", command);
		this.logger.info("");
		this.logger.info("");
		this.logger.info("");
		this.logger.info("##### [Delivery Service] #####");
		System.out.println("received : " + message.entrySet());

		// String command = (String) message.get("command");
		String channel = (String) message.get("channel");
		String testCase = (String) message.get("extra-case");
		Map<String, Object> msg = new HashMap<>();
		this.logger.info("커맨드를 실행합니다. {}", command);
		if ("배송".equals(command)) {
			DeliveryJpo jpo = this.executeCommnd((String) message.get("extra-orderId"));
			if ("error-delivery".equals(testCase)) {
				msg.put("channel-message", "오류:" + command);
			} else {
				msg.put("channel-message", "정상:" + command);
			}
			msg.put("deliveryId", jpo.getUuid());
		} else {
			this.logger.info("정의되지 않는 커맨드입니다.");
		}
		if (!msg.isEmpty()) {
			msg.put("channel", "reply-channel");
			msg.put("source-channel", channel);
			for (Entry<String, Object> entry : message.entrySet()) {
				if (entry.getKey().startsWith("extra-") && entry.getValue() != null) {
					msg.put(entry.getKey().substring("extra-".length()), entry.getValue());
				}
			}
			this.logger.info("Reply 메시지를 보냅니다.");
			// jmsTemplate.convertAndSend("order-service", msg);
			this.logger.info("Reply 메시지: {}", msg.entrySet());
		}

		this.logger.info("##### [Delivery Service] #####");
	}

	private DeliveryJpo executeCommnd(String orderId) {
		DeliveryJpo jpo = new DeliveryJpo(orderId);
		deliveryRepository.save(jpo);
		return jpo;
	}
}
