package com.poscoict.cateringpass;

import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.cloud.stream.messaging.Sink;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.handler.annotation.Headers;
import org.springframework.messaging.handler.annotation.Payload;

import com.poscoict.cateringpass.stock.StockHistoryJpo;
import com.poscoict.cateringpass.stock.StockHistoryRepository;
import com.poscoict.cateringpass.stock.StockJpo;
import com.poscoict.cateringpass.stock.StockRepository;

@EnableBinding(Sink.class)
public class OrderEventConsumer {
	private final Logger logger = LoggerFactory.getLogger(getClass());

	@Autowired
	private StockRepository stockRepository;

	@Autowired
	private StockHistoryRepository stockHistoryRepository;

	@StreamListener(target = Sink.INPUT) // , condition = "header['type']=='delivery-service'")
	public void receivedDelivery(@Payload Map<String, Object> event, @Headers MessageHeaders headers) {
		System.out.println("");
		System.out.println("");
		System.out.println("");
		System.out.println("");
		System.out.println("");
		this.logger.info("KAFKA /delivery-service");
		for (Entry<String, Object> entry : event.entrySet()) {
			this.logger.info("@Payload {}={}", entry.getKey(), entry.getValue());
		}
		for (Entry<String, Object> entry : headers.entrySet()) {
			this.logger.info("@Headers {}={}", entry.getKey(), entry.getValue());
		}

		String stockId = (String) event.get("stockId");
		Optional<StockJpo> value = this.stockRepository.findById(stockId);
		if (value.isPresent()) {
			StockJpo jpo = value.get();
			jpo.setStatus((String) event.get("channel-message") + " Reply 수신");
			this.stockRepository.save(jpo);
			StockHistoryJpo historyJpo = new StockHistoryJpo(jpo);
			this.stockHistoryRepository.save(historyJpo);
		}
	}
}
