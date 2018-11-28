package com.poscoict.cateringpass;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;

import com.poscoict.cateringpass.payment.PaymentHistoryJpo;
import com.poscoict.cateringpass.payment.PaymentHistoryRepository;
import com.poscoict.cateringpass.payment.PaymentJpo;
import com.poscoict.cateringpass.payment.PaymentRepository;

@Component
public class CommnadReceiver {
	private final Logger logger = LoggerFactory.getLogger(getClass());
	@Autowired
	private JmsTemplate jmsTemplate;
	@Autowired
	private PaymentRepository paymentRepository;
	@Autowired
	private PaymentHistoryRepository paymentHistoryRepository;

	@JmsListener(destination = "payment-service")
	public void receiveMessageFromPaymentChannel(Map<String, Object> message) throws InterruptedException {
		Thread.sleep(2000);
		this.logger.info("##### [Payment Service] #####");
		System.out.println("received");
		System.out.println(message.entrySet());
		/*
		 * received [extra-orderId=e10049c7-f22e-11e8-85cb-8f233f888911,
		 * extra-userId=yujin, extra-case=error-payment, channel=payment-channel,
		 * command=지불, extra-takeOutId=sandwich]
		 */
		String channel = (String) message.get("channel");
		String command = (String) message.get("command");
		String testCase = (String) message.get("extra-case");
		if ("지불".equals(command)) {
			this.logger.info("커맨드를 실행합니다. {}", command);
			String orderId = (String) message.get("extra-orderId");
			PaymentJpo jpo = new PaymentJpo(orderId);
			paymentRepository.save(jpo);
			PaymentHistoryJpo historyJpo = new PaymentHistoryJpo(jpo);
			paymentHistoryRepository.save(historyJpo);

			this.logger.info("Reply 메시지를 보냅니다.");
			Map<String, Object> msg = new HashMap<>();
			msg.put("channel", "reply-channel");
			msg.put("source-channel", channel);
			if ("error-payment".equals(testCase)) {
				msg.put("channel-message", "오류:지불");
			} else {
				msg.put("channel-message", "정상:지불");
			}
			for (String key : message.keySet()) {
				if (key.startsWith("extra-")) {
					msg.put(key.substring("extra-".length()), message.get(key));
				}
			}
			msg.put("paymentId", jpo.getUuid());

			jmsTemplate.convertAndSend("order-service", msg);
			this.logger.info("Reply 메시지: {}", msg.entrySet());

		} else if ("지불취소".equals(command)) {
			this.logger.info("커맨드를 실행합니다. {}", command);
			String paymentId = (String) message.get("extra-paymentId");
			Optional<PaymentJpo> value = paymentRepository.findById(paymentId);
			if (value.isPresent()) {
				PaymentJpo jpo = value.get();
				jpo.setStatus("지불취소");
				paymentRepository.save(jpo);
				PaymentHistoryJpo historyJpo = new PaymentHistoryJpo(jpo);
				paymentHistoryRepository.save(historyJpo);

				this.logger.info("Reply 메시지를 보냅니다.");
				Map<String, Object> msg = new HashMap<>();
				msg.put("channel", "reply-channel");
				msg.put("source-channel", channel);
				msg.put("channel-message", "정상:지불취소");// 결재성공, 결재실패
				for (String key : message.keySet()) {
					if (key.startsWith("extra-")) {
						msg.put(key.substring("extra-".length()), message.get(key));
					}
				}

				jmsTemplate.convertAndSend("order-service", msg);
				this.logger.info("Reply 메시지: {}", msg.entrySet());

			}
		} else {
			this.logger.info("지정되지 않는 커맨드입니다.");
		}
		this.logger.info("##### [Payment Service] #####");
		this.logger.info("");
		this.logger.info("");
		this.logger.info("");
	}
}
