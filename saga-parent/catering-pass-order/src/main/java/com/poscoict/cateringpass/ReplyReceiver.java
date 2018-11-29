package com.poscoict.cateringpass;

import java.util.Map;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

import com.poscoict.cateringpass.order.OrderHistoryJpo;
import com.poscoict.cateringpass.order.OrderHistoryRepository;
import com.poscoict.cateringpass.order.OrderJpo;
import com.poscoict.cateringpass.order.OrderRepository;
import com.poscoict.glueframework.biz.control.GlueBizController;
import com.poscoict.glueframework.context.GlueContext;
import com.poscoict.glueframework.context.GlueDefaultContext;

@Component
public class ReplyReceiver {
	private final Logger logger = LoggerFactory.getLogger(getClass());
	@Autowired
	private GlueBizController bizController;
	@Autowired
	private OrderRepository orderRepository;
	@Autowired
	private OrderHistoryRepository orderHistoryRepository;

	@JmsListener(destination = "order-service")
	public void receiveReplyMessage(Map<String, Object> message) throws InterruptedException {
		this.logger.info("");
		this.logger.info("");
		this.logger.info("");
		this.logger.info("##### [Order Saga] #####");
		System.out.println("received : " + message.entrySet());
		Thread.sleep(2000);

		Optional<OrderJpo> value = this.orderRepository.findById((String) message.get("orderId"));
		if (value.isPresent()) {
			OrderJpo orderJpo = value.get();
			orderJpo.setStatus((String) message.get("channel-message") + " Reply 수신");
			orderRepository.save(orderJpo);
			OrderHistoryJpo orderHistoryJpo = new OrderHistoryJpo(orderJpo);
			orderHistoryJpo.setReceivedMessage("" + message.entrySet());
			this.orderHistoryRepository.save(orderHistoryJpo);

			GlueContext ctx = new GlueDefaultContext("order-saga-service");
			ctx.putAll(message);
			ctx.put("channel-name", message.get("source-channel"));
			bizController.doAction(ctx);

			if (ctx.containsKey("jms-message")) {
				@SuppressWarnings("unchecked")
				Map<String, Object> result = (Map<String, Object>) ctx.get("jms-message");
				orderJpo.setStatus(result.get("command") + " CMD 보냄");
				this.orderRepository.save(orderJpo);
				orderHistoryJpo = new OrderHistoryJpo(orderJpo);
				this.orderHistoryRepository.save(orderHistoryJpo);
			}
		}

		this.logger.info("##### [Order Saga] #####");
	}
}
