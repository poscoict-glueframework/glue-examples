package com.poscoict.cateringpass;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;

import com.poscoict.cateringpass.stock.StockHistoryJpo;
import com.poscoict.cateringpass.stock.StockHistoryRepository;
import com.poscoict.cateringpass.stock.StockJpo;
import com.poscoict.cateringpass.stock.StockRepository;

@Component
public class CommnadReceiver {
	private final Logger logger = LoggerFactory.getLogger(getClass());
	@Autowired
	private JmsTemplate jmsTemplate;
	@Autowired
	private StockRepository stockRepository;
	@Autowired
	private StockHistoryRepository stockHistoryRepository;

	@JmsListener(destination = "stock-service")
	public void receiveMessageFromOrderChannel(Map<String, Object> message) throws InterruptedException {
		this.logger.info("");
		this.logger.info("");
		this.logger.info("");
		this.logger.info("##### [Stock Service] #####");
		System.out.println("received : " + message.entrySet());
		Thread.sleep(2000);

		String command = (String) message.get("command");
		String channel = (String) message.get("channel");
		String testCase = (String) message.get("extra-case");
		String stockId = null;
		Map<String, Object> msg = new HashMap<>();
		this.logger.info("커맨드를 실행합니다. {}", command);
		if ("준비".equals(command)) {
			StockJpo jpo = this.executeCommnd((String) message.get("extra-orderId"));
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

		if (!msg.isEmpty()) {
			this.beforeReply(stockId, msg);
			msg.put("channel", "reply-channel");
			msg.put("source-channel", channel);
			for (Entry<String, Object> entry : message.entrySet()) {
				if (entry.getKey().startsWith("extra-") && entry.getValue() != null) {
					msg.put(entry.getKey().substring("extra-".length()), entry.getValue());
				}
			}
			this.logger.info("Reply 메시지를 보냅니다.");
			jmsTemplate.convertAndSend("order-service", msg);
			this.logger.info("Reply 메시지: {}", msg.entrySet());
		}
		this.logger.info("##### [Payment Service] #####");
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
