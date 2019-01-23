package com.poscoict.cateringpass;

import java.util.Map;
import java.util.Optional;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.cloud.stream.messaging.Sink;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.handler.annotation.Headers;
import org.springframework.messaging.handler.annotation.Payload;

import com.poscoict.cateringpass.order.OrderHistoryJpo;
import com.poscoict.cateringpass.order.OrderHistoryRepository;
import com.poscoict.cateringpass.order.OrderJpo;
import com.poscoict.cateringpass.order.OrderRepository;

@EnableBinding(Sink.class)
public class OrderEventConsumer {
	private final Logger logger = LoggerFactory.getLogger(getClass());

	@Autowired
	private OrderRepository orderRepository;

	@Autowired
	private OrderHistoryRepository orderHistoryRepository;

	@StreamListener(target = Sink.INPUT) // , condition = "header['type']=='payment-service'")
	public void receivedPayment(@Payload Map<String, Object> event, @Headers MessageHeaders headers) {
		System.out.println("");
		System.out.println("");
		System.out.println("");
		System.out.println("");
		System.out.println("");
		this.logger.info("KAFKA /payment-service");
		for (Entry<String, Object> entry : event.entrySet()) {
			this.logger.info("@Payload {}={}", entry.getKey(), entry.getValue());
		}
		for (Entry<String, Object> entry : headers.entrySet()) {
			this.logger.info("@Headers {}={}", entry.getKey(), entry.getValue());
		}

		String orderId = (String) event.get("orderId");
		Optional<OrderJpo> value = this.orderRepository.findById(orderId);
		if (value.isPresent()) {
			OrderJpo orderJpo = value.get();
			orderJpo.setStatus((String) event.get("channel-message") + " Reply 수신");
			this.orderRepository.save(orderJpo);
			OrderHistoryJpo orderHistoryJpo = new OrderHistoryJpo(orderJpo);
			orderHistoryJpo.setReceivedMessage("" + event.entrySet());
			this.orderHistoryRepository.save(orderHistoryJpo);
		}
	}
}
