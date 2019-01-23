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

import com.poscoict.cateringpass.payment.PaymentHistoryJpo;
import com.poscoict.cateringpass.payment.PaymentHistoryRepository;
import com.poscoict.cateringpass.payment.PaymentJpo;
import com.poscoict.cateringpass.payment.PaymentRepository;

@EnableBinding(Sink.class)
public class OrderEventConsumer {
	private final Logger logger = LoggerFactory.getLogger(getClass());

	@Autowired
	private PaymentRepository paymentRepository;

	@Autowired
	private PaymentHistoryRepository paymentHistoryRepository;

	@StreamListener(target = Sink.INPUT) // , condition = "header['type']=='stock-service'")
	public void receivedStock(@Payload Map<String, Object> event, @Headers MessageHeaders headers) {
		System.out.println("");
		System.out.println("");
		System.out.println("");
		System.out.println("");
		System.out.println("");
		this.logger.info("KAFKA /stock-service");
		for (Entry<String, Object> entry : event.entrySet()) {
			this.logger.info("@Payload {}={}", entry.getKey(), entry.getValue());
		}
		for (Entry<String, Object> entry : headers.entrySet()) {
			this.logger.info("@Headers {}={}", entry.getKey(), entry.getValue());
		}

		String paymentId = (String) event.get("paymentId");
		Optional<PaymentJpo> value = this.paymentRepository.findById(paymentId);
		if (value.isPresent()) {
			PaymentJpo jpo = value.get();
			jpo.setStatus((String) event.get("channel-message") + " Reply 수신");
			this.paymentRepository.save(jpo);
			PaymentHistoryJpo historyJpo = new PaymentHistoryJpo(jpo);
			this.paymentHistoryRepository.save(historyJpo);
		}
	}
}
