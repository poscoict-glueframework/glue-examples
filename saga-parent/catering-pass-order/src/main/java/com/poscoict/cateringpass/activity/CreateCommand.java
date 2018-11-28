package com.poscoict.cateringpass.activity;

import org.springframework.stereotype.Component;

import com.poscoict.glueframework.biz.activity.GlueActivity;
import com.poscoict.glueframework.context.GlueContext;

@Component
public class CreateCommand extends GlueActivity<GlueContext> {
	public String runActivity(GlueContext ctx) {
		this.logger.info("##### [Order Saga] 커맨드 생성 : {}", ctx.getProperty("command"));
		ctx.put("command", ctx.getProperty("command"));
		return SUCCESS;
	}
}