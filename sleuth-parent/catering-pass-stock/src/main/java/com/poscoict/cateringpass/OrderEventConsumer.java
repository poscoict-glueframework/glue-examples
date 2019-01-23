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

import com.poscoict.cateringpass.stock.StockHistoryJpo;
import com.poscoict.cateringpass.stock.StockHistoryRepository;
import com.poscoict.cateringpass.stock.StockJpo;
import com.poscoict.cateringpass.stock.StockRepository;

@EnableBinding(Sink.class)
public class OrderEventConsumer {
	private final Logger logger = LoggerFactory.getLogger(getClass());

	@Value("${catering.stock.base-uri:http://localhost:9203}")
	private String uri;

	@Autowired
	private RestTemplate restTemplate;

	@Autowired
	private StockRepository stockRepository;

	@Autowired
	private StockHistoryRepository stockHistoryRepository;

	@StreamListener(target = Sink.INPUT) // , condition = "header['type']=='delivery-service'")
	public void receivedDelivery(@Payload Map<String, String> event, @Headers MessageHeaders headers) {
		System.out.println("");
		System.out.println("");
		System.out.println("");
		System.out.println("");
		System.out.println("");
		this.logger.info("KAFKA /delivery-service");
		for (Entry<String, String> entry : event.entrySet()) {
			this.logger.info("@Payload {}={}", entry.getKey(), entry.getValue());
		}
		for (Entry<String, Object> entry : headers.entrySet()) {
			this.logger.info("@Headers {}={}", entry.getKey(), entry.getValue());
		}

		String stockId = event.get("stockId");
		Optional<StockJpo> value = this.stockRepository.findById(stockId);
		if (value.isPresent()) {
			StockJpo jpo = value.get();
			jpo.setStatus(event.get("channel-message"));
			this.stockRepository.save(jpo);

			jpo.setStatus(event.get("channel-message") + "(KAFKA)");
			this.stockHistoryRepository.save(new StockHistoryJpo(jpo));
		}

		if (!"배송 가능".equals(event.get("channel-message"))) {
			// 1. 마이크로 서비스 호출하기
			String uri = this.uri + "/stock/rollback/{testCase}";
			Map<String, String> params = new HashMap<>();
			params.put("orderId", event.get("orderId"));
			params.put("testCase", "error-stock");
			params.put("userId", event.get("userId"));
			params.put("takeOutId", event.get("takeOutId"));
			params.put("paymentId", event.get("paymentId"));
			params.put("stockId", event.get("stockId"));
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
