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
		Thread.sleep(2000);
		System.out.println("received");
		System.out.println(message.entrySet());
		if ("reply-channel".equals(message.get("channel"))) {

			Optional<OrderJpo> value = this.orderRepository.findById((String) message.get("orderId"));
			if (value.isPresent()) {
				OrderJpo orderJpo = value.get();
				orderJpo.setStatus((String) message.get("channel-message"));
				orderRepository.save(orderJpo);
				OrderHistoryJpo orderHistoryJpo = new OrderHistoryJpo(orderJpo);
				orderHistoryJpo.setReceivedMessage("" + message.entrySet());
				this.orderHistoryRepository.save(orderHistoryJpo);
			}

			this.logger.info("##### [Order Saga] Reply를 확인합니다. ");
			GlueContext ctx = new GlueDefaultContext("order-saga-service");
			ctx.putAll(message);
			ctx.put("channel-name", message.get("source-channel"));
			bizController.doAction(ctx);
			this.logger.info("#####");
			this.logger.info("");
			this.logger.info("");
			this.logger.info("");
		}
	}
}
