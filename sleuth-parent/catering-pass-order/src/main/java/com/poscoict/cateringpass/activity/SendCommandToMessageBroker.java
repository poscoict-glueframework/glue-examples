package com.poscoict.cateringpass.activity;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

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

	@Autowired
	private RestTemplate restTemplate;

	public String runActivity(GlueContext ctx) {
		// ActiveMQ Queues Name
		String uri = ctx.getProperty(DESTINATION, true);
		Map<String, Object> params = new HashMap<>();
		Object data = params;
		params.put("command", (String) ctx.get(ctx.getProperty(CHANNEL_CMD_KEY, true)));
		params.put("channel", ctx.getProperty(CHANNEL_NAME, true));

		//Map<String, Object> jmsMessage = new HashMap<>();
		//jmsMessage.put("channel", ctx.getProperty(CHANNEL_NAME, true));
		//jmsMessage.put("command", (String) ctx.get(ctx.getProperty(CHANNEL_CMD_KEY, true)));
		this.logger.info("URI : {}", uri);
		this.logger.info("command : {}", params.get("command"));
		//this.logger.info("채널 : {}", jmsMessage.get("channel"));

		int parameterCount = super.getParamCount(ctx.getProperty("param-count"));
		for (int i = 0; i < parameterCount; i++) {
			String param = ctx.getProperty(GlueActivityConstants.PREFIX_ACTIVITY_PROPERTY_PARAM + (i + 1));
			if (ctx.containsKey(param) && ctx.get(param) != null)
				params.put("extra-" + param, ctx.get(param));
		}

		// ActiveMQ(Message Broker)로 메시지보내기
		// jmsTemplate.convertAndSend(jmsDestination, jmsMessage);
		restTemplate.postForObject(uri, data, data.getClass(), params);

		ctx.put(super.getResultKey(ctx), params);
		return SUCCESS;
	}
}