package com.poscoict.cateringpass;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.poscoict.cateringpass.payment.PaymentHistoryJpo;
import com.poscoict.cateringpass.payment.PaymentHistoryRepository;
import com.poscoict.cateringpass.payment.PaymentJpo;
import com.poscoict.cateringpass.payment.PaymentRepository;

@RestController
@RequestMapping("/payment")
public class CommnadReceiver {
	private final Logger logger = LoggerFactory.getLogger(getClass());

	@Autowired
	private PaymentRepository paymentRepository;

	@Autowired
	private PaymentHistoryRepository paymentHistoryRepository;

	@GetMapping
	public String hi() {
		logger.info("GET /payment");
		return "hi";
	}

	@PostMapping(path = "/{command}")
	public void receiveMessageFromPaymentChannel(@PathVariable String command,
			@RequestBody Map<String, Object> message) {
		logger.info("POST /payment/{}", command);
		this.logger.info("");
		this.logger.info("");
		this.logger.info("");
		this.logger.info("##### [Payment Service] #####");
		System.out.println("received : " + message.entrySet());

		// String command = (String) message.get("command");
		String channel = (String) message.get("channel");
		String testCase = (String) message.get("extra-case");
		String paymentId = null;
		Map<String, Object> msg = new HashMap<>();
		this.logger.info("커맨드를 실행합니다. {}", command);
		if ("지불".equals(command)) {
			PaymentJpo jpo = this.executeCommnd((String) message.get("extra-orderId"));
			paymentId = jpo.getUuid();
			if ("error-payment".equals(testCase)) {
				msg.put("channel-message", "오류:" + command);
			} else {
				msg.put("channel-message", "정상:" + command);
			}
			msg.put("paymentId", paymentId);
		} else if ("지불취소".equals(command)) {
			paymentId = (String) message.get("extra-paymentId");
			boolean rollback = this.executeRollbackCommnd(paymentId, command);
			if (rollback) {
				msg.put("channel-message", "정상:" + command);// 결재성공, 결재실패
			}
		} else {
			this.logger.info("정의되지 않는 커맨드입니다.");
		}

		if (!msg.isEmpty()) {
			this.beforeReply(paymentId, msg);
			msg.put("channel", "reply-channel");
			msg.put("source-channel", channel);
			for (Entry<String, Object> entry : message.entrySet()) {
				if (entry.getKey().startsWith("extra-") && entry.getValue() != null) {
					msg.put(entry.getKey().substring("extra-".length()), entry.getValue());
				}
			}
			this.logger.info("Reply 메시지를 보냅니다.");
//			jmsTemplate.convertAndSend("order-service", msg);
			this.logger.info("Reply 메시지: {}", msg.entrySet());
		}
		this.logger.info("##### [Payment Service] #####");
	}

	private PaymentJpo executeCommnd(String orderId) {
		PaymentJpo jpo = new PaymentJpo(orderId);
		paymentRepository.save(jpo);
		paymentHistoryRepository.save(new PaymentHistoryJpo(jpo));
		return jpo;
	}

	private boolean executeRollbackCommnd(String paymentId, String command) {
		Optional<PaymentJpo> value = paymentRepository.findById(paymentId);
		if (value.isPresent()) {
			PaymentJpo jpo = value.get();
			jpo.setStatus(command);
			paymentRepository.save(jpo);
			paymentHistoryRepository.save(new PaymentHistoryJpo(jpo));
			return true;
		}
		return false;
	}

	private void beforeReply(String paymentId, Map<String, Object> msg) {
		Optional<PaymentJpo> value = paymentRepository.findById(paymentId);
		if (value.isPresent()) {
			PaymentJpo jpo = value.get();
			jpo.setStatus("" + msg.get("channel-message"));
			paymentRepository.save(jpo);
			paymentHistoryRepository.save(new PaymentHistoryJpo(jpo));
		}
	}
}
