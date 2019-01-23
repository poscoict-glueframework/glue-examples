package com.poscoict.cateringpass;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;

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

import com.poscoict.cateringpass.payment.PaymentHistoryJpo;
import com.poscoict.cateringpass.payment.PaymentHistoryRepository;
import com.poscoict.cateringpass.payment.PaymentJpo;
import com.poscoict.cateringpass.payment.PaymentRepository;

@EnableBinding(Sink.class)
public class OrderEventConsumer {
	private final Logger logger = LoggerFactory.getLogger(getClass());

	@Value("${catering.payment.base-uri:http://localhost:9202}")
	private String uri;

	@Autowired
	private RestTemplate restTemplate;

	@Autowired
	private PaymentRepository paymentRepository;

	@Autowired
	private PaymentHistoryRepository paymentHistoryRepository;

	@StreamListener(target = Sink.INPUT) // , condition = "header['type']=='stock-service'")
	public void receivedStock(@Payload Map<String, String> event, @Headers MessageHeaders headers) {
		System.out.println("");
		System.out.println("");
		System.out.println("");
		System.out.println("");
		System.out.println("");
		this.logger.info("KAFKA /stock-service");
		for (Entry<String, String> entry : event.entrySet()) {
			this.logger.info("@Payload {}={}", entry.getKey(), entry.getValue());
		}
		for (Entry<String, Object> entry : headers.entrySet()) {
			this.logger.info("@Headers {}={}", entry.getKey(), entry.getValue());
		}

		String paymentId = event.get("paymentId");
		Optional<PaymentJpo> value = this.paymentRepository.findById(paymentId);
		if (value.isPresent()) {
			PaymentJpo jpo = value.get();
			jpo.setStatus(event.get("channel-message"));
			this.paymentRepository.save(jpo);

			jpo.setStatus(event.get("channel-message") + "(KAFKA)");
			this.paymentHistoryRepository.save(new PaymentHistoryJpo(jpo));
		}

		if (!"재고 있음".equals(event.get("channel-message"))) {
			// 1. 마이크로 서비스 호출하기
			String uri = this.uri + "/payment/rollback/{testCase}";
			Map<String, String> params = new HashMap<>();
			params.put("orderId", event.get("orderId"));
			params.put("testCase", "error-payment");
			params.put("userId", event.get("userId"));
			params.put("takeOutId", event.get("takeOutId"));
			params.put("paymentId", event.get("paymentId"));
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
