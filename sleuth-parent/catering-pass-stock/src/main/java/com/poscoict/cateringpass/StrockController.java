package com.poscoict.cateringpass;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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
import org.springframework.web.client.RestTemplate;

import com.poscoict.cateringpass.stock.StockHistoryJpo;
import com.poscoict.cateringpass.stock.StockHistoryRepository;
import com.poscoict.cateringpass.stock.StockJpo;
import com.poscoict.cateringpass.stock.StockRepository;

import brave.Tracer;

@RestController
@RequestMapping("/stock")
@EnableBinding(Source.class)
public class StrockController {
	private final Logger logger = LoggerFactory.getLogger(getClass());

	@Value("${catering.delivery.base-uri:http://localhost:9204}")
	private String uri;

	@Autowired
	private StockRepository stockRepository;

	@Autowired
	private StockHistoryRepository stockHistoryRepository;

	@Autowired
	private RestTemplate restTemplate;

	@Autowired
	private Tracer tracer;

	@Autowired
	private Source source;

	@GetMapping
	public String hi() {
		logger.info("GET /stock");
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
		this.logger.info("POST /stock/chaining/{}", testCase);
		for (Entry<String, String> entry : message.entrySet()) {
			this.logger.info("@RequestBody {}={}", entry.getKey(), entry.getValue());
		}
		this.logger.info("{}", tracer.toString());
		this.logger.info("{}", tracer.currentSpan().toString());

		String orderId = (String) message.get("orderId");

		// 1. 저장하기
		StockJpo jpo = new StockJpo(orderId);
		stockRepository.save(jpo);
		stockHistoryRepository.save(new StockHistoryJpo(jpo));

		Map<String, Object> event = new HashMap<>();
		event.put("channel-message", !"error-stock".equals(testCase) ? "재고 있음" : "재고 없음");

		// 2. 마이크로 서비스 호출하기
		String uri = this.uri + "/delivery/chaining/{testCase}";
		Map<String, String> params = new HashMap<>();
		params.put("orderId", orderId);
		params.put("testCase", testCase);
		params.put("userId", message.get("userId"));
		params.put("takeOutId", message.get("takeOutId"));
		params.put("paymentId", message.get("paymentId"));
		params.put("stockId", jpo.getUuid());
		this.logger.info("{}", uri);
		for (Entry<String, String> entry : params.entrySet()) {
			this.logger.info("{} {}={}", uri, entry.getKey(), entry.getValue());
		}

		try {
			if ("success".equals(testCase) || "error-delivery".equals(testCase)) {
				restTemplate.postForObject(uri, params, params.getClass(), params);
			}
		} catch (Exception e) {
			this.logger.info("{} {}", uri, e.getMessage());
			event.put("channel-message", "재고 오류");
			event.put("error", e.getMessage());
		}

		// 3. Event Sourcing : 저장후 보내기
		event.putAll(message);
		Message<?> eventMessage = MessageBuilder.withPayload(event).setHeader("type", "stock-service").build();
		this.logger.info("EventSourcing {}", event);
		this.logger.info("EventSourcing {}", eventMessage);
		source.output().send(eventMessage);
	}
}
