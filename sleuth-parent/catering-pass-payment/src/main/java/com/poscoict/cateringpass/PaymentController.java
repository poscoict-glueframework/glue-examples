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
import org.springframework.cloud.stream.messaging.Source;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
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
@EnableBinding(Source.class)
public class PaymentController {
	private final Logger logger = LoggerFactory.getLogger(getClass());

	@Value("${catering.stock.base-uri:http://localhost:9203}")
	private String uri;

	@Autowired
	private PaymentRepository paymentRepository;

	@Autowired
	private PaymentHistoryRepository paymentHistoryRepository;

	@Autowired
	private RestTemplate restTemplate;

	@Autowired
	private Tracer tracer;

	@Autowired
	private Source source;

	@GetMapping
	public String hi() {
		logger.info("GET /payment");
		return "hi";
	}

	@PostMapping(path = "/chaining/{testCase}")
	public Map<?, ?> makeOrderSequence(@PathVariable String testCase, @RequestBody Map<String, String> message) {
		System.out.println("");
		System.out.println("");
		System.out.println("");
		System.out.println("");
		System.out.println("");
		this.logger.info("POST /payment/chaining/{}", testCase);
		for (Entry<String, String> entry : message.entrySet()) {
			this.logger.info("@RequestBody {}={}", entry.getKey(), entry.getValue());
		}
		this.logger.info("{}", tracer.toString());
		this.logger.info("{}", tracer.currentSpan().toString());

		String orderId = (String) message.get("orderId");

		// 1. 저장하기
		PaymentJpo jpo = new PaymentJpo(orderId);
		this.paymentRepository.save(jpo);
		this.paymentHistoryRepository.save(new PaymentHistoryJpo(jpo));

		// 2. 마이크로 서비스 호출하기
		String uri = this.uri + "/stock/chaining/{testCase}";
		Map<String, String> params = new HashMap<>();
		params.putAll(message);
		params.put("paymentId", jpo.getUuid());
		this.logger.info("{}", uri);
		for (Entry<String, String> entry : params.entrySet()) {
			this.logger.info("{} {}={}", uri, entry.getKey(), entry.getValue());
		}

		Map<String, Object> event = new HashMap<>();
		event.put("channel-message", !"error-payment".equals(testCase) ? "지불 성공" : "지불 실패");
		try {
			if ("success".equals(testCase) || "error-stock".equals(testCase) || "error-delivery".equals(testCase)) {
				jpo.setStatus("재고 요청(REST)");
				this.paymentHistoryRepository.save(new PaymentHistoryJpo(jpo));

				Map<?, ?> result = restTemplate.postForObject(uri, params, params.getClass(), params);
				this.logger.info("{} {}", uri, result);

				jpo.setStatus("재고 결과(REST)");
				this.paymentHistoryRepository.save(new PaymentHistoryJpo(jpo));
				return result;
			} else {
				return params;
			}
		} catch (Exception e) {
			this.logger.info("{} {}", uri, e.getMessage());
			event.put("channel-message", "지불 오류");
			event.put("error", e.getMessage());
		} finally {
			// 3. Event Sourcing : 저장후 보내기
			event.putAll(params);
			Message<?> eventMessage = MessageBuilder.withPayload(event).setHeader("type", "payment-service").build();
			this.logger.info("EventSourcing {}", event);
			this.logger.info("EventSourcing {}", eventMessage);
			this.source.output().send(eventMessage);
		}

		return params;
	}

	@PutMapping(path = "/rollback/{testCase}")
	public void cancelOrder(@PathVariable String testCase, @RequestBody Map<String, String> message) {
		System.out.println("");
		System.out.println("");
		System.out.println("");
		System.out.println("");
		System.out.println("");
		this.logger.info("PUT /rollback/chaining/{}", testCase);
		for (Entry<String, String> entry : message.entrySet()) {
			this.logger.info("@RequestBody {}={}", entry.getKey(), entry.getValue());
		}
		this.logger.info("{}", tracer.toString());
		this.logger.info("{}", tracer.currentSpan().toString());

		String paymentId = (String) message.get("paymentId");
		Optional<PaymentJpo> value = this.paymentRepository.findById(paymentId);
		if (value.isPresent()) {
			PaymentJpo jpo = value.get();
			jpo.setStatus("지불 취소");
			this.paymentRepository.save(jpo);
			this.paymentHistoryRepository.save(new PaymentHistoryJpo(jpo));
		}

		// 3. Event Sourcing : 저장후 보내기
		Map<String, Object> event = new HashMap<>();
		event.put("channel-message", "지불 취소");
		event.putAll(message);
		Message<?> eventMessage = MessageBuilder.withPayload(event).setHeader("type", "payment-service").build();
		this.logger.info("EventSourcing {}", event);
		this.logger.info("EventSourcing {}", eventMessage);
		this.source.output().send(eventMessage);
	}
}
