package com.poscoict.cateringpass.activity;

import java.util.HashMap;
import java.util.Map;

//import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;

import com.poscoict.glueframework.biz.activity.GlueActivity;
import com.poscoict.glueframework.biz.activity.GlueActivityConstants;
import com.poscoict.glueframework.context.GlueContext;

/*
<activity name="μs호출 : PaymentService" class="com.poscoict.cateringpass.activity.SendCommandToMessageBroker">
    <transition name="success" value="end"/>
    <property name="destination" value="payment-service"/>
    <property name="channel-name" value="payment-channel"/>
    <property name="channal-cmd-key" value="command"/>
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
	public static final String PARAM_COUNT = GlueActivityConstants.ACTIVITY_PROPERTY_PARAM_COUNT;
	public static final String DESTINATION = "destination";
	public static final String CHANNEL_NAME = "channel-name";
	public static final String CHANNEL_CMD_KEY = "channal-cmd-key";

	public String runActivity(GlueContext ctx) {
		// JmsTemplate jmsTemplate =
		// super.applicationContext.getBean(JmsTemplate.class);
		Map<String, Object> jmsMessage = new HashMap<>();
		// ActiveMQ Queues Name
		String jmsDestination = ctx.getProperty(DESTINATION, true);

		jmsMessage.put("channel", ctx.getProperty(CHANNEL_NAME, true));
		jmsMessage.put("command", (String) ctx.get(ctx.getProperty(CHANNEL_CMD_KEY, true)));
		this.logger.info("큐 : {}", jmsDestination);
		this.logger.info("채널 : {}", jmsMessage.get("channel"));
		this.logger.info("CMD : {}", jmsMessage.get("command"));

		int parameterCount = super.getParamCount(ctx.getProperty("param-count"));
		for (int i = 0; i < parameterCount; i++) {
			String param = ctx.getProperty(GlueActivityConstants.PREFIX_ACTIVITY_PROPERTY_PARAM + (i + 1));
			if (ctx.containsKey(param) && ctx.get(param) != null)
				jmsMessage.put("extra-" + param, ctx.get(param));
		}

		// ActiveMQ(Message Broker)로 메시지보내기
		// jmsTemplate.convertAndSend(jmsDestination, jmsMessage);

		ctx.put(super.getResultKey(ctx), jmsMessage);
		return SUCCESS;
	}
}