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

import com.poscoict.cateringpass.stock.StockHistoryJpo;
import com.poscoict.cateringpass.stock.StockHistoryRepository;
import com.poscoict.cateringpass.stock.StockJpo;
import com.poscoict.cateringpass.stock.StockRepository;

import brave.Tracer;

@RestController
@RequestMapping("/stock")
public class OrderCommnadController {
	private final Logger logger = LoggerFactory.getLogger(getClass());

	@Value("${catering.order.base-uri:http://localhost:9201}")
	private String uri;

	@Autowired
	private StockRepository stockRepository;

	@Autowired
	private StockHistoryRepository stockHistoryRepository;

	@Autowired
	private RestTemplate restTemplate;

	@Autowired
	private Tracer tracer;

	@PostMapping(path = "/{command}")
	public Object receiveMessageFromStockChannel(@PathVariable String command, @RequestBody Map<String, Object> message) {
		System.out.println("");
		System.out.println("");
		System.out.println("");
		System.out.println("");
		System.out.println("");
		this.logger.info("POST /stock/{}", command);
		for (Entry<String, Object> entry : message.entrySet()) {
			this.logger.info("@RequestBody {}={}", entry.getKey(), entry.getValue());
		}
		this.logger.info("{}", tracer.toString());
		this.logger.info("{}", tracer.currentSpan().toString());

		String channel = (String) message.get("channel");
		String testCase = (String) message.get("case");
		String stockId = null;
		Map<String, Object> msg = new HashMap<>();
		if ("준비".equals(command)) {
			StockJpo jpo = this.executeCommnd((String) message.get("orderId"));
			stockId = jpo.getUuid();
			if ("error-stock".equals(testCase)) {
				msg.put("channel-message", "오류:" + command);
			} else {
				msg.put("channel-message", "정상:" + command);
			}
			msg.put("stockId", stockId);
		} else if ("준비취소".equals(command)) {
			stockId = (String) message.get("extra-stockId");
			boolean rollback = this.executeRollbackCommnd(stockId, command);
			if (rollback) {
				msg.put("channel-message", "정상:" + command);// 결재성공, 결재실패
			}
		} else {
			this.logger.info("정의되지 않는 커맨드입니다.");
		}

		System.out.println("");
		if (!msg.isEmpty()) {
			this.beforeReply(stockId, msg);
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

	private StockJpo executeCommnd(String orderId) {
		StockJpo jpo = new StockJpo(orderId);
		stockRepository.save(jpo);
		stockHistoryRepository.save(new StockHistoryJpo(jpo));
		return jpo;
	}

	private boolean executeRollbackCommnd(String stockId, String command) {
		Optional<StockJpo> value = stockRepository.findById(stockId);
		if (value.isPresent()) {
			StockJpo jpo = value.get();
			jpo.setStatus("준비취소");
			stockRepository.save(jpo);
			stockHistoryRepository.save(new StockHistoryJpo(jpo));
			return true;
		}
		return false;
	}

	private void beforeReply(String stockId, Map<String, Object> msg) {
		Optional<StockJpo> value = stockRepository.findById(stockId);
		if (value.isPresent()) {
			StockJpo jpo = value.get();
			jpo.setStatus("" + msg.get("channel-message"));
			stockRepository.save(jpo);
			stockHistoryRepository.save(new StockHistoryJpo(jpo));
		}
	}
}
