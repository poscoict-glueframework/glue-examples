package com.poscoict.cateringpass;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;

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

import com.poscoict.cateringpass.payment.PaymentHistoryJpo;
import com.poscoict.cateringpass.payment.PaymentHistoryRepository;
import com.poscoict.cateringpass.payment.PaymentJpo;
import com.poscoict.cateringpass.payment.PaymentRepository;

import brave.Tracer;

@RestController
@RequestMapping("/payment")
public class OrderCommandController {
	private final Logger logger = LoggerFactory.getLogger(getClass());

	@Value("${catering.order.base-uri:http://localhost:9201}")
	private String uri;

	@Autowired
	private PaymentRepository paymentRepository;

	@Autowired
	private PaymentHistoryRepository paymentHistoryRepository;

	@Autowired
	private RestTemplate restTemplate;

	@Autowired
	private Tracer tracer;

	@PostMapping(path = "/{command}")
	public Object receiveMessageFromPaymentChannel(@PathVariable String command,
			@RequestBody Map<String, Object> message) {
		System.out.println("");
		System.out.println("");
		System.out.println("");
		System.out.println("");
		System.out.println("");
		this.logger.info("POST /payment/{}", command);
		for (Entry<String, Object> entry : message.entrySet()) {
			this.logger.info("@RequestBody {}={}", entry.getKey(), entry.getValue());
		}
		this.logger.info("{}", tracer.toString());
		this.logger.info("{}", tracer.currentSpan().toString());

		String channel = (String) message.get("channel");
		String testCase = (String) message.get("case");
		String paymentId = null;
		Map<String, Object> msg = new HashMap<>();
		if ("지불".equals(command)) {
			PaymentJpo jpo = this.executeCommnd((String) message.get("orderId"));
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

		System.out.println("");
		if (!msg.isEmpty()) {
			this.beforeReply(paymentId, msg);
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
