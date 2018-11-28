package com.poscoict.cateringpass.activity;

import java.util.HashMap;
import java.util.Map;

import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;

import com.poscoict.glueframework.biz.activity.GlueActivity;
import com.poscoict.glueframework.biz.activity.GlueActivityConstants;
import com.poscoict.glueframework.context.GlueContext;

/*
<activity name="μs호출 : PaymentService" class="com.poscoict.cateringpass.activity.SendCommandToMessageBroker">
    <transition name="success" value="end"/>
    <property name="destination" value="payment-service"/>
    <property name="channel-name" value="payment-channel"/>
    <property name="message-key" value="command"/>
    <property name="param-count" value="3"/>
    <property name="param1" value="userId"/>
    <property name="param2" value="takeOutId"/>
    <property name="param3" value="orderId"/>
    <property name="result-key" value="message"/>
</activity>
 */
@Component
public class SendCommandToMessageBroker extends GlueActivity<GlueContext> {
	/** property : param-count */
	protected static final String PARAM_COUNT = GlueActivityConstants.ACTIVITY_PROPERTY_PARAM_COUNT;

	public String runActivity(GlueContext ctx) {
		// ActiveMQ Queues Name
		String destination = ctx.getProperty("destination", true);

		String channelName = ctx.getProperty("channel-name", true);
		String command = (String) ctx.get(ctx.getProperty("message-key", true));
		this.logger.info("##### [Order Saga] {} - 채널 : {}, CMD : {}", destination, channelName, command);

		Map<String, Object> message = new HashMap<>();
		message.put("channel", channelName);
		message.put("command", command);
		int parameterCount = super.getParamCount(ctx.getProperty("param-count"));
		String param = null;
		for (int i = 0; i < parameterCount; i++) {
			param = ctx.getProperty(GlueActivityConstants.PREFIX_ACTIVITY_PROPERTY_PARAM + (i + 1));
			message.put("extra-" + param, ctx.get(param));
		}

		JmsTemplate jmsTemplate = super.applicationContext.getBean(JmsTemplate.class);
		jmsTemplate.convertAndSend(destination, message);

		ctx.put(super.getResultKey(ctx), message);
		return SUCCESS;
	}
}