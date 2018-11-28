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
		Thread.sleep(2000);
		this.logger.info("##### [Stock Service] #####");
		System.out.println("received");
		System.out.println(message.entrySet());
		String command = (String) message.get("command");
		String channel = (String) message.get("channel");
		String testCase = (String) message.get("extra-case");

		if ("준비".equals(command)) {
			this.logger.info("커맨드를 실행합니다. {}", command);
			String orderId = (String) message.get("extra-orderId");
			StockJpo jpo = new StockJpo(orderId);
			stockRepository.save(jpo);
			StockHistoryJpo historyJpo = new StockHistoryJpo(jpo);
			stockHistoryRepository.save(historyJpo);

			this.logger.info("Reply 메시지를 보냅니다.");
			Map<String, Object> msg = new HashMap<>();
			msg.put("channel", "reply-channel");
			msg.put("source-channel", channel);
			if ("error-stock".equals(testCase)) {
				msg.put("channel-message", "오류:준비");
			} else {
				msg.put("channel-message", "정상:준비");
			}
			for (String key : message.keySet()) {
				if (key.startsWith("extra-")) {
					msg.put(key.substring("extra-".length()), message.get(key));
				}
			}
			msg.put("stockId", jpo.getUuid());

			jmsTemplate.convertAndSend("order-service", msg);
			this.logger.info("Reply 메시지: {}", msg.entrySet());

		} else if ("준비취소".equals(command)) {
			this.logger.info("커맨드를 실행합니다. {}", command);
			String stockId = (String) message.get("extra-stockId");
			Optional<StockJpo> value = stockRepository.findById(stockId);
			if (value.isPresent()) {
				StockJpo jpo = value.get();
				jpo.setStatus("준비취소");
				stockRepository.save(jpo);
				StockHistoryJpo historyJpo = new StockHistoryJpo(jpo);
				stockHistoryRepository.save(historyJpo);

				this.logger.info("Reply 메시지를 보냅니다.");
				Map<String, Object> msg = new HashMap<>();
				msg.put("channel", "reply-channel");
				msg.put("source-channel", channel);
				msg.put("channel-message", "정상:준비취소");
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
