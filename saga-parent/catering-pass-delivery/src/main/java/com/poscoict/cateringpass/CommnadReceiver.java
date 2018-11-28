package com.poscoict.cateringpass;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;

import com.poscoict.cateringpass.delivery.DeliveryJpo;
import com.poscoict.cateringpass.delivery.DeliveryRepository;

@Component
public class CommnadReceiver {
	private final Logger logger = LoggerFactory.getLogger(getClass());
	@Autowired
	private JmsTemplate jmsTemplate;
	@Autowired
	private DeliveryRepository deliveryRepository;

	@JmsListener(destination = "delivery-service")
	public void receiveMessageFromDeliveryChannel(Map<String, Object> message) throws InterruptedException {
		Thread.sleep(2000);
		this.logger.info("##### [Delivery Service] #####");
		System.out.println("received");
		System.out.println(message.entrySet());
		String command = (String) message.get("command");
		String channel = (String) message.get("channel");
		String testCase = (String) message.get("extra-case");

		if ("배송".equals(command)) {
			this.logger.info("커맨드를 실행합니다. {}", command);
			String orderId = (String) message.get("extra-orderId");
			DeliveryJpo jpo = new DeliveryJpo(orderId);
			deliveryRepository.save(jpo);

			this.logger.info("Reply 메시지를 보냅니다.");
			Map<String, Object> msg = new HashMap<>();
			msg.put("channel", "reply-channel");
			msg.put("source-channel", channel);
			if ("error-delivery".equals(testCase)) {
				msg.put("channel-message", "오류:배송");
			} else {
				msg.put("channel-message", "정상:배송");
			}
			for (String key : message.keySet()) {
				if (key.startsWith("extra-")) {
					msg.put(key.substring("extra-".length()), message.get(key));
				}
			}
			msg.put("deliveryId", jpo.getUuid());

			jmsTemplate.convertAndSend("order-service", msg);
			this.logger.info("Reply 메시지: {}", msg.entrySet());

		} else {
			this.logger.info("지정되지 않는 커맨드입니다.");
		}
		this.logger.info("##### [Payment Service] #####");
		this.logger.info("");
		this.logger.info("");
		this.logger.info("");
	}
}
